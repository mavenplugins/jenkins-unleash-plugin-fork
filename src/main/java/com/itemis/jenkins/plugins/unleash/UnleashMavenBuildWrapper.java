package com.itemis.jenkins.plugins.unleash;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;

import hudson.Extension;
import hudson.Launcher;
import hudson.maven.AbstractMavenProject;
import hudson.maven.MavenModuleSet;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Action;
import hudson.model.BuildListener;
import hudson.tasks.BuildWrapper;
import hudson.tasks.BuildWrapperDescriptor;

public class UnleashMavenBuildWrapper extends BuildWrapper {
  private String goals = DescriptorImpl.DEFAULT_GOALS;
  private boolean useLogTimestamps = DescriptorImpl.DEFAULT_USE_LOG_TIMESTAMPS;
  private String tagNamePattern = DescriptorImpl.DEFAULT_TAG_NAME_PATTERN;
  private boolean preselectUseCustomScmCredentials = DescriptorImpl.DEFAULT_PRESELECT_USE_CUSTOM_SCM_CREDENTIALS;
  private boolean preselectUseGlobalVersion = DescriptorImpl.DEFAULT_PRESELECT_USE_GLOBAL_VERSION;

  @DataBoundConstructor
  public UnleashMavenBuildWrapper(String goals, boolean useLogTimestamps, String tagNamePattern,
      boolean preselectUseCustomScmCredentials, boolean preselectUseGlobalVersion) {
    super();
    this.goals = goals;
    this.useLogTimestamps = useLogTimestamps;
    this.tagNamePattern = tagNamePattern;
    this.preselectUseCustomScmCredentials = preselectUseCustomScmCredentials;
    this.preselectUseGlobalVersion = preselectUseGlobalVersion;
  }

  @Override
  public Environment setUp(@SuppressWarnings("rawtypes") AbstractBuild build, Launcher launcher, BuildListener listener)
      throws IOException, InterruptedException {
    if (!isReleaseBuild(build)) {
      return new Environment() {
        @Override
        public void buildEnvVars(Map<String, String> env) {
          // if (StringUtils.isNotBlank(releaseEnvVar)) {
          // // inform others that we are NOT doing a release build
          // env.put(releaseEnvVar, "false");
          // }
        }
      };
    }

    UnleashArgumentsAction arguments = build.getAction(UnleashArgumentsAction.class);
    StringBuilder buildGoals = new StringBuilder();
    if (arguments != null) {
      if (isUseLogTimestamps()) {
        buildGoals.append("-Dunleash.logTimestamps=true ");
      }
      buildGoals.append("-Dunleash.tagNamePattern=").append(getTagNamePattern()).append(' ');
      if (arguments.getGlobalReleaseVersion().isPresent()) {
        buildGoals.append("-Dunleash.releaseVersion=").append(arguments.getGlobalReleaseVersion().get()).append(' ');
      }
      if (arguments.getGlobalDevelopmentVersion().isPresent()) {
        buildGoals.append("-Dunleash.developmentVersion=").append(arguments.getGlobalDevelopmentVersion().get())
            .append(' ');
      }
      if (arguments.getScmUsername().isPresent()) {
        buildGoals.append("-Dunleash.scmUsername=").append(arguments.getScmUsername().get()).append(' ');
      }
      if (arguments.getScmPassword().isPresent()) {
        buildGoals.append("-Dunleash.scmPassword=").append(arguments.getScmPassword().get()).append(' ');
      }
      buildGoals.append(getGoals());
    }

    build.addAction(new UnleashArgumentInterceptorAction(buildGoals.toString()));
    build.addAction(new UnleashBadgeAction());

    return new Environment() {
      @Override
      public void buildEnvVars(Map<String, String> env) {
        // if (StringUtils.isNotBlank(releaseEnvVar)) {
        // // inform others that we are NOT doing a release build
        // env.put(releaseEnvVar, "false");
        // }
      }
    };
  }

  private boolean isReleaseBuild(@SuppressWarnings("rawtypes") AbstractBuild build) {
    return build.getCause(UnleashCause.class) != null;
  }

  public String getGoals() {
    return StringUtils.isBlank(this.goals) ? DescriptorImpl.DEFAULT_GOALS : this.goals;
  }

  public void setGoals(String goals) {
    this.goals = goals;
  }

  public boolean isUseLogTimestamps() {
    return this.useLogTimestamps;
  }

  public void setUseLogTimestamps(boolean useLogTimestamps) {
    this.useLogTimestamps = useLogTimestamps;
  }

  public String getTagNamePattern() {
    return StringUtils.isBlank(this.tagNamePattern) ? DescriptorImpl.DEFAULT_TAG_NAME_PATTERN : this.tagNamePattern;
  }

  public void setTagNamePattern(String tagNamePattern) {
    this.tagNamePattern = tagNamePattern;
  }

  public boolean isPreselectUseCustomScmCredentials() {
    return this.preselectUseCustomScmCredentials;
  }

  public void setPreselectUseCustomScmCredentials(boolean preselectUseCustomScmCredentials) {
    this.preselectUseCustomScmCredentials = preselectUseCustomScmCredentials;
  }

  public boolean isPreselectUseGlobalVersion() {
    return this.preselectUseGlobalVersion;
  }

  public void setPreselectUseGlobalVersion(boolean preselectUseGlobalVersion) {
    this.preselectUseGlobalVersion = preselectUseGlobalVersion;
  }

  @Override
  public Collection<? extends Action> getProjectActions(@SuppressWarnings("rawtypes") AbstractProject job) {
    return Collections.singleton(
        new UnleashAction((MavenModuleSet) job, this.preselectUseCustomScmCredentials, this.preselectUseGlobalVersion));
  }

  @Extension
  public static class DescriptorImpl extends BuildWrapperDescriptor {
    public static final String DEFAULT_GOALS = "unleash:perform";
    public static final boolean DEFAULT_USE_LOG_TIMESTAMPS = true;
    public static final String DEFAULT_TAG_NAME_PATTERN = "@{project.version}";
    public static final boolean DEFAULT_PRESELECT_USE_CUSTOM_SCM_CREDENTIALS = false;
    public static final boolean DEFAULT_PRESELECT_USE_GLOBAL_VERSION = false;

    @Override
    public boolean isApplicable(AbstractProject<?, ?> item) {
      return item instanceof AbstractMavenProject;
    }

    @Override
    public String getDisplayName() {
      return "Unleash";
    }
  }
}
