package com.felixseifert.socialweatherstreamer.analyticsindicator.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableTweet.class)
@JsonDeserialize(as = ImmutableTweet.class)
public abstract class Tweet {

  public abstract Long id();

  public abstract String text();

  public abstract Double correlation();
}
