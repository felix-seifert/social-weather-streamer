package com.felixseifert.socialweatherstreamer.analyticsindicator.views.tweetcorrelation;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public abstract class TweetCorrelationView extends HorizontalLayout {

  public abstract void updateCorrelation(final double correlation);
}
