package com.felixseifert.socialweatherstreamer.weatherenricher.util;

import com.felixseifert.socialweatherstreamer.weatherenricher.model.Tweet;
import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;

public class TweetDeserializer extends ObjectMapperDeserializer<Tweet> {

  public TweetDeserializer() {
    super(Tweet.class);
  }
}
