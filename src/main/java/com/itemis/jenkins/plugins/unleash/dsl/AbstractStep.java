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

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jenkinsci.plugins.workflow.steps.Step;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepExecution;
import org.jenkinsci.plugins.workflow.steps.SynchronousStepExecution;

import hudson.model.Run;

abstract class AbstractStep<RETURN_TYPE> extends Step {

  /**
   * @return fields map for {@link AbstractStep#toString()} like "propName: 'propValue'"
   */
  protected abstract List<String> fieldsToString();

  protected abstract RETURN_TYPE run(Run<?, ?> build) throws Exception;

  @Override
  public String toString() {
    return getDescriptor().getFunctionName() + "(" + StringUtils.join(fieldsToString(), ", ") + ")";
  }

  @Override
  public StepExecution start(StepContext context) throws Exception {
    return new SynchronousStepExecution<RETURN_TYPE>(context) {

      private static final long serialVersionUID = 1L;

      @Override
      protected RETURN_TYPE run() throws Exception {
        Run<?, ?> build = getContext().get(Run.class);
        if (build != null) {
          return AbstractStep.this.run(build);
        }
        return null;
      }

    };
  }

}
