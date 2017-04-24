package com.itemis.jenkins.plugins.unleash.util;

import java.io.IOException;
import java.io.InputStream;

import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;

import com.google.common.base.Optional;

import hudson.maven.MavenModule;
import hudson.maven.MavenModuleSet;

public final class MavenUtil {
  private MavenUtil() {
    // utility class
  }

  public static Optional<Model> parseModel(MavenModule module, MavenModuleSet mavenModuleSet) {
    if (module == null || mavenModuleSet == null || mavenModuleSet.getRootModule() == null) {
      return Optional.absent();
    }

    String pathToPom;
    if (mavenModuleSet.getRootModule().equals(module)) {
      pathToPom = mavenModuleSet.getRootPOM(null);
    } else {
      pathToPom = module.getRelativePath() + "/pom.xml";
    }

    Model model = null;
    InputStream modelIS = null;
    try {
      modelIS = mavenModuleSet.getWorkspace().child(pathToPom).read();
      model = new MavenXpp3Reader().read(modelIS);
    } catch (Throwable t) {
      // intentionally blank
    } finally {
      if (modelIS != null) {
        try {
          modelIS.close();
        } catch (IOException e) {
          // intentionally blank
        }
      }
    }
    return Optional.fromNullable(model);
  }

  public static Optional<String> parseVersion(Model model) {
    String version = model.getVersion();
    if (version == null) {
      Parent parent = model.getParent();
      if (parent != null) {
        version = parent.getVersion();
      }
    }
    return Optional.fromNullable(version);
  }
}
