package com.felixseifert.socialweatherstreamer.tweetconsumepublisher;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/hello")
public class ExampleResource {

  @GET
  @Produces(MediaType.TEXT_PLAIN)
  public String hello() {
    return "This application should connect to Twitter's Filtered Stream API and publish retrieved Tweets to Kafka.";
  }
}
