package com.felixseifert.socialweatherstreamer.analyticsindicator.service;

import com.felixseifert.socialweatherstreamer.analyticsindicator.model.Tweet;
import com.felixseifert.socialweatherstreamer.analyticsindicator.views.tweetcorrelation.TweetCorrelationView;
import com.vaadin.flow.component.UI;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class TweetServiceImpl implements TweetService {

  private static final Logger LOGGER = LoggerFactory.getLogger(TweetServiceImpl.class);

  private final Map<UI, TweetCorrelationView> views = new HashMap<>();

  private int numberOfTweetsForCorrelationAverage = 0;

  private double currentCorrelation = 0;

  @Override
  public void sendDataToUi(final Tweet tweet) {
    LOGGER.info("Inform UIs of new correlation {}", currentCorrelation);
    views
        .keySet()
        .forEach(ui -> ui.access(() -> views.get(ui).updateCorrelation(currentCorrelation)));
  }

  @Override
  public synchronized void updateCorrelation(final double newCorrelation) {
    currentCorrelation =
        (currentCorrelation * numberOfTweetsForCorrelationAverage + newCorrelation)
            / (numberOfTweetsForCorrelationAverage + 1);
    numberOfTweetsForCorrelationAverage += 1;
    LOGGER.info("Updated correlation average to {}", currentCorrelation);
  }

  @Override
  public void register(final TweetCorrelationView tweetCorrelationView) {
    tweetCorrelationView.getUI().ifPresent(ui -> views.put(ui, tweetCorrelationView));
  }

  @Override
  public void unregister(final TweetCorrelationView tweetCorrelationView) {
    tweetCorrelationView.getUI().ifPresent(views::remove);
  }
}
