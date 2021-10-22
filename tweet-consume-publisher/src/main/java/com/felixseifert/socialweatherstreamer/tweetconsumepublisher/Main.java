package com.felixseifert.socialweatherstreamer.tweetconsumepublisher;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import javax.inject.Inject;

@QuarkusMain
public class Main {
  public static void main(String... args) {
    Quarkus.run(MyApp.class, args);
  }

  public static class MyApp implements QuarkusApplication {

    @Inject
    TwitterListener twitterListener;

    @Override
    public int run(String... args) throws Exception {
      twitterListener.startListener();
      Quarkus.waitForExit();
      return 0;
    }
  }
}
