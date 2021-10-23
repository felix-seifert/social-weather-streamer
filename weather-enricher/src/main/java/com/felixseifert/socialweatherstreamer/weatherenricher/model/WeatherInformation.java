package com.felixseifert.socialweatherstreamer.weatherenricher.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;
import org.json.JSONObject;

@Value.Immutable
@JsonSerialize(as = ImmutableWeatherInformation.class)
@JsonDeserialize(as = ImmutableWeatherInformation.class)
public abstract class WeatherInformation {

  @JsonProperty("last_updated")
  public abstract String lastUpdated();

  @JsonProperty("temp_c")
  public abstract Double temperatureCelsius();

  @JsonProperty("precip_mm")
  public abstract Double precipitationMm();

  public abstract Integer humidity();

  @JsonProperty("cloud")
  public abstract Integer cloudCoverage();

  @JsonProperty("feelslike_c")
  public abstract Double feelsLikeCelsius();

  @JsonProperty("is_day")
  public abstract Integer isDay();  // 1 if day, 0 if night.

  public static WeatherInformation parseFromJson(JSONObject jsonObject) {
    try {
      return new ObjectMapper()
          .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
          .readValue(jsonObject.toString(), WeatherInformation.class);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    return ImmutableWeatherInformation.builder().build(); // Empty build will fail.
  }
}
