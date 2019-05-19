package com.paypal.butterfly.rest.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;

/**
 * Project location of the application to be transformed (a folder or a repo have to be defined)
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "folder",
    "repo"
})
public class Application {

  @JsonProperty("folder")
  @Pattern(
      regexp = "^\\/?([A-z0-9-_+]+\\/?)*$"
  )
  private String folder;

  @JsonProperty("repo")
  @Valid
  private Scm repo;

  /**
   * Path to the folder on the server machine where the application to be transformed is
   */
  @JsonProperty("folder")
  public String getFolder() {
    return this.folder;
  }

  @JsonProperty("folder")
  public void setFolder(String folder) {
    this.folder = folder;
  }

  /**
   * Application repository information
   */
  @JsonProperty("repo")
  public Scm getRepo() {
    return this.repo;
  }

  @JsonProperty("repo")
  public void setRepo(Scm repo) {
    this.repo = repo;
  }

}
