package com.felixseifert.socialweatherstreamer.tweetconsumepublisher;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.json.JSONObject;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class Tweet {

  private final Long id;

  private final String text;

  public Tweet(Long id, String text) {
    this.id = id;
    this.text = text;
  }

  public static Tweet parseFromJsonObject(JSONObject jsonObject) {
    final long id = jsonObject.getLong("id");
    final String text = jsonObject.getString("text");
    return Tweet.builder().id(id).text(text).build();
  }
}
