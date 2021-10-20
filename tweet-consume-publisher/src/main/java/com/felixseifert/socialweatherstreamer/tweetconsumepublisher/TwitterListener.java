package com.felixseifert.socialweatherstreamer.tweetconsumepublisher;

import io.quarkus.runtime.Startup;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
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

    final Stream<Tweet> tweetStream = twitterClient.connectStream(1024);
    consumeStream(tweetStream);
  }

  private void consumeStream(final Stream<Tweet> tweetStream) {
    tweetStream.forEach(LOGGER::info);
  }
}
