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
    StringBuilder str = new StringBuilder();

    if (isFailedBuild()) {
      str.append("Failed release");
    } else {
      str.append("Release");
    }
    str.append(" - ");
    str.append(getVersionNumber());

    return str.toString();
  }

  public boolean isFailedBuild() {
    return !isSuccessfulBuild(this.run);
  }

  private boolean isSuccessfulBuild(Run<?, ?> run) {
    Result result = run.getResult();
    if (result != null) {
      return result.isBetterOrEqualTo(Result.SUCCESS);
    } else { // build is not yet initiated
      return true;
    }
  }

  public String getVersionNumber() {
    UnleashArgumentsAction args = this.run.getAction(UnleashArgumentsAction.class);
    if (args != null) {
      return args.getGlobalReleaseVersion().or("Unknown Version");
    } else { // builds by old versions of the plugin
      return null;
    }
  }
}