package com.felixseifert.socialweatherstreamer.tweetconsumepublisher;

import io.quarkus.runtime.Startup;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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
    final Map<String, String> rules =
        Stream.of(
                "New York",
                "New York City",
                "San Francisco",
                "Stockholm",
                "Sydney",
                "Barcelona",
                "Berlin",
                "Singapore",
                "Tokyo")
            .collect(Collectors.toMap(this::createRuleForCity, city -> city));

    final List<String> existingRules = twitterClient.getExistingRules();
    if (!existingRules.isEmpty()) twitterClient.deleteRules(existingRules);
    twitterClient.createRules(rules);

    final Stream<Tweet> tweetStream = twitterClient.connectStream(1024);
    consumeStream(tweetStream);
  }

  private void consumeStream(final Stream<Tweet> tweetStream) {
    tweetStream
        .parallel()
        .filter(tweet -> tweet.geoInformation().isPresent())
        .forEach(LOGGER::info);
  }

  private String createRuleForCity(final String city) {
    final String cityWithoutSpaces = city.replaceAll("\\s+", "");
    return String.format(
        "(entity:\\\"%s\\\" OR #%s OR \\\"%s\\\") lang:en -is:retweet",
        city, cityWithoutSpaces, city);
  }
}
