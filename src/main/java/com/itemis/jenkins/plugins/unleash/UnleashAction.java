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
package com.itemis.jenkins.plugins.unleash;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.ServletException;

import org.apache.commons.lang.StringUtils;
import org.apache.maven.model.Model;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.itemis.jenkins.plugins.unleash.permalinks.LastFailedReleasePermalink;
import com.itemis.jenkins.plugins.unleash.permalinks.LastSuccessfulReleasePermalink;
import com.itemis.jenkins.plugins.unleash.util.MavenUtil;
import com.itemis.maven.plugins.unleash.util.MavenVersionUtil;
import com.itemis.maven.plugins.unleash.util.VersionUpgradeStrategy;

import hudson.maven.MavenModule;
import hudson.maven.MavenModuleSet;
import hudson.model.PermalinkProjectAction;

/**
 * @author Stanley Hillner
 */
// This class was developed based on org.jvnet.hudson.plugins.m2release.M2ReleaseAction
// The class still contains substantial parts of the original implementation
// original authors: James Nord & Dominik Bartholdi
public class UnleashAction implements PermalinkProjectAction {
  private static final Logger LOGGER = Logger.getLogger(UnleashAction.class.getName());

  private MavenModuleSet project;
  private boolean useGlobalVersion;
  private boolean allowLocalReleaseArtifacts;
  private boolean commitBeforeTagging;
  private boolean errorLog;
  private boolean debugLog;
  private VersionUpgradeStrategy versionUpgradeStrategy;

  public UnleashAction(MavenModuleSet project, boolean useGlobalVersion, boolean allowLocalReleaseArtifacts,
      boolean commitBeforeTagging, boolean errorLog, boolean debugLog, VersionUpgradeStrategy versionUpgradeStrategy) {
    this.project = project;
    this.useGlobalVersion = useGlobalVersion;
    this.allowLocalReleaseArtifacts = allowLocalReleaseArtifacts;
    this.commitBeforeTagging = commitBeforeTagging;
    this.errorLog = errorLog;
    this.debugLog = debugLog;
    this.versionUpgradeStrategy = versionUpgradeStrategy;
  }

  @Override
  public String getIconFileName() {
    return "/plugin/unleash/img/unleash.png";
  }

  @Override
  public String getDisplayName() {
    return "Trigger Unleash Maven Plugin";
  }

  @Override
  public String getUrlName() {
    return "unleash";
  }

  @Override
  public List<Permalink> getPermalinks() {
    return Lists.newArrayList(LastSuccessfulReleasePermalink.INSTANCE, LastFailedReleasePermalink.INSTANCE);
  }

  public String computeReleaseVersion() {
    String version = null;
    Optional<Model> model = MavenUtil.parseModel(this.project.getRootModule(), this.project);
    if (model.isPresent()) {
      Optional<String> parsedVersion = MavenUtil.parseVersion(model.get());
      if (parsedVersion.isPresent()) {
        version = MavenVersionUtil.calculateReleaseVersion(parsedVersion.get());
      }
    }

    if (StringUtils.isBlank(version)) {
      MavenModule rootModule = this.project.getRootModule();
      if (rootModule != null && StringUtils.isNotBlank(rootModule.getVersion())) {
        version = MavenVersionUtil.calculateReleaseVersion(rootModule.getVersion());
      }
    }

    if (StringUtils.isNotBlank(version)) {
      return version;
    }
    return "NaN";
  }

  public String computeReleaseVersion(MavenModule module) {
    String version = null;
    if (module != null) {
      Optional<Model> model = MavenUtil.parseModel(module, this.project);
      if (model.isPresent()) {
        Optional<String> parsedVersion = MavenUtil.parseVersion(model.get());
        if (parsedVersion.isPresent()) {
          version = MavenVersionUtil.calculateReleaseVersion(parsedVersion.get());
        }
      }

      if (StringUtils.isBlank(version) && StringUtils.isNotBlank(module.getVersion())) {
        version = MavenVersionUtil.calculateReleaseVersion(module.getVersion());
      }
    }

    if (StringUtils.isNotBlank(version)) {
      return version;
    }
    return "NaN";
  }

  public String computeNextDevelopmentVersion() {
    String version = null;
    Optional<Model> model = MavenUtil.parseModel(this.project.getRootModule(), this.project);
    if (model.isPresent()) {
      Optional<String> parsedVersion = MavenUtil.parseVersion(model.get());
      if (parsedVersion.isPresent()) {
        version = MavenVersionUtil.calculateNextSnapshotVersion(parsedVersion.get(), this.versionUpgradeStrategy);
      }
    }

    if (StringUtils.isBlank(version)) {
      MavenModule rootModule = this.project.getRootModule();
      if (rootModule != null && StringUtils.isNotBlank(rootModule.getVersion())) {
        version = MavenVersionUtil.calculateNextSnapshotVersion(rootModule.getVersion(), this.versionUpgradeStrategy);
      }
    }

    if (StringUtils.isNotBlank(version)) {
      return version;
    }
    return "NaN";
  }

  public String computeNextDevelopmentVersion(MavenModule module) {
    String version = null;
    if (module != null) {
      Optional<Model> model = MavenUtil.parseModel(this.project.getRootModule(), this.project);
      if (model.isPresent()) {
        Optional<String> parsedVersion = MavenUtil.parseVersion(model.get());
        if (parsedVersion.isPresent()) {
          version = MavenVersionUtil.calculateNextSnapshotVersion(parsedVersion.get(), this.versionUpgradeStrategy);
        }
      }

      if (StringUtils.isBlank(version) && StringUtils.isNotBlank(module.getVersion())) {
        version = MavenVersionUtil.calculateNextSnapshotVersion(module.getVersion(), this.versionUpgradeStrategy);
      }
    }

    if (StringUtils.isNotBlank(version)) {
      return version;
    }
    return "NaN";

  }

  public boolean isUseGlobalVersion() {
    return this.useGlobalVersion;
  }

  public boolean isNotUseGlobalVersion() {
    return !this.useGlobalVersion;
  }

  public void setUseGlobalVersion(boolean useGlobalVersion) {
    this.useGlobalVersion = useGlobalVersion;
  }

  public boolean isAllowLocalReleaseArtifacts() {
    return this.allowLocalReleaseArtifacts;
  }

  public void setAllowLocalReleaseArtifacts(boolean allowLocalReleaseArtifacts) {
    this.allowLocalReleaseArtifacts = allowLocalReleaseArtifacts;
  }

  public boolean isCommitBeforeTagging() {
    return this.commitBeforeTagging;
  }

  public void setCommitBeforeTagging(boolean commitBeforeTagging) {
    this.commitBeforeTagging = commitBeforeTagging;
  }

  public boolean isErrorLog() {
    return this.errorLog;
  }

  public void setErrorLog(boolean errorLog) {
    this.errorLog = errorLog;
  }

  public boolean isDebugLog() {
    return this.debugLog;
  }

  public void setDebugLog(boolean debugLog) {
    this.debugLog = debugLog;
  }

  public List<MavenModule> getAllMavenModules() {
    List<MavenModule> modules = Lists.newArrayList();
    modules.addAll(this.project.getModules());
    return modules;
  }

  public void doSubmit(StaplerRequest req, StaplerResponse resp) throws IOException, ServletException {
    RequestWrapper requestWrapper = new RequestWrapper(req);

    UnleashArgumentsAction arguments = new UnleashArgumentsAction();
    boolean globalVersions = requestWrapper.getBoolean("useGlobalVersion");

    arguments.setUseGlobalReleaseVersion(globalVersions);
    if (globalVersions) {
      arguments.setGlobalReleaseVersion(requestWrapper.getString("releaseVersion"));
      arguments.setGlobalDevelopmentVersion(requestWrapper.getString("developmentVersion"));
    } else {
      arguments.setGlobalReleaseVersion(computeReleaseVersion());
      arguments.setGlobalDevelopmentVersion(computeNextDevelopmentVersion());
    }

    arguments.setAllowLocalReleaseArtifacts(requestWrapper.getBoolean("allowLocalReleaseArtifacts"));
    arguments.setCommitBeforeTagging(requestWrapper.getBoolean("commitBeforeTagging"));
    arguments.setErrorLog(requestWrapper.getBoolean("errorLog"));
    arguments.setDebugLog(requestWrapper.getBoolean("debugLog"));

    if (this.project.scheduleBuild(0, new UnleashCause(), arguments)) {
      resp.sendRedirect(req.getContextPath() + '/' + this.project.getUrl());
    } else {
      resp.sendRedirect(req.getContextPath() + '/' + this.project.getUrl() + '/' + getUrlName() + "/failed");
    }
  }

  static class RequestWrapper {
    private final StaplerRequest request;

    public RequestWrapper(StaplerRequest request) throws ServletException {
      this.request = request;
    }

    private String getString(String key) throws javax.servlet.ServletException, java.io.IOException {
      Map parameters = this.request.getParameterMap();
      Object o = parameters.get(key);
      if (o != null) {
        if (o instanceof String) {
          return (String) o;
        } else if (o.getClass().isArray()) {
          Object firstParam = ((Object[]) o)[0];
          if (firstParam instanceof String) {
            return (String) firstParam;
          }
        }
      }
      return null;
    }

    private boolean getBoolean(String key) {
      Map parameters = this.request.getParameterMap();
      String flag = null;

      Object o = parameters.get(key);
      if (o != null) {
        if (o instanceof String) {
          flag = (String) o;
        } else if (o.getClass().isArray()) {
          Object firstParam = ((Object[]) o)[0];
          if (firstParam instanceof String) {
            flag = (String) firstParam;
          }
        }
      }
      return Objects.equal("true", flag) || Objects.equal("on", flag);
    }
  }
}
