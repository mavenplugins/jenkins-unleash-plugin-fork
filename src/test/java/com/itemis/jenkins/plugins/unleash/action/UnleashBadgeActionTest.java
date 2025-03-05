/*
 * The MIT License
 *
 * Copyright (c) 2024, Unleash Plugin Authors
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
package com.itemis.jenkins.plugins.unleash.action;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;
import org.mockito.Mockito;

import com.itemis.jenkins.plugins.unleash.UnleashBadgeAction;

import hudson.model.Result;
import hudson.model.Run;

@WithJenkins
public class UnleashBadgeActionTest {

  @Test
  void version(JenkinsRule r) {
    UnleashBadgeAction action = createAction(null, null);

    assertNotNull(action.getVersion());
    assertEquals(UnleashBadgeAction.UNKNWON_VERSION, action.getVersion());

    action = createAction("1.0", null);
    assertEquals("1.0", action.getVersion());

    action = createAction(StringUtils.EMPTY, null);
    assertEquals(UnleashBadgeAction.UNKNWON_VERSION, action.getVersion());

    assertActionOverrides(action);
  }

  @Test
  void dryRun(JenkinsRule r) {

    UnleashBadgeAction action = createAction(null, null);
    assertFalse(action.isDryRun());

    action = createAction(null, false);
    assertFalse(action.isDryRun());

    action = createAction(null, true);
    assertTrue(action.isDryRun());

    assertActionOverrides(action);
  }

  @Test
  void noBuild(JenkinsRule r) {

    UnleashBadgeAction action = createAction("1.0", null);

    assertFalse(action.isBuilding());
    assertFalse(action.isFailedBuild());
    assertFalse(action.isUnstableDryRunBuild());
    assertFalse(action.isUnstableReleaseBuild());
    assertFalse(action.isSuccessfulDryRunBuild());
    assertFalse(action.isSuccessfulReleaseBuild());
  }

  @Test
  void buildingBuild(JenkinsRule r) {

    UnleashBadgeAction action = createAction("1.0", null);
    final Run<?, ?> run = attachMockedRun(action);
    Mockito.when(run.isBuilding()).thenReturn(true);
    Mockito.when(run.getResult()).thenReturn(null);

    assertTrue(action.isBuilding());
    assertFalse(action.isFailedBuild());
    assertFalse(action.isUnstableDryRunBuild());
    assertFalse(action.isUnstableReleaseBuild());
    assertFalse(action.isSuccessfulDryRunBuild());
    assertFalse(action.isSuccessfulReleaseBuild());
  }

  @Test
  void successBuild(JenkinsRule r) {

    UnleashBadgeAction action = createAction("1.0", null);
    final Run<?, ?> run = attachMockedRun(action);
    Mockito.when(run.isBuilding()).thenReturn(false);
    Mockito.when(run.getResult()).thenReturn(Result.SUCCESS);

    assertFalse(action.isBuilding());
    assertFalse(action.isFailedBuild());
    assertFalse(action.isUnstableDryRunBuild());
    assertFalse(action.isUnstableReleaseBuild());
    assertFalse(action.isSuccessfulDryRunBuild());
    assertTrue(action.isSuccessfulReleaseBuild());

    action.setDryRun(true);
    assertFalse(action.isBuilding());
    assertFalse(action.isFailedBuild());
    assertFalse(action.isUnstableDryRunBuild());
    assertFalse(action.isUnstableReleaseBuild());
    assertTrue(action.isSuccessfulDryRunBuild());
    assertFalse(action.isSuccessfulReleaseBuild());
  }

  @Test
  void unstableBuild(JenkinsRule r) {

    UnleashBadgeAction action = createAction("1.0", null);
    final Run<?, ?> run = attachMockedRun(action);
    Mockito.when(run.isBuilding()).thenReturn(false);
    Mockito.when(run.getResult()).thenReturn(Result.UNSTABLE);

    assertFalse(action.isBuilding());
    assertFalse(action.isFailedBuild());
    assertFalse(action.isUnstableDryRunBuild());
    assertTrue(action.isUnstableReleaseBuild());
    assertFalse(action.isSuccessfulDryRunBuild());
    assertFalse(action.isSuccessfulReleaseBuild());

    action.setDryRun(true);
    assertFalse(action.isBuilding());
    assertFalse(action.isFailedBuild());
    assertTrue(action.isUnstableDryRunBuild());
    assertFalse(action.isUnstableReleaseBuild());
    assertFalse(action.isSuccessfulDryRunBuild());
    assertFalse(action.isSuccessfulReleaseBuild());
  }

  @Test
  void failureBuild(JenkinsRule r) {

    UnleashBadgeAction action = createAction("1.0", null);
    final Run<?, ?> run = attachMockedRun(action);
    Mockito.when(run.isBuilding()).thenReturn(false);
    Mockito.when(run.getResult()).thenReturn(Result.FAILURE);

    assertFalse(action.isBuilding());
    assertTrue(action.isFailedBuild());
    assertFalse(action.isUnstableDryRunBuild());
    assertFalse(action.isUnstableReleaseBuild());
    assertFalse(action.isSuccessfulDryRunBuild());
    assertFalse(action.isSuccessfulReleaseBuild());

    action.setDryRun(true);
    assertFalse(action.isBuilding());
    assertTrue(action.isFailedBuild());
    assertFalse(action.isUnstableDryRunBuild());
    assertFalse(action.isUnstableReleaseBuild());
    assertFalse(action.isSuccessfulDryRunBuild());
    assertFalse(action.isSuccessfulReleaseBuild());
  }

  @Test
  void notBuiltBuild(JenkinsRule r) {

    UnleashBadgeAction action = createAction("1.0", null);
    final Run<?, ?> run = attachMockedRun(action);
    Mockito.when(run.isBuilding()).thenReturn(false);
    Mockito.when(run.getResult()).thenReturn(Result.NOT_BUILT);

    assertFalse(action.isBuilding());
    assertTrue(action.isFailedBuild());
    assertFalse(action.isUnstableDryRunBuild());
    assertFalse(action.isUnstableReleaseBuild());
    assertFalse(action.isSuccessfulDryRunBuild());
    assertFalse(action.isSuccessfulReleaseBuild());

    action.setDryRun(true);
    assertFalse(action.isBuilding());
    assertTrue(action.isFailedBuild());
    assertFalse(action.isUnstableDryRunBuild());
    assertFalse(action.isUnstableReleaseBuild());
    assertFalse(action.isSuccessfulDryRunBuild());
    assertFalse(action.isSuccessfulReleaseBuild());
  }

  @Test
  void abortedBuild(JenkinsRule r) {

    UnleashBadgeAction action = createAction("1.0", null);
    final Run<?, ?> run = attachMockedRun(action);
    Mockito.when(run.isBuilding()).thenReturn(false);
    Mockito.when(run.getResult()).thenReturn(Result.ABORTED);

    assertFalse(action.isBuilding());
    assertTrue(action.isFailedBuild());
    assertFalse(action.isUnstableDryRunBuild());
    assertFalse(action.isUnstableReleaseBuild());
    assertFalse(action.isSuccessfulDryRunBuild());
    assertFalse(action.isSuccessfulReleaseBuild());

    action.setDryRun(true);
    assertFalse(action.isBuilding());
    assertTrue(action.isFailedBuild());
    assertFalse(action.isUnstableDryRunBuild());
    assertFalse(action.isUnstableReleaseBuild());
    assertFalse(action.isSuccessfulDryRunBuild());
    assertFalse(action.isSuccessfulReleaseBuild());
  }

  protected Run<?, ?> attachMockedRun(UnleashBadgeAction action) {
    final Run<?, ?> run = Mockito.mock(Run.class);
    action.onAttached(run);
    return run;
  }

  protected void assertActionOverrides(UnleashBadgeAction action) {
    assertNull(action.getDisplayName());
    assertNull(action.getIconFileName());
    assertNull(action.getUrlName());
  }

  protected UnleashBadgeAction createAction(String version, Boolean isDryRun) {
    UnleashBadgeAction ret = new UnleashBadgeAction(version);
    if (isDryRun instanceof Boolean) {
      ret.setDryRun(isDryRun);
    }
    return ret;
  }
}
