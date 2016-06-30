package com.itemis.jenkins.plugins.unleash;

import com.google.common.base.Optional;

import hudson.model.Action;

public class UnleashArgumentsAction implements Action {
  private String globalReleaseVersion;
  private String globalDevelopmentVersion;
  private String scmUsername;
  private String scmPassword;
  private boolean useGlobalReleaseVersion;
  private boolean allowLocalReleaseArtifacts;
  private boolean commitBeforeTagging;

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

  public void setUseGlobalReleaseVersion(boolean useGlobalReleaseVersion) {
    this.useGlobalReleaseVersion = useGlobalReleaseVersion;
  }

  public boolean useGlobalReleaseVersion() {
    return this.useGlobalReleaseVersion;
  }

  public String getGlobalReleaseVersion() {
    return this.globalReleaseVersion;
  }

  public void setGlobalReleaseVersion(String globalReleaseVersion) {
    this.globalReleaseVersion = globalReleaseVersion;
  }

  public String getGlobalDevelopmentVersion() {
    return this.globalDevelopmentVersion;
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

  public boolean allowLocalReleaseArtifacts() {
    return this.allowLocalReleaseArtifacts;
  }

  public void setAllowLocalReleaseArtifacts(boolean allowLocalReleaseArtifacts) {
    this.allowLocalReleaseArtifacts = allowLocalReleaseArtifacts;
  }

  public boolean commitBeforeTagging() {
    return this.commitBeforeTagging;
  }

  public void setCommitBeforeTagging(boolean commitBeforeTagging) {
    this.commitBeforeTagging = commitBeforeTagging;
  }
}
