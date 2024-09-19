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

import com.google.common.base.Strings;

import hudson.model.BuildBadgeAction;
import hudson.model.Result;
import hudson.model.Run;
import jenkins.model.RunAction2;

/**
 * @author Stanley Hillner
 */
// This class was developed based on org.jvnet.hudson.plugins.m2release.M2ReleaseBadgeAction
// The class still contains substantial parts of the original implementation
// original authors: domi & teilo
public class UnleashBadgeAction implements BuildBadgeAction, RunAction2 {

  public static final String UNKNWON_VERSION = "Unknown Version";

  // Tooltip prefixes
  public static final String TT_PREFIX_SEPARATOR = " - ";
  public static final String RELEASE_TT_PREFIX = "Release" + TT_PREFIX_SEPARATOR;
  public static final String DRYRUN_TT_PREFIX = "Dryrun" + TT_PREFIX_SEPARATOR;
  public static final String FAILED_RELEASE_TT_PREFIX = "Failed release" + TT_PREFIX_SEPARATOR;
  public static final String FAILED_DRYRUN_TT_PREFIX = "Failed dryrun" + TT_PREFIX_SEPARATOR;

  private Run<?, ?> run;
  private String version;
  private boolean isDryRun;

  public UnleashBadgeAction(String version) {
    this(version, false);
  }

  public UnleashBadgeAction(String version, boolean isDryRun) {
    this.version = version;
    this.isDryRun = isDryRun;
  }

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

  @Override
  public void onAttached(Run<?, ?> run) {
    this.run = run;
  }

  @Override
  public void onLoad(Run<?, ?> run) {
    this.run = run;
  }

  public String getTooltipText() {
    StringBuilder sb = new StringBuilder();
    if (isFailedBuild()) {
      sb.append(isDryRun() ? FAILED_DRYRUN_TT_PREFIX : FAILED_RELEASE_TT_PREFIX);
    } else {
      sb.append(isDryRun() ? DRYRUN_TT_PREFIX : RELEASE_TT_PREFIX);
    }
    sb.append(getVersion());
    return sb.toString();
  }

  public boolean isSuccessfulDryRunBuild() {
    return isSuccessfulBuild() && isDryRun();
  }

  public boolean isSuccessfulReleaseBuild() {
    return isSuccessfulBuild() && !isDryRun();
  }

  private boolean isSuccessfulBuild() {
    if (this.run != null) {
      Result result = this.run.getResult();
      if (result != null) {
        return result.isBetterOrEqualTo(Result.SUCCESS);
      }
    }
    return false;
  }

  public boolean isFailedBuild() {
    if (this.run != null) {
      Result result = this.run.getResult();
      if (result != null) {
        return result.isWorseOrEqualTo(Result.FAILURE);
      }
    }
    return false;
  }

  public boolean isUnstableDryRunBuild() {
    return isUnstableBuild() && isDryRun();
  }

  public boolean isUnstableReleaseBuild() {
    return isUnstableBuild() && !isDryRun();
  }

  private boolean isUnstableBuild() {
    if (this.run != null) {
      Result result = this.run.getResult();
      if (result != null) {
        return result.isBetterOrEqualTo(Result.UNSTABLE) && result.isWorseOrEqualTo(Result.UNSTABLE);
      }
    }
    return false;
  }

  public boolean isBuilding() {
    if (this.run != null) {
      return this.run.isBuilding();
    }
    return false;
  }

  public String getVersion() {
    if (!Strings.isNullOrEmpty(this.version)) {
      return this.version;
    }

    // backwards compatibility to older builds where the badge action doesn't yet carry the version attribute
    if (this.run != null) {
      UnleashArgumentsAction args = this.run.getAction(UnleashArgumentsAction.class);
      if (args != null) {
        return args.getGlobalReleaseVersion();
      }
    }

    return UNKNWON_VERSION;
  }

  public void setVersion(String version) {
    if (!Strings.isNullOrEmpty(version)) {
      this.version = version;
    }
  }

  public boolean isDryRun() {
    return this.isDryRun;
  }

  public void setDryRun(boolean isDryRun) {
    this.isDryRun = isDryRun;
  }
}
