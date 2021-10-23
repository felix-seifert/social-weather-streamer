package com.felixseifert.socialweatherstreamer.weatherenricher;

import com.felixseifert.socialweatherstreamer.weatherenricher.model.ImmutableTweet;
import com.felixseifert.socialweatherstreamer.weatherenricher.model.Tweet;
import com.felixseifert.socialweatherstreamer.weatherenricher.model.WeatherInformation;
import java.util.Optional;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.jboss.logging.Logger;

@ApplicationScoped
public class KafkaListener {

  private static final Logger LOGGER = Logger.getLogger(KafkaListener.class);

  @Inject WeatherService weatherService;

  @Incoming("tweets")
  @Outgoing("tweets-enriched")
  public Tweet enrichTweetsWithWeatherAtPostLocation(Tweet tweet) {
    final String location = getLocation(tweet);
    final Optional<WeatherInformation> weatherInformationOfLocation =
        weatherService.getCurrentWeatherAtLocation(location);
    final Tweet enrichedTweet = addWeatherInformationToTweet(tweet, weatherInformationOfLocation);
    LOGGER.infov("Publish enriched Tweet on topic 'tweets-enriched': {0}", enrichedTweet);
    return enrichedTweet;
  }

  private String getLocation(final Tweet tweet) {
    return tweet.geoInformation().fullName() + ", " + tweet.geoInformation().country();
  }

  private Tweet addWeatherInformationToTweet(
      final Tweet tweet, final Optional<WeatherInformation> weatherInformationOfLocation) {
    return ImmutableTweet.copyOf(tweet).withWeatherInformation(weatherInformationOfLocation);
  }
}
