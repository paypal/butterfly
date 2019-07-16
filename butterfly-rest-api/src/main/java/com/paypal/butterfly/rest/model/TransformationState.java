package com.paypal.butterfly.rest.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import javax.validation.constraints.NotNull;

/**
 * The state of a transformation
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "state"
})
public class TransformationState {

  public enum State {

    @JsonProperty("Queued")
    QUEUED("Queued"),

    @JsonProperty("InProgress")
    INPROGRESS("InProgress"),

    @JsonProperty("Invalid")
    INVALID("Invalid"),

    @JsonProperty("Succeeded")
    SUCCEEDED("Succeeded"),

    @JsonProperty("Failed")
    FAILED("Failed");

    private String name;

    State(String name) {
      this.name = name;
    }

  }

  @JsonProperty("state")
  @NotNull
  private State state;

  @JsonProperty("state")
  public State getState() {
    return this.state;
  }

  @JsonProperty("state")
  public void setState(State state) {
    this.state = state;
  }

}
