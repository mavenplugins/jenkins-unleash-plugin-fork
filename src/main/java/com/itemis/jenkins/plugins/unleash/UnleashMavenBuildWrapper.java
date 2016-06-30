package com.itemis.jenkins.plugins.unleash;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

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
import net.sf.json.JSONObject;

public class UnleashMavenBuildWrapper extends BuildWrapper {
  private String goals = DescriptorImpl.DEFAULT_GOALS;
  private String profiles = DescriptorImpl.DEFAULT_PROFILES;
  private String releaseArgs = DescriptorImpl.DEFAULT_RELEASE_ARGS;
  private List<HookDescriptor> hooks = DescriptorImpl.DEFAULT_HOOKS;
  private boolean useLogTimestamps = DescriptorImpl.DEFAULT_USE_LOG_TIMESTAMPS;
  private String tagNamePattern = DescriptorImpl.DEFAULT_TAG_NAME_PATTERN;
  private String scmMessagePrefix = DescriptorImpl.DEFAULT_SCM_MESSAGE_PREFIX;
  private boolean preselectUseCustomScmCredentials = DescriptorImpl.DEFAULT_PRESELECT_USE_CUSTOM_SCM_CREDENTIALS;
  private boolean preselectUseGlobalVersion = DescriptorImpl.DEFAULT_PRESELECT_USE_GLOBAL_VERSION;
  private boolean preselectAllowLocalReleaseArtifacts = DescriptorImpl.DEFAULT_PRESELECT_ALLOW_LOCAL_RELEASE_ARTIFACTS;
  private boolean preselectCommitBeforeTagging = DescriptorImpl.DEFAULT_PRESELECT_COMMIT_BEFORE_TAGGING;

  @DataBoundConstructor
  public UnleashMavenBuildWrapper(String goals, String profiles, String releaseArgs, boolean useLogTimestamps,
      String tagNamePattern, String scmMessagePrefix, boolean preselectUseCustomScmCredentials,
      boolean preselectUseGlobalVersion, List<HookDescriptor> hooks, boolean preselectAllowLocalReleaseArtifacts,
      boolean preselectCommitBeforeTagging) {
    super();
    this.goals = goals;
    this.profiles = profiles;
    this.releaseArgs = releaseArgs;
    this.useLogTimestamps = useLogTimestamps;
    this.tagNamePattern = tagNamePattern;
    this.scmMessagePrefix = scmMessagePrefix;
    this.preselectUseCustomScmCredentials = preselectUseCustomScmCredentials;
    this.preselectUseGlobalVersion = preselectUseGlobalVersion;
    this.hooks = hooks;
    this.preselectAllowLocalReleaseArtifacts = preselectAllowLocalReleaseArtifacts;
    this.preselectCommitBeforeTagging = preselectCommitBeforeTagging;
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
    StringBuilder command = new StringBuilder(getGoals());

    // appends the profiles to the Maven call
    if (StringUtils.isNotBlank(getProfiles())) {
      Iterable<String> split = Splitter.on(',').split(getProfiles());
      boolean isFirst = true;
      for (String profile : split) {
        if (StringUtils.isBlank(profile)) {
          continue;
        }

        if (isFirst) {
          command.append(" -Dunleash.profiles=");
        } else {
          command.append(',');
        }
        command.append(profile.trim());
      }
    }

    if (StringUtils.isNotBlank(getReleaseArgs())) {
      command.append(" -Dunleash.releaseArgs=\"").append(getReleaseArgs().trim()).append("\"");
    }
    if (StringUtils.isNotBlank(getTagNamePattern())) {
      command.append(" -Dunleash.tagNamePattern=").append(getTagNamePattern().trim());
    }
    if (StringUtils.isNotBlank(getScmMessagePrefix())) {
      command.append(" -Dunleash.scmMessagePrefix=").append(getScmMessagePrefix().trim());
    }
    command.append(" -Dunleash.logTimestamps=").append(isUseLogTimestamps());

    if (this.hooks != null) {
      for (HookDescriptor hookData : this.hooks) {
        if (StringUtils.isNotBlank(hookData.getName()) && StringUtils.isNotBlank(hookData.getData())) {
          command.append(" -D").append(hookData.getName()).append("=\"").append(hookData.getData()).append("\"");
          if (StringUtils.isNotBlank(hookData.getRollbackData())) {
            command.append(" -D").append(hookData.getName()).append("-rollback=\"").append(hookData.getRollbackData())
                .append("\"");
          }
        }
      }
    }

    if (arguments != null) {
      if (arguments.useGlobalReleaseVersion()) {
        command.append(" -Dunleash.releaseVersion=").append(arguments.getGlobalReleaseVersion());
        command.append(" -Dunleash.developmentVersion=").append(arguments.getGlobalDevelopmentVersion());
      }
      if (arguments.getScmUsername().isPresent()) {
        command.append(" -Dunleash.scmUsername=").append(arguments.getScmUsername().get());
      }
      if (arguments.getScmPassword().isPresent()) {
        command.append(" -Dunleash.scmPassword=").append(arguments.getScmPassword().get());
      }
      command.append(" -Dunleash.allowLocalReleaseArtifacts=").append(arguments.allowLocalReleaseArtifacts());
      command.append(" -Dunleash.commitBeforeTagging=").append(arguments.commitBeforeTagging());
    }

    build.addAction(new UnleashArgumentInterceptorAction(command.toString()));
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

  public String getProfiles() {
    return this.profiles;
  }

  public void setProfiles(String profiles) {
    this.profiles = profiles;
  }

  public String getReleaseArgs() {
    return this.releaseArgs;
  }

  public void setReleaseArgs(String releaseArgs) {
    this.releaseArgs = releaseArgs;
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

  public String getScmMessagePrefix() {
    return this.scmMessagePrefix;
  }

  public void setScmMessagePrefix(String scmMessagePrefix) {
    this.scmMessagePrefix = scmMessagePrefix;
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
    return Collections.singleton(new UnleashAction((MavenModuleSet) job, this.preselectUseCustomScmCredentials,
        this.preselectUseGlobalVersion, this.preselectAllowLocalReleaseArtifacts, this.preselectCommitBeforeTagging));
  }

  public List<HookDescriptor> getHooks() {
    return this.hooks;
  }

  public void setHooks(List<HookDescriptor> hooks) {
    this.hooks = hooks;
  }

  public boolean isPreselectAllowLocalReleaseArtifacts() {
    return this.preselectAllowLocalReleaseArtifacts;
  }

  public void setPreselectAllowLocalReleaseArtifacts(boolean preselectAllowLocalReleaseArtifacts) {
    this.preselectAllowLocalReleaseArtifacts = preselectAllowLocalReleaseArtifacts;
  }

  public boolean isPreselectCommitBeforeTagging() {
    return this.preselectCommitBeforeTagging;
  }

  public void setPreselectCommitBeforeTagging(boolean preselectCommitBeforeTagging) {
    this.preselectCommitBeforeTagging = preselectCommitBeforeTagging;
  }

  @Extension
  public static class DescriptorImpl extends BuildWrapperDescriptor {
    public static final String DEFAULT_GOALS = "unleash:perform";
    public static final String DEFAULT_PROFILES = "";
    public static final String DEFAULT_RELEASE_ARGS = "";
    public static final List<HookDescriptor> DEFAULT_HOOKS = Lists.newArrayList();
    public static final boolean DEFAULT_USE_LOG_TIMESTAMPS = true;
    public static final String DEFAULT_TAG_NAME_PATTERN = "@{project.version}";
    public static final String DEFAULT_SCM_MESSAGE_PREFIX = "[unleash-maven-plugin]";
    public static final boolean DEFAULT_PRESELECT_USE_CUSTOM_SCM_CREDENTIALS = false;
    public static final boolean DEFAULT_PRESELECT_USE_GLOBAL_VERSION = false;
    public static final boolean DEFAULT_PRESELECT_ALLOW_LOCAL_RELEASE_ARTIFACTS = true;
    public static final boolean DEFAULT_PRESELECT_COMMIT_BEFORE_TAGGING = false;

    private boolean useLogTimestamps = DEFAULT_USE_LOG_TIMESTAMPS;
    private boolean preselectAllowLocalReleaseArtifacts = DEFAULT_PRESELECT_ALLOW_LOCAL_RELEASE_ARTIFACTS;
    private boolean preselectCommitBeforeTagging = DEFAULT_PRESELECT_COMMIT_BEFORE_TAGGING;
    private String tagNamePattern = DEFAULT_TAG_NAME_PATTERN;
    private String scmMessagePrefix = DEFAULT_SCM_MESSAGE_PREFIX;

    public void setUseLogTimestamps(boolean useLogTimestamps) {
      this.useLogTimestamps = useLogTimestamps;
    }

    public boolean isUseLogTimestamps() {
      return this.useLogTimestamps;
    }

    public void setPreselectAllowLocalReleaseArtifacts(boolean preselectAllowLocalReleaseArtifacts) {
      this.preselectAllowLocalReleaseArtifacts = preselectAllowLocalReleaseArtifacts;
    }

    public boolean isPreselectAllowLocalReleaseArtifacts() {
      return this.preselectAllowLocalReleaseArtifacts;
    }

    public void setPreselectCommitBeforeTagging(boolean preselectCommitBeforeTagging) {
      this.preselectCommitBeforeTagging = preselectCommitBeforeTagging;
    }

    public boolean isPreselectCommitBeforeTagging() {
      return this.preselectCommitBeforeTagging;
    }

    public void setTagNamePattern(String tagNamePattern) {
      this.tagNamePattern = tagNamePattern;
    }

    public String getTagNamePattern() {
      return this.tagNamePattern;
    }

    public void setScmMessagePrefix(String scmMessagePrefix) {
      this.scmMessagePrefix = scmMessagePrefix;
    }

    public String getScmMessagePrefix() {
      return this.scmMessagePrefix;
    }

    @Override
    public boolean isApplicable(AbstractProject<?, ?> item) {
      return item instanceof AbstractMavenProject;
    }

    @Override
    public String getDisplayName() {
      return "Unleash";
    }

    @Override
    public boolean configure(StaplerRequest req, JSONObject json) throws hudson.model.Descriptor.FormException {
      req.bindJSON(this, json);
      save();
      return super.configure(req, json);
    }
  }
}
