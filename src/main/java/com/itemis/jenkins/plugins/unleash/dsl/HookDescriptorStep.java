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

import org.apache.commons.lang3.StringUtils;
import org.jenkinsci.plugins.workflow.steps.Step;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepExecution;
import org.jenkinsci.plugins.workflow.steps.SynchronousStepExecution;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import com.itemis.jenkins.plugins.unleash.HookDescriptor;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;

public class HookDescriptorStep extends Step {

  private String name;
  private String data;
  private String rollbackData;

  /**
   * @param name The name of the hook as being used within a workflow
   * @param data The data of the hook
   */
  @DataBoundConstructor
  public HookDescriptorStep(String name, String data) {
    this.name = name;
    this.data = data;
    this.rollbackData = StringUtils.EMPTY;
  }

  public String getName() {
    return this.name;
  }

  public String getData() {
    return this.data;
  }

  /**
   * @param rollbackData the hook rollback data
   */
  @DataBoundSetter
  public void setRollbackData(String rollbackData) {
    this.rollbackData = rollbackData;
  }

  public String getRollbackData() {
    return this.rollbackData;
  }

  @Override
  public String toString() {
    List<String> fields = new ArrayList<>();

    if (getName() != null) {
      fields.add("name: '" + getName() + "'");
    }
    if (getData() != null) {
      fields.add("data: '" + getData() + "'");
    }
    if (getRollbackData() != null) {
      fields.add("rollbackData: '" + getRollbackData() + "'");
    }

    return getDescriptor().getFunctionName() + "(" + StringUtils.join(fields, ", ") + ")";
  }

  @Override
  public StepExecution start(StepContext context) throws Exception {
    return new SynchronousStepExecution<HookDescriptor>(context) {

      private static final long serialVersionUID = 1L;

      private final HookDescriptorStep step = HookDescriptorStep.this;

      @Override
      protected HookDescriptor run() throws Exception {
        return new HookDescriptor(this.step.getName(), this.step.getData(), this.step.getRollbackData());
      }

    };
  }

  @Extension
  public static class DescriptorImpl extends AbstractTaskListenerDescriptor {

    @Override
    public String getFunctionName() {
      return "unleashHook";
    }

    @NonNull
    @Override
    public String getDisplayName() {
      return "Create an Unleash Descriptor";
    }

  }

}
