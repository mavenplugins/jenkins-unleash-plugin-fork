package com.itemis.jenkins.plugins.unleash.views;

import org.kohsuke.stapler.DataBoundConstructor;

import com.itemis.jenkins.plugins.unleash.UnleashBadgeAction;
import com.itemis.jenkins.plugins.unleash.permalinks.LastFailedReleasePermalink;

import hudson.Extension;
import hudson.maven.AbstractMavenProject;
import hudson.model.AbstractBuild;
import hudson.model.Run;
import hudson.views.ListViewColumn;
import hudson.views.ListViewColumnDescriptor;

public class LastFailedReleaseListViewColumn extends ListViewColumn {
  @DataBoundConstructor
  public LastFailedReleaseListViewColumn() {
  }

  public Info getLastReleaseInfoOf(AbstractMavenProject<?, ?> project) {
    Run<?, ?> run = LastFailedReleasePermalink.INSTANCE.resolve(project);
    if (run != null) {
      return new Info((AbstractBuild<?, ?>) run);
    }
    return null;
  }

  public static class Info {
    public final AbstractBuild<?, ?> build;
    public final UnleashBadgeAction action;

    Info(AbstractBuild<?, ?> build) {
      this.build = build;
      this.action = build.getAction(UnleashBadgeAction.class);
      assert this.action != null;
    }
  }

  @Extension
  public static class DescriptorImpl extends ListViewColumnDescriptor {
    @Override
    public String getDisplayName() {
      return "Last Failed Release (unleash-maven-plugin)";
    }

    @Override
    public boolean shownByDefault() {
      return false;
    }
  }
}
