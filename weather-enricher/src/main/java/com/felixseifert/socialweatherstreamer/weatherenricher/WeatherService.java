package com.felixseifert.socialweatherstreamer.weatherenricher;

import com.felixseifert.socialweatherstreamer.weatherenricher.model.WeatherInformation;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class WeatherService {

  private final LoadingCache<String, Optional<WeatherInformation>> weatherCache;

  public WeatherService(WeatherAPIClient weatherAPIClient) {
    this.weatherCache =
        Caffeine.newBuilder()
            .maximumSize(10000L)
            .refreshAfterWrite(30, TimeUnit.MINUTES)
            .build(weatherAPIClient::getCurrentWeatherAtLocation);
  }

  public Optional<WeatherInformation> getCurrentWeatherAtLocation(String locationName) {
    return weatherCache.get(locationName);
  }
}
