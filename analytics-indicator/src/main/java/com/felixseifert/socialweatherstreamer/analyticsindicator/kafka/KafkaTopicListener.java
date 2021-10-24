package com.felixseifert.socialweatherstreamer.analyticsindicator.kafka;

import com.felixseifert.socialweatherstreamer.analyticsindicator.model.Tweet;
import com.felixseifert.socialweatherstreamer.analyticsindicator.service.TweetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaTopicListener {

  private static final Logger LOGGER = LoggerFactory.getLogger(KafkaTopicListener.class);

  private final TweetService tweetService;

  public KafkaTopicListener(TweetService tweetService) {
    this.tweetService = tweetService;
  }

  @KafkaListener(
      id = "${analytics-indicator.kafka.correlation.id}",
      topics = "${analytics-indicator.kafka.correlation.topic}",
      containerFactory = "${analytics-indicator.kafka.correlation.container-factory}")
  public void listenToSensorsTopic(final Tweet tweet) {
    LOGGER.info("Received Tweet: {}", tweet);
    tweetService.updateCorrelation(tweet.correlation());
    tweetService.sendDataToUi(tweet);
  }
}
