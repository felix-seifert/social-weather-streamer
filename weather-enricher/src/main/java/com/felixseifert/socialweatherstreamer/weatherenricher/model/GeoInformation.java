package com.felixseifert.socialweatherstreamer.weatherenricher.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.List;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableGeoInformation.class)
@JsonDeserialize(as = ImmutableGeoInformation.class)
public abstract class GeoInformation {

  public abstract String id();

  public abstract String fullName();

  public abstract String country();

  public abstract String countryCode();

  public abstract List<Double> boxCoordinates();

  public abstract String type();
}
