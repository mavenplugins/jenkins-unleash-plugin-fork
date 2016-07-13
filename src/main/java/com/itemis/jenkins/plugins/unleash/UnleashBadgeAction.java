package com.itemis.jenkins.plugins.unleash;

import hudson.model.BuildBadgeAction;
import hudson.model.Result;
import hudson.model.Run;
import jenkins.model.RunAction2;

public class UnleashBadgeAction implements BuildBadgeAction, RunAction2 {
  private Run<?, ?> run;

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
    sb.append(" - ").append(getVersionNumber());
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

  public boolean isBuilding() {
    return this.run.isBuilding();
  }

  public String getVersionNumber() {
    UnleashArgumentsAction args = this.run.getAction(UnleashArgumentsAction.class);
    if (args != null) {
      return args.getGlobalReleaseVersion();
    } else { // builds by old versions of the plugin
      return "Unknown Version";
    }
  }
}
