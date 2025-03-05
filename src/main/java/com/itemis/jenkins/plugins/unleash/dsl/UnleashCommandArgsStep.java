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
package com.itemis.jenkins.plugins.unleash.dsl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import com.itemis.jenkins.plugins.unleash.HookDescriptor;
import com.itemis.jenkins.plugins.unleash.UnleashArgumentsAction;
import com.itemis.jenkins.plugins.unleash.UnleashScmCredentialArguments;
import com.itemis.jenkins.plugins.unleash.util.BuildUtil;
import com.itemis.maven.plugins.unleash.util.VersionUpgradeStrategy;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.model.ParametersAction;
import hudson.model.Run;

public class UnleashCommandArgsStep extends AbstractStep<String> {

  private String goal;
  private String releaseVersion;
  private String nextDevelopmentVersion;
  private boolean commitBeforeTagging = true;
  private boolean allowLocalReleaseArtifacts = true;
  private boolean useLogTimestamps = true;
  private String scmTagNamePattern = "@{project.version}";
  private String scmMessagePrefix = "[unleash-maven-plugin]";
  // scm user / password
  private String scmUsername;
  private String scmPassword;
  private String scmUsernameEnvVar;
  private String scmPasswordEnvVar;
  // git SSH
  private String scmSshPassphrase;
  private String scmSshPassphraseEnvVar;
  private String scmSshPrivateKeyEnvVar;
  // Advanced
  private String releaseArgs;
  private String mavenProfiles;
  private String workflowFilePath;
  private List<HookDescriptor> hooks = new ArrayList<HookDescriptor>();
  // Maven debugging outputs
  private boolean errorLog = true;
  private boolean debugLog = false;

  /**
   * @param goal                   The Maven goal
   * @param releaseVersion         The release version - must not be empty
   * @param nextDevelopmentVersion The next development version - must not be empty
   */
  @DataBoundConstructor
  public UnleashCommandArgsStep(String goal, String releaseVersion, String nextDevelopmentVersion) {
    this.goal = goal != null ? goal.trim() : "";
    this.releaseVersion = releaseVersion != null ? releaseVersion.trim() : "";
    this.nextDevelopmentVersion = nextDevelopmentVersion != null ? nextDevelopmentVersion.trim() : "";
  }

  public String getGoal() {
    return this.goal;
  }

  public void setGoal(String goal) {
    this.goal = goal;
  }

  public String getReleaseVersion() {
    return this.releaseVersion;
  }

  public void setReleaseVersion(String releaseVersion) {
    this.releaseVersion = releaseVersion;
  }

  public String getNextDevelopmentVersion() {
    return this.nextDevelopmentVersion;
  }

  public void setNextDevelopmentVersion(String nextDevelopmentVersion) {
    this.nextDevelopmentVersion = nextDevelopmentVersion;
  }

  public boolean isCommitBeforeTagging() {
    return this.commitBeforeTagging;
  }

  @DataBoundSetter
  public void setCommitBeforeTagging(boolean commitBeforeTagging) {
    this.commitBeforeTagging = commitBeforeTagging;
  }

  public boolean isAllowLocalReleaseArtifacts() {
    return this.allowLocalReleaseArtifacts;
  }

  @DataBoundSetter
  public void setAllowLocalReleaseArtifacts(boolean allowLocalReleaseArtifacts) {
    this.allowLocalReleaseArtifacts = allowLocalReleaseArtifacts;
  }

  public boolean isUseLogTimestamps() {
    return this.useLogTimestamps;
  }

  @DataBoundSetter
  public void setUseLogTimestamps(boolean useLogTimestamps) {
    this.useLogTimestamps = useLogTimestamps;
  }

  public String getScmTagNamePattern() {
    return this.scmTagNamePattern;
  }

  @DataBoundSetter
  public void setScmTagNamePattern(String scmTagNamePattern) {
    this.scmTagNamePattern = scmTagNamePattern;
  }

  public String getScmMessagePrefix() {
    return this.scmMessagePrefix;
  }

  @DataBoundSetter
  public void setScmMessagePrefix(String scmMessagePrefix) {
    this.scmMessagePrefix = scmMessagePrefix;
  }

  public String getScmUsername() {
    return this.scmUsername;
  }

  @DataBoundSetter
  public void setScmUsername(String scmUsername) {
    this.scmUsername = scmUsername;
  }

  public String getScmPassword() {
    return this.scmPassword;
  }

  @DataBoundSetter
  public void setScmPassword(String scmPassword) {
    this.scmPassword = scmPassword;
  }

  public String getScmUsernameEnvVar() {
    return this.scmUsernameEnvVar;
  }

  @DataBoundSetter
  public void setScmUsernameEnvVar(String scmUsernameEnvVar) {
    this.scmUsernameEnvVar = scmUsernameEnvVar;
  }

  public String getScmPasswordEnvVar() {
    return this.scmPasswordEnvVar;
  }

  @DataBoundSetter
  public void setScmPasswordEnvVar(String scmPasswordEnvVar) {
    this.scmPasswordEnvVar = scmPasswordEnvVar;
  }

  public String getScmSshPassphrase() {
    return this.scmSshPassphrase;
  }

  @DataBoundSetter
  public void setScmSshPassphrase(String scmSshPassphrase) {
    this.scmSshPassphrase = scmSshPassphrase;
  }

  public String getScmSshPassphraseEnvVar() {
    return this.scmSshPassphraseEnvVar;
  }

  @DataBoundSetter
  public void setScmSshPassphraseEnvVar(String scmSshPassphraseEnvVar) {
    this.scmSshPassphraseEnvVar = scmSshPassphraseEnvVar;
  }

  public String getScmSshPrivateKeyEnvVar() {
    return this.scmSshPrivateKeyEnvVar;
  }

  @DataBoundSetter
  public void setScmSshPrivateKeyEnvVar(String scmSshPrivateKeyEnvVar) {
    this.scmSshPrivateKeyEnvVar = scmSshPrivateKeyEnvVar;
  }

  public String getReleaseArgs() {
    return this.releaseArgs;
  }

  @DataBoundSetter
  public void setReleaseArgs(String releaseArgs) {
    this.releaseArgs = releaseArgs;
  }

  public String getMavenProfiles() {
    return this.mavenProfiles;
  }

  @DataBoundSetter
  public void setMavenProfiles(String mavenProfiles) {
    this.mavenProfiles = mavenProfiles;
  }

  public String getWorkflowFilePath() {
    return this.workflowFilePath;
  }

  @DataBoundSetter
  public void setWorkflowFilePath(String workflowFilePath) {
    this.workflowFilePath = workflowFilePath;
  }

  public List<HookDescriptor> getHooks() {
    return this.hooks;
  }

  @DataBoundSetter
  public void setHooks(List<HookDescriptor> hooks) {
    this.hooks = hooks != null ? hooks : new ArrayList<HookDescriptor>();
  }

  public boolean isErrorLog() {
    return this.errorLog;
  }

  @DataBoundSetter
  public void setErrorLog(boolean errorLog) {
    this.errorLog = errorLog;
  }

  public boolean isDebugLog() {
    return this.debugLog;
  }

  @DataBoundSetter
  public void setDebugLog(boolean debugLog) {
    this.debugLog = debugLog;
  }

  @Override
  protected List<String> fieldsToString() {
    List<String> fields = new ArrayList<>();

    fields.add("goal: '" + getGoal() + "'");
    fields.add("releaseVersion: '" + getReleaseVersion() + "'");
    fields.add("nextDevelopmentVersion: '" + getNextDevelopmentVersion() + "'");
    fields.add("commitBeforeTagging: " + isCommitBeforeTagging());
    fields.add("allowLocalReleaseArtifacts: " + isAllowLocalReleaseArtifacts());
    fields.add("useLogTimestamps: " + isUseLogTimestamps());
    fields.add("scmTagNamePattern: '" + getScmTagNamePattern() + "'");
    fields.add("scmMessagePrefix: '" + getScmMessagePrefix() + "'");
    addIfNotNull(fields, "scmUsername", getScmUsername());
    addIfNotNull(fields, "scmPassword", getScmPassword());
    addIfNotNull(fields, "scmUsernameEnvVar", getScmUsernameEnvVar());
    addIfNotNull(fields, "scmPasswordEnvVar", getScmPasswordEnvVar());
    addIfNotNull(fields, "scmSshPassphrase", getScmSshPassphrase());
    addIfNotNull(fields, "scmSshPassphraseEnvVar", getScmSshPassphraseEnvVar());
    addIfNotNull(fields, "scmSshPrivateKeyEnvVar", getScmSshPrivateKeyEnvVar());
    addIfNotNull(fields, "releaseArgs", getReleaseArgs());
    addIfNotNull(fields, "mavenProfiles", getMavenProfiles());
    addIfNotNull(fields, "workflowFilePath", getWorkflowFilePath());
    if (!getHooks().isEmpty()) {
      String s = "hooks: [";
      boolean isFirst = true;
      for (HookDescriptor hook : getHooks()) {
        s += isFirst ? "" : ", ";
        s += "[" + hook.toString() + "]";
        isFirst = false;
      }
      s += "]";
      fields.add(s);
    }
    fields.add("errorLog: " + isErrorLog());
    fields.add("debugLog: " + isDebugLog());

    return fields;
  }

  private void addIfNotNull(List<String> fields, String fieldName, String value) {
    if (value != null) {
      fields.add(fieldName + ": '" + value + "'");
    }
  }

  private UnleashArgumentsAction createArgumentsAction() {
    final UnleashArgumentsAction ret = new UnleashArgumentsAction();

    ret.setUseGlobalReleaseVersion(true);
    ret.setGlobalReleaseVersion(getReleaseVersion());
    ret.setGlobalDevelopmentVersion(getNextDevelopmentVersion());
    ret.setCommitBeforeTagging(isCommitBeforeTagging());
    ret.setAllowLocalReleaseArtifacts(isAllowLocalReleaseArtifacts());
    ret.setTagNamePattern(getScmTagNamePattern());
    ret.setScmMessagePrefix(getScmMessagePrefix());
    ret.setErrorLog(isErrorLog());
    ret.setDebugLog(isDebugLog());

    return ret;
  }

  private UnleashScmCredentialArguments createCredentialArguments() {
    final UnleashScmCredentialArguments ret = new UnleashScmCredentialArguments();

    ret.setUseSshCredentials(
        StringUtils.isNotBlank(getScmSshPassphrase()) || StringUtils.isNotBlank(getScmSshPassphraseEnvVar()));
    ret.setPassphrase(ret.isUseSshCredentials() ? getScmSshPassphrase() : getScmPassword());
    ret.setPassphraseEnvVar(ret.isUseSshCredentials() ? getScmSshPassphraseEnvVar() : getScmPasswordEnvVar());
    ret.setUserNameOrSshPrivateKey(ret.isUseSshCredentials() ? "" : getScmUsername());
    ret.setUserNameOrSshPrivateKeyEnvVar(
        ret.isUseSshCredentials() ? getScmSshPrivateKeyEnvVar() : getScmUsernameEnvVar());

    return ret;
  }

  @Override
  protected String run(Run<?, ?> build) throws Exception {
    final UnleashArgumentsAction argumentsAction = createArgumentsAction();
    final UnleashScmCredentialArguments scmCredentialArguments = createCredentialArguments();
    final Pair<Map<String, String>, StringBuilder> p = BuildUtil.buildScmEnvAndUnleashCommandOptions(// nl
        build.getAction(ParametersAction.class), // #1
        getGoal(), // #2
        getWorkflowFilePath(), // #3
        getMavenProfiles(), // #4
        getReleaseArgs(), // #5
        isUseLogTimestamps(), // #6
        getHooks(), // #7
        getScmTagNamePattern(), // #8
        getScmMessagePrefix(), // #9
        VersionUpgradeStrategy.DEFAULT, // #10
        argumentsAction, // #11
        scmCredentialArguments // #12
    );
    return p.getRight().toString();
  }

  @Extension
  public static class DescriptorImpl extends AbstractTaskListenerDescriptor {

    @Override
    public String getFunctionName() {
      return "unleashCommandArgs";
    }

    @NonNull
    @Override
    public String getDisplayName() {
      return "Build Unleash commandline args";
    }

  }

}
