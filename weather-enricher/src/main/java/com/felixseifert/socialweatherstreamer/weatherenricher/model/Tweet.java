package com.felixseifert.socialweatherstreamer.weatherenricher.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.Optional;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableTweet.class)
@JsonDeserialize(as = ImmutableTweet.class)
public abstract class Tweet {

  public abstract Long id();

  public abstract String text();

  public abstract String createdAt();

  public abstract GeoInformation geoInformation();

  public abstract Optional<WeatherInformation> weatherInformation();
}
