package com.itemis.jenkins.plugins.unleash;

import com.google.common.base.Optional;

import hudson.model.Action;

public class UnleashArgumentsAction implements Action {
  private String globalReleaseVersion;
  private String globalDevelopmentVersion;
  private String scmUsername;
  private String scmPassword;

  @Override
  public String getIconFileName() {
    return null;
  }

  @Override
  public String getDisplayName() {
    return null;
  }

  @Override
  public String getUrlName() {
    return null;
  }

  public Optional<String> getGlobalReleaseVersion() {
    return Optional.fromNullable(this.globalReleaseVersion);
  }

  public void setGlobalReleaseVersion(String globalReleaseVersion) {
    this.globalReleaseVersion = globalReleaseVersion;
  }

  public Optional<String> getGlobalDevelopmentVersion() {
    return Optional.fromNullable(this.globalDevelopmentVersion);
  }

  public void setGlobalDevelopmentVersion(String globalDevelopmentVersion) {
    this.globalDevelopmentVersion = globalDevelopmentVersion;
  }

  public Optional<String> getScmUsername() {
    return Optional.fromNullable(this.scmUsername);
  }

  public void setScmUsername(String scmUsername) {
    this.scmUsername = scmUsername;
  }

  public Optional<String> getScmPassword() {
    return Optional.fromNullable(this.scmPassword);
  }

  public void setScmPassword(String scmPassword) {
    this.scmPassword = scmPassword;
  }
}
