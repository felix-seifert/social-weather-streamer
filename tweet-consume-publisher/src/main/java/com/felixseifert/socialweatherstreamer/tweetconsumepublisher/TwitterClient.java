package com.felixseifert.socialweatherstreamer.tweetconsumepublisher;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
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

@ApplicationScoped
public class TwitterClient {

  private static final Logger LOGGER = Logger.getLogger(TwitterClient.class);

  @ConfigProperty(name = "twitter.url.stream")
  String twitterUrlStream;

  @ConfigProperty(name = "twitter.url.stream.rule")
  String twitterUrlStreamRules;

  @ConfigProperty(name = "twitter.bearer-token")
  String twitterBearerToken;

  public Optional<BufferedReader> connectStream() throws URISyntaxException, IOException {
    final HttpClient httpClient = getHttpClientWithStandardCookieSpecs();
    final HttpGet httpGet = createHttpGetWithHeader(twitterUrlStream);
    final HttpResponse response = httpClient.execute(httpGet);
    final Optional<HttpEntity> entityOptional = Optional.ofNullable(response.getEntity());
    return getBufferedReaderFromEntity(entityOptional);
  }

  private Optional<BufferedReader> getBufferedReaderFromEntity(
      final Optional<HttpEntity> entityOptional) {
    return entityOptional
        .map(this::getReader)
        .map(InputStreamReader::new)
        .map(inputStreamReader -> new BufferedReader(inputStreamReader, 1024));
  }

  private InputStream getReader(final HttpEntity httpEntity) {
    try {
      return httpEntity.getContent();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
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

  public List<String> getExistingRules() throws URISyntaxException, IOException {
    final HttpClient httpClient = getHttpClientWithStandardCookieSpecs();
    final HttpGet httpGet = createHttpGetWithHeader(twitterUrlStreamRules);
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

  public void deleteRules(final List<String> existingRules) throws IOException, URISyntaxException {
    final HttpClient httpClient = getHttpClientWithStandardCookieSpecs();
    final HttpPost httpPost = createHttpPostWithHeaderAndBodyToDelete(existingRules);
    final HttpResponse response = httpClient.execute(httpPost);
    final Optional<HttpEntity> entityOptional = Optional.ofNullable(response.getEntity());
    entityOptional.ifPresent(entity -> LOGGER.info(httpEntityToString(entity)));
  }

  public void createRules(final Map<String, String> rules) throws URISyntaxException, IOException {
    final HttpClient httpClient = getHttpClientWithStandardCookieSpecs();
    final HttpPost httpPost = createHttpPostWithHeaderAndBodyToAdd(rules);
    final HttpResponse response = httpClient.execute(httpPost);
    final Optional<HttpEntity> entityOptional = Optional.ofNullable(response.getEntity());
    entityOptional.ifPresent(entity -> LOGGER.info(httpEntityToString(entity)));
  }

  private HttpGet createHttpGetWithHeader(String url) throws URISyntaxException {
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

  private HttpPost createHttpPostWithHeaderAndBodyToAdd(final Map<String, String> rules)
      throws URISyntaxException, UnsupportedEncodingException {
    final HttpPost httpPost = createHttpPostWithHeader();
    final StringEntity body = new StringEntity(getFormattedAddString(rules));
    httpPost.setEntity(body);
    return httpPost;
  }

  private HttpPost createHttpPostWithHeader() throws URISyntaxException {
    final URIBuilder uriBuilder = new URIBuilder(twitterUrlStreamRules);
    final HttpPost httpPost = new HttpPost(uriBuilder.build());
    httpPost.setHeader("Authorization", String.format("Bearer %s", twitterBearerToken));
    httpPost.setHeader("content-type", "application/json");
    return httpPost;
  }

  private String getFormattedDeleteString(List<String> ids) {
    final StringBuilder stringBuilder = new StringBuilder();
    ids.stream().map(id -> "\"" + id + "\"" + ",").forEach(stringBuilder::append);
    final String result = stringBuilder.toString();
    return String.format(
        "{ \"delete\": { \"ids\": [%s]}}", result.substring(0, result.length() - 1));
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
