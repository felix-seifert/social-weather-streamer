package com.felixseifert.socialweatherstreamer.analyticsindicator.service;

import com.felixseifert.socialweatherstreamer.analyticsindicator.model.Tweet;
import com.felixseifert.socialweatherstreamer.analyticsindicator.views.tweetcorrelation.TweetCorrelationView;

public interface TweetService {

  void sendDataToUi(final Tweet tweet);

  void updateCorrelation(final double correlation);

  void register(final TweetCorrelationView tweetCorrelationView);

  void unregister(final TweetCorrelationView tweetCorrelationView);
}
