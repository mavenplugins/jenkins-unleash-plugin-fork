package com.itemis.jenkins.plugins.unleash.util;

import java.io.IOException;

import com.itemis.jenkins.plugins.unleash.UnleashBadgeAction;

import hudson.model.Result;
import hudson.model.Run;
import hudson.util.RunList;

public class BuildUtil {

  private BuildUtil() {
    // not supposed to be instantiated
  }

  /**
   * Lock the given numberOfBuildsToLock of succeeded release builds.
   *
   * @param build                The current build
   * @param numberOfBuildsToLock Keep up to this number of succeeded release builds locked
   * @throws IOException
   * @throws InterruptedException
   */
  public static void lockSucceededReleaseBuilds(Run<?, ?> build, int numberOfBuildsToLock)
      throws IOException, InterruptedException {
    int lockedBuilds = 0;
    Result result = build.getResult();
    if (result != null && result.isBetterOrEqualTo(Result.UNSTABLE)) {
      if (numberOfBuildsToLock != 0) {
        build.keepLog();
        lockedBuilds++;
      }
      // Loop over all project builds by the latest build first
      for (Run<?, ?> run : (RunList<? extends Run<?, ?>>) build.getParent().getBuilds()) {
        if (isSuccessfulReleaseBuild(run)) {
          if (numberOfBuildsToLock < 0 || lockedBuilds < numberOfBuildsToLock) {
            run.keepLog();
            lockedBuilds++;
          } else {
            run.keepLog(false);
          }
        }
      }
    }
  }

  private static boolean isSuccessfulReleaseBuild(Run<?, ?> run) {
    UnleashBadgeAction badgeAction = run.getAction(UnleashBadgeAction.class);
    if (badgeAction != null && !run.isBuilding()) {
      Result result = run.getResult();
      if (result != null && result.isBetterOrEqualTo(Result.UNSTABLE)) {
        return true;
      }
    }
    return false;
  }

}
