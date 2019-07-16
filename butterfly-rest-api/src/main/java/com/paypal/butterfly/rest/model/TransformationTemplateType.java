package com.paypal.butterfly.rest.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum TransformationTemplateType {

  @JsonProperty("transformation")
  TRANSFORMATION("transformation"),

  @JsonProperty("upgrade")
  UPGRADE("upgrade");

  private String name;

  TransformationTemplateType(String name) {
    this.name = name;
  }
}
