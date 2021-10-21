package com.felixseifert.socialweatherstreamer.tweetconsumepublisher.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.immutables.value.Value;
import org.json.JSONArray;
import org.json.JSONObject;

@Value.Immutable
@JsonSerialize(as = ImmutableGeoInformation.class)
public abstract class GeoInformation {

  public abstract String id();

  public abstract String fullName();

  public abstract String country();

  public abstract String countryCode();

  public abstract List<Double> boxCoordinates();

  public abstract String type();

  public static GeoInformation parseJsonObjectFromTwitter(final JSONObject jsonObject) {
    final String id = jsonObject.getString("id");
    final String fullName = jsonObject.getString("full_name");
    final String country = jsonObject.getString("country");
    final String countryCode = jsonObject.getString("country_code");
    final String type = jsonObject.getString("place_type");

    final JSONArray coordinatesArray = jsonObject.getJSONObject("geo").getJSONArray("bbox");
    final List<Double> boxCoordinates =
        IntStream.range(0, coordinatesArray.length())
            .mapToObj(coordinatesArray::getDouble)
            .collect(Collectors.toList());

    return ImmutableGeoInformation.builder()
        .id(id)
        .fullName(fullName)
        .country(country)
        .countryCode(countryCode)
        .boxCoordinates(boxCoordinates)
        .type(type)
        .build();
  }
}
