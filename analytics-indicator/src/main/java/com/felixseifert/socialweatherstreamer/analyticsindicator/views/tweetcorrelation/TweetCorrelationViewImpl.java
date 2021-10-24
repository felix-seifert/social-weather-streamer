package com.felixseifert.socialweatherstreamer.analyticsindicator.views.tweetcorrelation;

import com.felixseifert.socialweatherstreamer.analyticsindicator.service.TweetService;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import com.vaadin.flow.theme.Theme;

@Theme(themeFolder = "socialweatherstreamer")
@PageTitle("Tweet Correlation")
@Route(value = "")
@SpringComponent
@UIScope
public class TweetCorrelationViewImpl extends TweetCorrelationView {

  private final TweetService tweetService;

  private final Label label =
      new Label("Correlation between Tweet sentiment and temperature (click button to refresh)");

  private final Button correlationButton = new Button();

  public TweetCorrelationViewImpl(final TweetService tweetService) {
    this.tweetService = tweetService;

    correlationButton.setText("0");

    setMargin(true);
    add(label, correlationButton);
    setVerticalComponentAlignment(Alignment.END, correlationButton);
  }

  @Override
  public void updateCorrelation(final double correlation) {
    correlationButton.setText(String.valueOf(correlation));
    setCorrelationButtonColor(correlation);
  }

  private void setCorrelationButtonColor(final double correlation) {
    correlationButton.removeThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_ERROR);
    if (correlation == 0) {
      return;
    }
    if (correlation < 0) {
      correlationButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
      return;
    }
    correlationButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
  }

  @Override
  protected void onAttach(AttachEvent attachEvent) {
    tweetService.register(this);
  }

  @Override
  protected void onDetach(DetachEvent detachEvent) {
    tweetService.unregister(this);
  }
}
