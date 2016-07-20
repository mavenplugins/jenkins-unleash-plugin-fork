package com.itemis.jenkins.plugins.unleash.views;

import org.kohsuke.stapler.DataBoundConstructor;

import com.itemis.jenkins.plugins.unleash.UnleashAction;

import hudson.Extension;
import hudson.maven.AbstractMavenProject;
import hudson.views.ListViewColumn;
import hudson.views.ListViewColumnDescriptor;

public class UnleashListViewColumn extends ListViewColumn {
  @DataBoundConstructor
  public UnleashListViewColumn() {
  }

  public boolean isProjectEnabled(AbstractMavenProject<?, ?> project) {
    return project.getAction(UnleashAction.class) != null;
  }

  @Extension
  public static class DescriptorImpl extends ListViewColumnDescriptor {
    @Override
    public String getDisplayName() {
      return "Trigger Unleash Maven Plugin";
    }

    @Override
    public boolean shownByDefault() {
      return false;
    }
  }
}
