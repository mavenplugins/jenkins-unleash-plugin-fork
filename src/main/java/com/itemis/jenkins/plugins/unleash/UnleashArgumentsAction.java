/*
 * The MIT License
 *
 * Copyright (c) 2009, NDS Group Ltd., James Nord, CloudBees, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.itemis.jenkins.plugins.unleash;

import hudson.model.Action;
import hudson.model.ParameterValue;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Stanley Hillner
 */
// This class was developed based on org.jvnet.hudson.plugins.m2release.M2ReleaseArgumentsAction
// The class still contains substantial parts of the original implementation
// original authors: teilo
public class UnleashArgumentsAction implements Action {
  private String globalReleaseVersion;
  private String globalDevelopmentVersion;
  private boolean useGlobalReleaseVersion;
  private boolean allowLocalReleaseArtifacts;
  private boolean commitBeforeTagging;
  private boolean errorLog;
  private boolean debugLog;

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

  public boolean errorLog() {
    return this.errorLog;
  }

  public void setErrorLog(boolean errorLog) {
    this.errorLog = errorLog;
  }

  public boolean debugLog() {
    return this.debugLog;
  }

  public void setDebugLog(boolean debugLog) {
    this.debugLog = debugLog;
  }
}
