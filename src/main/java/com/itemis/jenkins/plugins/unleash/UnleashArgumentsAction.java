package com.itemis.jenkins.plugins.unleash;

import hudson.model.Action;

public class UnleashArgumentsAction implements Action {
  private String globalReleaseVersion;
  private String globalDevelopmentVersion;
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
