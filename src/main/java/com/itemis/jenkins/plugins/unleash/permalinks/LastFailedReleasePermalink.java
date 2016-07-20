package com.itemis.jenkins.plugins.unleash.permalinks;

import com.itemis.jenkins.plugins.unleash.UnleashBadgeAction;

import hudson.model.Run;
import hudson.model.PermalinkProjectAction.Permalink;
import jenkins.model.PeepholePermalink;

public class LastFailedReleasePermalink extends PeepholePermalink {
  public static final Permalink INSTANCE = new LastFailedReleasePermalink();

  @Override
  public boolean apply(Run<?, ?> run) {
    UnleashBadgeAction badgeAction = run.getAction(UnleashBadgeAction.class);
    if (badgeAction != null) {
      return !run.isBuilding() && badgeAction.isFailedBuild();
    }
    return false;
  }

  @Override
  public String getDisplayName() {
    return "Last Failed Release Build";
  }

  @Override
  public String getId() {
    return "lastFailedReleaseBuild";
  }
}
