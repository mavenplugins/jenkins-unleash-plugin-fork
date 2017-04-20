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
  private Run<?, ?> run;
  private String version;

  public UnleashBadgeAction(String version) {
    this.version = version;
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
      sb.append("Failed release");
    } else {
      sb.append("Release");
    }
    sb.append(" - ").append(getVersion());
    return sb.toString();
  }

  public boolean isSuccessfulBuild() {
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

  public boolean isUnstableBuild() {
    if (this.run != null) {
      Result result = this.run.getResult();
      if (result != null) {
        return result.isBetterOrEqualTo(Result.UNSTABLE) && result.isWorseOrEqualTo(Result.UNSTABLE);
      }
    }
    return false;
  }

  public boolean isBuilding() {
    return this.run.isBuilding();
  }

  public String getVersion() {
    if (!Strings.isNullOrEmpty(this.version)) {
      return this.version;
    }

    // backwards compatibility to older builds where the badge action doesn't yet carry the version attribute
    UnleashArgumentsAction args = this.run.getAction(UnleashArgumentsAction.class);
    if (args != null) {
      return args.getGlobalReleaseVersion();
    }

    return "Unknown Version";
  }

  public void setVersion(String version) {
    if (!Strings.isNullOrEmpty(version)) {
      this.version = version;
    }
  }
}
