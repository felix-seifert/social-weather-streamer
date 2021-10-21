package com.felixseifert.socialweatherstreamer.tweetconsumepublisher.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.Optional;
import javax.validation.constraints.NotBlank;
import org.immutables.value.Value;
import org.json.JSONObject;

@Value.Immutable
@JsonSerialize(as = ImmutableTweet.class)
public abstract class Tweet {

  public abstract Long id();

  public abstract String text();

  public abstract String createdAt();

  public abstract Optional<GeoInformation> geoInformation();

  public static Tweet parseJsonLineFromTwitter(@NotBlank final String jsonLine) {
    final JSONObject lineObject = new JSONObject(jsonLine);

    final JSONObject dataObject = lineObject.getJSONObject("data");
    final long id = dataObject.getLong("id");
    final String text = dataObject.getString("text");
    final String createdAtString = dataObject.getString("created_at");

    final Optional<GeoInformation> geoInformation = getGeoInformationIfPresent(lineObject);

    return ImmutableTweet.builder()
        .id(id)
        .text(text)
        .createdAt(createdAtString)
        .geoInformation(geoInformation)
        .build();
  }

  private static Optional<GeoInformation> getGeoInformationIfPresent(final JSONObject lineObject) {
    return lineObject.getJSONObject("data").getJSONObject("geo").isEmpty()
        ? Optional.empty()
        : Optional.of(
            GeoInformation.parseJsonObjectFromTwitter(
                lineObject.getJSONObject("includes").getJSONArray("places").getJSONObject(0)));
  }
}
