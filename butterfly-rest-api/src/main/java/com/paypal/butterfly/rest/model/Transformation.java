package com.paypal.butterfly.rest.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Basic transformation information
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "id",
    "transformationRequestSimple",
    "dateTime",
})
public class Transformation {

  @JsonProperty("id")
  @NotNull
  private int id;

  @JsonProperty("transformationRequestSimple")
  @NotNull
  @Valid
  private TransformationRequestSimple transformationRequestSimple;

  @JsonProperty("dateTime")
  @JsonFormat(
      shape = JsonFormat.Shape.STRING,
      pattern = "yyyy-MM-dd'T'HH:mm:ss"
  )
  private Date dateTime;

  /**
   * An identifier for this transformation
   */
  @JsonProperty("id")
  public int getId() {
    return this.id;
  }

  @JsonProperty("id")
  public void setId(int id) {
    this.id = id;
  }

  @JsonProperty("transformationRequestSimple")
  public TransformationRequestSimple getTransformationRequestSimple() {
    return this.transformationRequestSimple;
  }

  @JsonProperty("transformationRequestSimple")
  public void setTransformationRequestSimple(
      TransformationRequestSimple transformationRequestSimple) {
    this.transformationRequestSimple = transformationRequestSimple;
  }

  /**
   * The transformation conclusion date in 'yyyyy-mm-dd hh:mm:ss' (absent if not concluded yet)
   */
  @JsonProperty("dateTime")
  public Date getDateTime() {
    return this.dateTime;
  }

  @JsonProperty("dateTime")
  public void setDateTime(Date dateTime) {
    this.dateTime = dateTime;
  }

  // TODO Embed TransformationState

}
