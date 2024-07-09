# Unleash Jenkins plugin

License Information:
====================
This software was first published under the Eclipse Publich License v1.0 (EPL-1.0). For a better integration with the Jenkins CI server ([https://jenkins.io/](https://jenkins.io/)) and substantial parts of the software that were taken from the [M2 Release Plugin](https://wiki.jenkins-ci.org/display/JENKINS/M2+Release+Plugin), the license has been switched to MIT license.

Copyright Information:
======================
Substantial parts of this plugin have been taken from the [M2 Release Plugin](https://wiki.jenkins-ci.org/display/JENKINS/M2+Release+Plugin). It served as a starting point for further development. Therefore many parts of the source code are still similar to the ones of the M2 Release Plugin. Appropriate copyright information is attached at the relevant classes.

Releasing The Plugin:
=====================
In order to release the plugin we need credentials for Github and the Jenkins-CI repository. The preferred way for releasing the plugin is to setup an unleash run on Jenkins or to perform the unleash-based release locally. In order to do this we need to clone the repository using HTTPS (in order to be able to pass Github credentials with the call), setup the Maven settings file like shown below and then call the unleash plugin and pass your Github credentials using the unleash system properties (either the env var or the direct properties):

`mvn unleash:perform -Dunleash.scmUsername=... -Dunleash.scmPassword=...`

OR

`mvn unleash:perform -Dunleash.scmUsernameEnvVar=... -Dunleash.scmPasswordEnvVar=...`

```XML
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 http://maven.apache.org/xsd/settings-1.0.0.xsd">
  <servers>
    <server>
      <id>maven.jenkins-ci.org</id> <!-- For parent 1.397 or newer; this ID is used for historical reasons and independent of the actual host name -->
      <username>...</username>
      <password>...</password>
    </server>
  </servers>
</settings>
```

Note that you can also operate on a local copy of the repository cloned using SSH. In this case you will have to provide your SSH private key as well as your passphrase using the respective unleash system properties. There is also some basic SSH agent support available.

[Official Jenkins CI release description](https://wiki.jenkins.io/display/JENKINS/Hosting+Plugins#HostingPlugins-Releasingtojenkins-ci.org)
