package com.itemis.jenkins.plugins.unleash;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.ServletException;

import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.itemis.maven.plugins.unleash.util.MavenVersionUtil;

import hudson.maven.MavenModule;
import hudson.maven.MavenModuleSet;
import hudson.model.PermalinkProjectAction;

public class UnleashAction implements PermalinkProjectAction {
  private static final Logger LOGGER = Logger.getLogger(UnleashAction.class.getName());

  private MavenModuleSet project;
  private boolean useCustomScmCredentials;
  private boolean useGlobalVersion;

  public UnleashAction(MavenModuleSet project, boolean useCustomScmCredentials, boolean useGlobalVersion) {
    this.project = project;
    this.useCustomScmCredentials = useCustomScmCredentials;
    this.useGlobalVersion = useGlobalVersion;
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
    String version = "NaN";
    final MavenModule rootModule = this.project.getRootModule();
    if (rootModule != null && StringUtils.isNotBlank(rootModule.getVersion())) {
      version = MavenVersionUtil.calculateReleaseVersion(rootModule.getVersion());
    }
    return version;
  }

  public String computeNextDevelopmentVersion() {
    String version = "NaN";
    final MavenModule rootModule = this.project.getRootModule();
    if (rootModule != null && StringUtils.isNotBlank(rootModule.getVersion())) {
      version = MavenVersionUtil.calculateNextSnapshotVersion(rootModule.getVersion());
    }
    return version;
  }

  public boolean isUseCustomScmCredentials() {
    return this.useCustomScmCredentials;
  }

  public void setUseCustomScmCredentials(boolean useCustomScmCredentials) {
    this.useCustomScmCredentials = useCustomScmCredentials;
  }

  public boolean isUseGlobalVersion() {
    return this.useGlobalVersion;
  }

  public void setUseGlobalVersion(boolean useGlobalVersion) {
    this.useGlobalVersion = useGlobalVersion;
  }

  public void doSubmit(StaplerRequest req, StaplerResponse resp) throws IOException, ServletException {
    // JSON collapses everything in the dynamic specifyVersions section so
    // we need to fall back to
    // good old http...
    RequestWrapper requestWrapper = new RequestWrapper(req);

    UnleashArgumentsAction arguments = new UnleashArgumentsAction();
    boolean globalVersions = requestWrapper.getBoolean("useGlobalVersion");
    if (globalVersions) {
      arguments.setGlobalReleaseVersion(requestWrapper.getString("releaseVersion"));
      arguments.setGlobalDevelopmentVersion(requestWrapper.getString("developmentVersion"));
    }
    boolean customCredentials = requestWrapper.getBoolean("setScmCredentials");
    if (customCredentials) {
      arguments.setScmUsername(requestWrapper.getString("scmUsername"));
      arguments.setScmPassword(requestWrapper.getString("scmPassword"));
    }

    if (this.project.scheduleBuild(0, new UnleashCause(), arguments)) {
      resp.sendRedirect(req.getContextPath() + '/' + this.project.getUrl());
    } else {
      // redirect to error page.
      // TODO try and get this to go back to the form page with an
      // error at the top.
      resp.sendRedirect(req.getContextPath() + '/' + this.project.getUrl() + '/' + getUrlName() + "/failed");
    }
  }

  /**
   * Wrapper to access request data with a special treatment if POST is multipart encoded
   */
  static class RequestWrapper {
    private final StaplerRequest request;
    // private Map<String, FileItem> parsedFormData;
    // private boolean isMultipartEncoded;

    public RequestWrapper(StaplerRequest request) throws ServletException {
      this.request = request;
      //
      // // JENKINS-16043, POST can be multipart encoded if there's a file parameter in the job
      // String ct = request.getContentType();
      // if (ct != null && ct.startsWith("multipart/")) {
      // // as multipart content can only be read once, we can't read it here, otherwise it would
      // // break request.getSubmittedForm(). So, we get it using reflection by reading private
      // // field parsedFormData
      //
      // // ensure parsedFormData field is filled
      // request.getSubmittedForm();
      //
      // try {
      // java.lang.reflect.Field privateField = org.kohsuke.stapler.RequestImpl.class
      // .getDeclaredField("parsedFormData");
      // privateField.setAccessible(true);
      // this.parsedFormData = (Map<String, FileItem>) privateField.get(request);
      // } catch (NoSuchFieldException e) {
      // throw new IllegalArgumentException(e);
      // } catch (IllegalAccessException e) {
      // throw new IllegalArgumentException(e);
      // }
      //
      // this.isMultipartEncoded = true;
      // } else {
      // this.isMultipartEncoded = false;
      // }
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
      // if (this.isMultipartEncoded) {
      // // borrowed from org.kohsuke.staple.RequestImpl
      // FileItem item = this.parsedFormData.get(key);
      // if (item != null && item.isFormField()) {
      // if (item.getContentType() == null && this.request.getCharacterEncoding() != null) {
      // // JENKINS-11543: If client doesn't set charset per part, use request encoding
      // try {
      // return item.getString(this.request.getCharacterEncoding());
      // } catch (java.io.UnsupportedEncodingException uee) {
      // LOGGER.log(Level.WARNING, "Request has unsupported charset, using default for '" + key + "' parameter",
      // uee);
      // return item.getString();
      // }
      // } else {
      // return item.getString();
      // }
      // } else {
      // throw new IllegalArgumentException("Parameter not found: " + key);
      // }
      // } else {
      // return (String) ((Object[]) this.request.getParameterMap().get(key))[0];
      // }
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
      return Objects.equal("on", flag);
    }

    // /**
    // * returns true if request contains key
    // *
    // * @param key parameter name
    // * @return
    // */
    // private boolean containsKey(String key) throws javax.servlet.ServletException, java.io.IOException {
    // // JENKINS-16043, POST can be multipart encoded if there's a file parameter in the job
    // if (this.isMultipartEncoded) {
    // return this.parsedFormData.containsKey(key);
    // } else {
    // return this.request.getParameterMap().containsKey(key);
    // }
    // }
  }
}
