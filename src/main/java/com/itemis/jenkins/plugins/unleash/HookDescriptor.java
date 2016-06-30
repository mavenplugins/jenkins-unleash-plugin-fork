package com.itemis.jenkins.plugins.unleash;

import org.kohsuke.stapler.DataBoundConstructor;

public class HookDescriptor {
  String name;
  String data;
  String rollbackData;

  public HookDescriptor() {

  }

  @DataBoundConstructor
  public HookDescriptor(String name, String data, String rollbackData) {
    this.name = name;
    this.data = data;
    this.rollbackData = rollbackData;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getData() {
    return this.data;
  }

  public void setData(String data) {
    this.data = data;
  }

  public String getRollbackData() {
    return this.rollbackData;
  }

  public void setRollbackData(String rollbackData) {
    this.rollbackData = rollbackData;
  }
}
