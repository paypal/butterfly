package com.paypal.butterfly.rest.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * Source code management system used to store the application project
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "scmType",
    "repoUrl",
    "repoBranch",
    "folder"
})
public class Scm {

  public enum Type {
    @JsonProperty("github")
    GITHUB("github");

    private String name;

    Type(String name) {
      this.name = name;
    }
  }

  @NotNull
  @JsonProperty("scmType")
  private Type scmType;

  @NotNull
  @JsonProperty("repoUrl")
  private String repoUrl;

  @JsonProperty("repoBranch")
  @NotNull
  private String repoBranch;

  @JsonProperty("folder")
  @Pattern(
      regexp = "^\\/?([A-z0-9-_+]+\\/?)*$"
  )
  private String folder;

  /**
   * Source code management system used to store the application project
   */
  @JsonProperty("scmType")
  public Type getScmType() {
    return this.scmType;
  }

  @JsonProperty("scmType")
  public void setScmType(Type scmType) {
    this.scmType = scmType;
  }

  /**
   * Application project repository URL
   */
  @JsonProperty("repoUrl")
  public String getRepoUrl() {
    return this.repoUrl;
  }

  @JsonProperty("repoUrl")
  public void setRepoUrl(String repoUrl) {
    this.repoUrl = repoUrl;
  }

  /**
   * Application project repository branch
   */
  @JsonProperty("repoBranch")
  public String getRepoBranch() {
    return this.repoBranch;
  }

  @JsonProperty("repoBranch")
  public void setRepoBranch(String repoBranch) {
    this.repoBranch = repoBranch;
  }

  /**
   * Folder where the application to be transformed is located, if not in the root of the repository
   */
  @JsonProperty("folder")
  public String getFolder() {
    return this.folder;
  }

  @JsonProperty("folder")
  public void setFolder(String folder) {
    this.folder = folder;
  }

}
