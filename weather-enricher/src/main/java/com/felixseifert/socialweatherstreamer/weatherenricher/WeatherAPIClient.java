package com.felixseifert.socialweatherstreamer.weatherenricher;

import com.felixseifert.socialweatherstreamer.weatherenricher.model.WeatherInformation;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Optional;
import javax.enterprise.context.ApplicationScoped;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;
import org.json.JSONObject;

@ApplicationScoped
public class WeatherAPIClient {

  private static final Logger LOGGER = Logger.getLogger(WeatherAPIClient.class);

  @ConfigProperty(name = "weather-api.url")
  String weatherApiUrl;

  @ConfigProperty(name = "weather-api.token")
  String weatherApiToken;

  public Optional<WeatherInformation> getCurrentWeatherAtLocation(final String locationName)
      throws URISyntaxException, IOException {
    LOGGER.infov("Retrieve weather information for {0}", locationName);
    final HttpClient httpClient = getHttpClientWithStandardCookieSpecs();
    final HttpGet httpGet = createHttpGet(locationName);
    final HttpResponse response = httpClient.execute(httpGet);
    final Optional<HttpEntity> entityOptional = Optional.ofNullable(response.getEntity());
    return entityOptional
        .map(this::httpEntityToString)
        .map(JSONObject::new)
        .map(jsonObject -> jsonObject.getJSONObject("current"))
        .map(WeatherInformation::parseFromJson);
  }

  private HttpGet createHttpGet(final String locationName) throws URISyntaxException {
    final URIBuilder uriBuilder = new URIBuilder(weatherApiUrl);
    uriBuilder.addParameter("key", weatherApiToken);
    uriBuilder.addParameter("q", locationName);
    return new HttpGet(uriBuilder.build());
  }

  private HttpClient getHttpClientWithStandardCookieSpecs() {
    return HttpClients.custom()
        .setDefaultRequestConfig(RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build())
        .build();
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
