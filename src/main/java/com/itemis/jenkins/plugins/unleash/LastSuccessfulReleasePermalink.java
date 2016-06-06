package com.itemis.jenkins.plugins.unleash;

import hudson.model.Result;
import hudson.model.Run;
import hudson.model.PermalinkProjectAction.Permalink;
import jenkins.model.PeepholePermalink;

public class LastSuccessfulReleasePermalink extends PeepholePermalink {
  public static final Permalink INSTANCE = new LastSuccessfulReleasePermalink();

  @Override
  public boolean apply(Run<?, ?> run) {
    boolean retVal = false;
    UnleashAction a = run.getAction(UnleashAction.class);
    if (a != null) {
      if (!run.isBuilding()) {
        if (run.getResult() == Result.SUCCESS) {
          retVal = true;
        }
      }
    }
    return retVal;
  }

  @Override
  public String getDisplayName() {
    return "Last Successful Release";
  }

  @Override
  public String getId() {
    return "lastSuccessfulRelease";
  }
}
