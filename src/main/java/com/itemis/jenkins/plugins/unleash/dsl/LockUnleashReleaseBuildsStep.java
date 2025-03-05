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

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import com.itemis.jenkins.plugins.unleash.util.BuildUtil;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.model.Run;

public class LockUnleashReleaseBuildsStep extends AbstractStep<Void> {

  private int numberOfBuildsToLock;

  /**
   * @param version The release version
   */
  @DataBoundConstructor
  public LockUnleashReleaseBuildsStep() {
    this.numberOfBuildsToLock = 1;
  }

  public int getNumberOfBuildsToLock() {
    return this.numberOfBuildsToLock;
  }

  /**
   * @param version the release version
   */
  @DataBoundSetter
  public void setNumberOfBuildsToLock(int numberOfBuildsToLock) {
    this.numberOfBuildsToLock = numberOfBuildsToLock;
  }

  @Override
  protected List<String> fieldsToString() {
    List<String> fields = new ArrayList<>();

    fields.add("numberOfBuildsToLock: " + getNumberOfBuildsToLock());

    return fields;
  }

  @Override
  protected Void run(Run<?, ?> build) throws Exception {
    BuildUtil.lockSucceededReleaseBuilds(build, getNumberOfBuildsToLock());
    return null;
  }

  @Extension
  public static class DescriptorImpl extends AbstractTaskListenerDescriptor {

    @Override
    public String getFunctionName() {
      return "unleashLockReleaseBuilds";
    }

    @NonNull
    @Override
    public String getDisplayName() {
      return "Lock Unleash Release Builds";
    }

  }

}
