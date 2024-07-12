# Unleash Plugin

This is a Jenkins Build Wrapper for Maven Jobs. It enables you to
perform Maven releases using the
[unleash-maven-plugin](https://github.com/mavenplugins/unleash-maven-plugin).

# How To Use The Plugin?

This section describes the project configuration options as well as the
actual usage of the plugin when building Maven releases.

### Project Configuration

In order to use the plugin for building Maven releases you will have to
enable it first in the configuration of your Maven build job. Go to the
**Build Environment** section and enable the build wrapper by checking
the **Unleash** box.  
Then you will see some input fields for basic settings. There you can
add the required profiles and options for your release build. The goals
are pre-defined because currently unleash:perform is the only goals that
makes sense there.  
![](docs/images/2016-08-25_14h50_44.png)

Clicking the Advanced... button shows some additional settings for
workflow overriding, SCM-related settings and some checkboxes that
affect the actual release form that we use later.  
![](docs/images/2016-08-25_14h51_17.png)

The Hook Data section can then be used for really advanced stuff. If you
f.i. extend the default workflow and add an exec-hook to the workflow,
you will be able to provide data for the hook execution at this point.  
![](docs/images/2016-08-25_14h52_08.png)

### Use The Plugin

The usage of the plugin is pretty simple and is nothing more than
submitting a form on a subpage of the project.  
After you've enabled the plugin a link will be shown on the left side of
the screen called **Trigger Unleash Maven Plugin**:  
![](docs/images/Screenshot.png)

After clicking on this link you will be directed to the form page where
you can start the release build for your project.  
![](docs/images/2016-08-25_14h53_56.png)

On this page you can either specify a global version for all modules of
the project (for release and dev) or you stick to the versions provided
by the plugin and listed in the table which allows idependent versioning
of modules.  
You can also provide custom SCM credentials and reqeuest some other
stuff.  
![](docs/images/2016-08-25_14h54_06.png)

### Release Info

Once you have some successful or failed release builds you can also get
some project-related release information, such as the badge icons
indicating successful or failed releases.  
![](docs/images/2016-07-27_14h05_10.png)

There are also two permalinks available, one for successful and one for
failed releases.  
![](docs/images/Screenshot-1.png)

Finally there are some ListView columns:  
![](docs/images/Screenshot-5.png)

# Support

Issues around this plugin are tracked in the Jenkins Issue Tracker. To
browse open issues please visit [this
page](https://issues.jenkins-ci.org/secure/IssueNavigator.jspa?mode=hide&reset=true&jqlQuery=project+%3D+JENKINS+AND+status+in+(Open,+%22In+Progress%22,+Reopened)+AND+component+%3D+unleash-plugin).
There you can also file new ones if necessary.

General information about the usage of the Maven plugin can be found
here:
[unleash-maven-plugin](https://github.com/mavenplugins/unleash-maven-plugin).

# Copyright Information

Substantial parts of the plugin sources have been extracted from the M2
Release Plugin. It served as a base for getting started since the
feature set was very similar.  
Please find further copyright notes in the source code.

# Version History

See [Releases](https://github.com/jenkinsci/unleash-plugin/releases)