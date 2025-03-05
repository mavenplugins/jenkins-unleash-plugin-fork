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
package com.itemis.jenkins.plugins.unleash.util;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.itemis.jenkins.plugins.unleash.HookDescriptor;
import com.itemis.jenkins.plugins.unleash.UnleashArgumentsAction;
import com.itemis.jenkins.plugins.unleash.UnleashBadgeAction;
import com.itemis.jenkins.plugins.unleash.UnleashScmCredentialArguments;
import com.itemis.maven.plugins.unleash.util.VersionUpgradeStrategy;

import hudson.model.ParameterValue;
import hudson.model.ParametersAction;
import hudson.model.Result;
import hudson.model.Run;
import hudson.util.RunList;

/**
 * @author <a href="mailto:mhoffrogg@gmail.com">Markus Hoffrogge</a>
 */
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

  /**
   * Build command line options and probable environment variables for the Maven unleash goal execution.
   *
   * @param buildParametersAction   The current builds parameter action
   * @param unleashGoals            e.g. unleash:perform
   * @param workflowPath            Optional: the unleash workflow file to be used
   * @param mavenProfiles           Optional: Maven profiles to be activated
   * @param unleashReleaseArgs      Optional: additional unleash release args
   * @param isUseLogTimestamps      true or false
   * @param unleashHooks            Optional: list of unleash hook definitions
   * @param defaultTagNamePattern   Optional: the tag name pattern, if argumentsAction below is null
   * @param defaultScmMessagePrefix Optional: the SCM message prefix, if argumentsAction below is null
   * @param versionUpgradeStrategy  The version upgrade strategy for automatic release and next version calculation
   * @param argumentsAction         {@link UnleashArgumentsAction} defining job config properties
   * @param scmCredentialArguments  {@link UnleashScmCredentialArguments} as configured by the job
   * @return {@link Pair} of (scm environment, command options)
   */
  public static Pair<Map<String, String>, StringBuilder> buildScmEnvAndUnleashCommandOptions(// nl
      final ParametersAction buildParametersAction, // #1
      final String unleashGoals, // #2
      final String workflowPath, // #3
      final String mavenProfiles, // #4
      final String unleashReleaseArgs, // #5
      final boolean isUseLogTimestamps, // #6
      final List<HookDescriptor> unleashHooks, // #7
      final String defaultTagNamePattern, // #8 can be null, if following arguments is not null
      final String defaultScmMessagePrefix, // #9 can be null, if following arguments is not null
      final VersionUpgradeStrategy versionUpgradeStrategy, // #10
      final UnleashArgumentsAction argumentsAction, // #11
      final UnleashScmCredentialArguments scmCredentialArguments // #12
  ) {

    // goals
    final StringBuilder commandOptions = new StringBuilder(unleashGoals);

    if (argumentsAction != null) {
      if (argumentsAction.errorLog()) {
        commandOptions.append(" -e");
      }
      if (argumentsAction.debugLog()) {
        commandOptions.append(" -X");
      }
    }

    // workflow
    if (StringUtils.isNotBlank(workflowPath)) {
      commandOptions.append(" -Dworkflow=\"").append(workflowPath).append("\"");
    }

    // appends the profiles to the Maven call
    if (StringUtils.isNotBlank(mavenProfiles)) {
      Iterable<String> split = Splitter.on(',').split(mavenProfiles);
      List<String> profiles = Lists.newArrayList();
      for (String profile : split) {
        if (StringUtils.isNotBlank(profile)) {
          profile = profile.trim();
          if (StringUtils.startsWith(profile, "-")) {
            profile = StringUtils.replaceOnce(profile, "-", "!");
          }
          profiles.add(profile);
        }
      }
      if (profiles.size() > 0) {
        String listedProfiles = Joiner.on(',').join(profiles);
        commandOptions.append(" -P ").append(listedProfiles);
        commandOptions.append(" -Dunleash.profiles=").append(listedProfiles);
      }
    }

    // releaseArgs
    if (StringUtils.isNotBlank(unleashReleaseArgs)) {
      commandOptions.append(" -Dunleash.releaseArgs=\"").append(unleashReleaseArgs.trim()).append("\"");
    }

    // enableLogTimestamps
    commandOptions.append(" -DenableLogTimestamps=").append(isUseLogTimestamps);

    // hooks
    if (unleashHooks != null) {
      for (HookDescriptor hookData : unleashHooks) {
        if (StringUtils.isNotBlank(hookData.getName()) && StringUtils.isNotBlank(hookData.getData())) {
          if (StringUtils.containsWhitespace(hookData.getName())) {
            throw new IllegalArgumentException(
                "Illegal hook name='" + hookData.getName() + "' - must NOT contain whitespace characters!");
          }
          commandOptions.append(" -D").append(hookData.getName()).append("=\"").append(hookData.getData()).append("\"");
          if (StringUtils.isNotBlank(hookData.getRollbackData())) {
            commandOptions.append(" -D").append(hookData.getName()).append("-rollback=\"")
                .append(hookData.getRollbackData()).append("\"");
          }
        }
      }
    }

    // arguments
    String tagNamePattern = defaultTagNamePattern;
    String scmMessagePrefix = defaultScmMessagePrefix;
    if (argumentsAction != null) {
      tagNamePattern = argumentsAction.getTagNamePattern();
      scmMessagePrefix = argumentsAction.getScmMessagePrefix();
      if (argumentsAction.useGlobalReleaseVersion()) {
        commandOptions.append(" -Dunleash.releaseVersion=").append(argumentsAction.getGlobalReleaseVersion());
        commandOptions.append(" -Dunleash.developmentVersion=").append(argumentsAction.getGlobalDevelopmentVersion());
      } else {
        commandOptions.append(" -Dunleash.versionUpgradeStrategy=").append(versionUpgradeStrategy.name());
      }
      commandOptions.append(" -Dunleash.allowLocalReleaseArtifacts=")
          .append(argumentsAction.allowLocalReleaseArtifacts());
      commandOptions.append(" -Dunleash.commitBeforeTagging=").append(argumentsAction.commitBeforeTagging());
    }
    if (StringUtils.isNotBlank(tagNamePattern)) {
      commandOptions.append(" -Dunleash.tagNamePattern=\"").append(tagNamePattern.trim()).append("\"");
    }
    if (StringUtils.isNotBlank(scmMessagePrefix)) {
      commandOptions.append(" -Dunleash.scmMessagePrefix=\"").append(scmMessagePrefix.trim()).append("\"");
    }

    // SCM credentials
    final Map<String, String> scmEnv = updateCommandOptionsWithScmCredentials(scmCredentialArguments, commandOptions);

    replaceJobParameterReferences(buildParametersAction, commandOptions);

    return Pair.of(scmEnv, commandOptions);
  }

  private static Map<String, String> updateCommandOptionsWithScmCredentials(
      UnleashScmCredentialArguments scmCredentialArguments, StringBuilder commandOptions) {
    final Map<String, String> scmEnv = Maps.newHashMap();
    final String userNameOrSshPrivateKey = scmCredentialArguments.getUserNameOrSshPrivateKey();
    final String passphrase = scmCredentialArguments.getPassphrase();
    final String userNameOrSshPrivateKeyEnvVar = scmCredentialArguments.getUserNameOrSshPrivateKeyEnvVar();
    final String passphraseEnvVar = scmCredentialArguments.getPassphraseEnvVar();
    if (scmCredentialArguments.isUseSshCredentials()) {
      if (userNameOrSshPrivateKey != null) {
        if (userNameOrSshPrivateKeyEnvVar != null) {
          commandOptions.append(" -Dunleash.scmSshPrivateKeyEnvVar=" + userNameOrSshPrivateKeyEnvVar);
          scmEnv.put(userNameOrSshPrivateKeyEnvVar, userNameOrSshPrivateKey);
        } else {
          // Note: System property unleash.scmSshPrivateKey not yet supported by unleash Maven plugin
          // commandOptions.append(" -Dunleash.scmSshPrivateKey=" + userNameOrSshPrivateKey);
        }
      }
      if (passphrase != null) {
        if (passphraseEnvVar != null) {
          commandOptions.append(" -Dunleash.scmSshPassphraseEnvVar=" + passphraseEnvVar);
          scmEnv.put(passphraseEnvVar, passphrase);
        } else {
          commandOptions.append(" -Dunleash.scmSshPassphrase=" + passphrase);
        }
      }
    } else {
      if (userNameOrSshPrivateKey != null) {
        if (userNameOrSshPrivateKeyEnvVar != null) {
          commandOptions.append(" -Dunleash.scmUsernameEnvVar=" + userNameOrSshPrivateKeyEnvVar);
          scmEnv.put(userNameOrSshPrivateKeyEnvVar, userNameOrSshPrivateKey);
        } else {
          commandOptions.append(" -Dunleash.scmUsername=" + userNameOrSshPrivateKey);
        }
      }
      if (passphrase != null) {
        if (passphraseEnvVar != null) {
          commandOptions.append(" -Dunleash.scmPasswordEnvVar=" + passphraseEnvVar);
          scmEnv.put(passphraseEnvVar, passphrase);
        } else {
          commandOptions.append(" -Dunleash.scmPassword=" + passphrase);
        }
      }
    }
    return scmEnv;
  }

  private static void replaceJobParameterReferences(final ParametersAction buildParametersAction,
      StringBuilder commandOptions) {
    if (buildParametersAction != null && commandOptions != null) {
      int start = commandOptions.indexOf("${");
      while (start >= 0) {
        int end = commandOptions.indexOf("}", start);
        String name = commandOptions.substring(start + 2, end);
        ParameterValue paramValue = buildParametersAction.getParameter(name);
        if (paramValue != null) {
          Object value = paramValue.getValue();
          if (value != null) {
            commandOptions.replace(start, end + 1, value.toString());
          }
        }
        start = commandOptions.indexOf("${", end);
      }
    }
  }

}
