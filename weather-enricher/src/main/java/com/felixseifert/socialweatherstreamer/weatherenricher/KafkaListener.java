package com.felixseifert.socialweatherstreamer.weatherenricher;

import com.felixseifert.socialweatherstreamer.weatherenricher.model.ImmutableTweet;
import com.felixseifert.socialweatherstreamer.weatherenricher.model.Tweet;
import com.felixseifert.socialweatherstreamer.weatherenricher.model.WeatherInformation;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Optional;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.jboss.logging.Logger;

@ApplicationScoped
public class KafkaListener {

  private static final Logger LOGGER = Logger.getLogger(KafkaListener.class);

  @Inject WeatherAPIClient weatherAPIClient;

  @Incoming("tweets")
  @Outgoing("tweets-enriched")
  public Tweet enrichTweetsWithWeatherAtPostLocation(Tweet tweet)
      throws URISyntaxException, IOException {
    final String locationNameOfTweet = tweet.geoInformation().fullName();
    final Optional<WeatherInformation> weatherInformationOfLocation =
        weatherAPIClient.getCurrentWeatherAtLocation(locationNameOfTweet);
    final Tweet enrichedTweet =
        ImmutableTweet.copyOf(tweet).withWeatherInformation(weatherInformationOfLocation);
    LOGGER.infov("Publish enriched Tweet on topic 'tweets-enriched': {0}", enrichedTweet);
    return enrichedTweet;
  }
}
