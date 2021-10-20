package com.felixseifert.socialweatherstreamer.tweetconsumepublisher;

import io.quarkus.runtime.Startup;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

@Startup
@ApplicationScoped
public class TwitterListener {

  private static final Logger LOGGER = Logger.getLogger(TwitterListener.class);

  private final TwitterClient twitterClient;

  public TwitterListener(TwitterClient twitterClient) {
    this.twitterClient = twitterClient;
  }

  @PostConstruct
  public void startListenerAfterConstruction() throws IOException, URISyntaxException {
    Map<String, String> rules =
        Map.of(
            "(entity:\\\"New York\\\" OR #NewYork OR \\\"New York\\\") lang:en -is:retweet",
            "New York");

    final List<String> existingRules = twitterClient.getExistingRules();
    if (!existingRules.isEmpty()) twitterClient.deleteRules(existingRules);
    twitterClient.createRules(rules);

    BufferedReader streamReader = twitterClient.connectStream(1024).orElseThrow();
    consumeStream(streamReader);
  }

  private void consumeStream(BufferedReader streamReader) {
    streamReader.lines().forEach(LOGGER::info);
  }
}
