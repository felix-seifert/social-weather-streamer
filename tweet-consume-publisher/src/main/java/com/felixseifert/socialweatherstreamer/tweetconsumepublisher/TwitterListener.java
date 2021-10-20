package com.felixseifert.socialweatherstreamer.tweetconsumepublisher;

import io.quarkus.runtime.Startup;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

@Startup
@ApplicationScoped
public class TwitterListener {

  private static final Logger LOGGER = Logger.getLogger(TwitterListener.class);

  @ConfigProperty(name = "twitter.url.stream")
  String twitterUrlStream;

  @ConfigProperty(name = "twitter.url.stream.rule")
  String twitterUrlStreamRules;

  @ConfigProperty(name = "twitter.bearer-token")
  String twitterBearerToken;

  @PostConstruct
  public void startListenerAfterConstruction() throws IOException, URISyntaxException {
    Map<String, String> rules =
        Map.of(
            "(entity:\\\"New York\\\" OR #NewYork OR \\\"New York\\\") lang:en -is:retweet",
            "New York");

    final List<String> existingRules = getExistingRules();
    if (!existingRules.isEmpty()) deleteRules(existingRules);
    createRules(rules);

    connectStream();
  }

  private void connectStream() throws URISyntaxException, IOException {
    final HttpClient httpClient = getHttpClientWithStandardCookieSpecs();
    final HttpGet httpGet = createHttpGetWithToken(twitterUrlStream);
    final HttpResponse response = httpClient.execute(httpGet);
    final Optional<HttpEntity> entityOptional = Optional.ofNullable(response.getEntity());
    entityOptional.ifPresent(this::readIncomingStream);
  }

  private void readIncomingStream(HttpEntity entity) {
    try {
      final InputStreamReader reader = new InputStreamReader((entity.getContent()));
      final BufferedReader bufferedReader = new BufferedReader(reader, 1024);
      String line = bufferedReader.readLine();
      while (line != null) {
        System.out.println(line);
        line = bufferedReader.readLine();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private List<String> getExistingRules() throws URISyntaxException, IOException {
    final HttpClient httpClient = getHttpClientWithStandardCookieSpecs();
    final HttpGet httpGet = createHttpGetWithToken(twitterUrlStreamRules);
    final HttpResponse response = httpClient.execute(httpGet);
    final Optional<HttpEntity> entityOptional = Optional.ofNullable(response.getEntity());
    return parseEntityToGetExistingRules(entityOptional);
  }

  private List<String> parseEntityToGetExistingRules(final Optional<HttpEntity> entityOptional) {
    return entityOptional.stream()
        .map(e -> new JSONObject(httpEntityToString(e)))
        .filter(jsonObject -> jsonObject.length() > 1)
        .map(jsonObject -> jsonObject.getJSONArray("data"))
        .flatMap(this::convertJsonArrayToStream)
        .map(jsonObject -> jsonObject.getString("id"))
        .collect(Collectors.toList());
  }

  private void deleteRules(final List<String> existingRules)
      throws IOException, URISyntaxException {
    final HttpClient httpClient = getHttpClientWithStandardCookieSpecs();
    final HttpPost httpPost = createHttpPostWithHeaderAndBodyToDelete(existingRules);
    final HttpResponse response = httpClient.execute(httpPost);
    final Optional<HttpEntity> entityOptional = Optional.ofNullable(response.getEntity());
    entityOptional.ifPresent(entity -> LOGGER.info(httpEntityToString(entity)));
  }

  private void createRules(final Map<String, String> rules) throws URISyntaxException, IOException {
    final HttpClient httpClient = getHttpClientWithStandardCookieSpecs();
    final HttpPost httpPost = createHttpPostWithHeaderAndBodyToAdd(rules);
    final HttpResponse response = httpClient.execute(httpPost);
    final Optional<HttpEntity> entityOptional = Optional.ofNullable(response.getEntity());
    entityOptional.ifPresent(entity -> LOGGER.info(httpEntityToString(entity)));
  }

  private HttpGet createHttpGetWithToken(String url) throws URISyntaxException {
    final URIBuilder uriBuilder = new URIBuilder(url);
    final HttpGet httpGet = new HttpGet(uriBuilder.build());
    httpGet.setHeader("Authorization", String.format("Bearer %s", twitterBearerToken));
    httpGet.setHeader("content-type", "application/json");
    return httpGet;
  }

  private HttpPost createHttpPostWithHeaderAndBodyToDelete(final List<String> existingRules)
      throws URISyntaxException, UnsupportedEncodingException {
    final HttpPost httpPost = createHttpPostWithHeader();
    final StringEntity body = new StringEntity(getFormattedDeleteString(existingRules));
    httpPost.setEntity(body);
    return httpPost;
  }

  private String getFormattedDeleteString(List<String> ids) {
    final StringBuilder stringBuilder = new StringBuilder();
    ids.stream().map(id -> "\"" + id + "\"" + ",").forEach(stringBuilder::append);
    final String result = stringBuilder.toString();
    return String.format(
        "{ \"delete\": { \"ids\": [%s]}}", result.substring(0, result.length() - 1));
  }

  private HttpPost createHttpPostWithHeaderAndBodyToAdd(final Map<String, String> rules)
      throws URISyntaxException, UnsupportedEncodingException {
    final HttpPost httpPost = createHttpPostWithHeader();
    final StringEntity body = new StringEntity(getFormattedAddString(rules));
    httpPost.setEntity(body);
    return httpPost;
  }

  private String getFormattedAddString(final Map<String, String> rules) {
    final StringBuilder stringBuilder = new StringBuilder();
    rules.entrySet().stream()
        .map(
            entry ->
                "{\"value\": \""
                    + entry.getKey()
                    + "\", \"tag\": \""
                    + entry.getValue()
                    + "\"}"
                    + ",")
        .forEach(stringBuilder::append);
    final String result = stringBuilder.toString();
    return String.format("{\"add\": [%s]}", result.substring(0, result.length() - 1));
  }

  private HttpPost createHttpPostWithHeader() throws URISyntaxException {
    final URIBuilder uriBuilder = new URIBuilder(twitterUrlStreamRules);
    final HttpPost httpPost = new HttpPost(uriBuilder.build());
    httpPost.setHeader("Authorization", String.format("Bearer %s", twitterBearerToken));
    httpPost.setHeader("content-type", "application/json");
    return httpPost;
  }

  private HttpClient getHttpClientWithStandardCookieSpecs() {
    return HttpClients.custom()
        .setDefaultRequestConfig(RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build())
        .build();
  }

  private Stream<JSONObject> convertJsonArrayToStream(final JSONArray jsonArray) {
    return IntStream.range(0, jsonArray.length()).mapToObj(jsonArray::getJSONObject);
  }

  private String httpEntityToString(final HttpEntity entity) {
    try {
      return EntityUtils.toString(entity, "UTF-8");
    } catch (IOException e) {
      e.printStackTrace();
    }
    return "";
  }
}
