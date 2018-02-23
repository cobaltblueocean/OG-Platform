﻿Setting up Eclipse and using Ivy to resolve dependencies
========================================================

You may run a command-line build (see README.txt) before setting up an Eclipse workspace, although
this isn't required. If you choose not to run a command line build, be aware it can take a
significant amount of time the first time you start after you finish the procedure below, when
Eclipse resolves the Ivy artifacts.  This will of course depend on your internet connection.  Once
you've done it for the first time, your Ivy cache should make building much faster.

Procedure
---------

This procedure, including a screencast, is also available at:

  http://docs.opengamma.com/display/DOC/Setting+up+an+Eclipse+Workspace

It's very important you follow the steps below exactly in the order specified, or the result will
be incorrect.

  1. Make sure you install, as a minimum: Eclipse itself, the IvyDE plugin and the Checkstyle
     plugin.

     a. Install Eclipse (www.eclipse.org). We recommend the 'Classic' bundle. Then, create a fresh
        workspace created in a location separate from your source tree. The .project files we use
        work with the Indigo or later stable release of Eclipse.

     b. Depending on your version of Eclipse, either:

        * Import the required plug-ins automatically

            i. Go to "File->Import"
           ii. Under "Install" choose "Install Software Items from File"
          iii. Click the "Browse..." button
           iv. Navigate the file chooser to the "OG-Platform/eclipse" folder and choose "plugins.p2f"
            v. Click "Next", click the "Accept the license terms" and click "Finish". You may see
             warnings about unsigned content and Eclipse may offer to restart.
      
        * Or install the plug-ins manually

            i. IvyDE (required)                       http://ant.apache.org/ivy/ivyde/download.cgi
           ii. Eclipse-CS (optional, but recommended) http://eclipse-cs.sourceforge.net/downloads.html
          iii. TestNG (optional, but recommended)     http://testng.org/doc/download.html

  2. Import the default preferences
     a. Go to "File->Import"
     b. Under "General" choose "Preferences"
     c. Click "Next"
     d. Click the "Browse..." button
     e. Navigate the file chooser to the "OG-Platform/eclipse" folder and choose
        "OpenGamma-Eclipse-Preferences.epf"
     f. Click "Finish"

  3. Turn of auto-builds until we've finished setting up
     a. Go to "Projects->Build Automatically" to uncheck the "Build Automatically" menu entry

  4. Import the sub-projects
     a. Go to "File->Import"
     b. Under "General" choose "Existing Projects into Workspace"
     c. Click the "Browse..." button
     d. Navigate the file chooser to the "OG-Platform/projects" folder and clock "Open"

       IMPORTANT: Pay special attention to the fact that it's the projects/ subfolder, not the root

     e. A large list of projects should appear
     f. Click "Finish"

  5. Import the top-level project
     a. Go to "File->Import"
     b. Under "General" choose "Existing Projects into Workspace"
     c. Click the "Browse..." button
     d. Navigate the file chooser to the "OG-Platform" folder and click "Open"

       IMPORTANT: This this it is the root folder

     e. "OG-Platform" should appear as a project on its own
     f. Click "Finish"

  6. Turn on auto-builds
     a. Go to "Project->Build Automatically" to check the "Build Automatically" menu entry

  7. Quit Eclipse and restart (do not just use "File->Restart")

     This may take several minutes of building but eventually will show all projects error free.
     If you have not run a command line build it may take longer while artifacts are downloaded.

     If there is an error shown in the OG-Engine project:

        * Find the source files (PutRequest.java and GetRequest.java are the culprits) in the
          package explorer

        * Right-click on each one in turn in the package explorer and choose
          "Checkstyle->Clear Checkstyle violations". This is necessary because of a bug in the
          Checkstyle parser.

  8. Get exploring!

Recommended Extras for Contributors
-----------------------------------

  1. Load the code templates

     a. Go to Global Preferences (on the Mac it's 'Eclipse->Preferences..' and on Windows/Linux
        it's under 'Window->Preferences...')

     b. Under 'Java->Code Style->Code Templates' click on 'Import...'

     c. Browse to OG-Platform/eclipse/ and choose 'OpenGamma-Java-Eclipse-CodeTemplates.xml'

     d. Click 'Open'

  2. Load the code formatter

     a. Go to Global Preferences (on the Mac it's 'Eclipse->Preferences..' and on Windows/Linux
        it's under 'Window->Preferences...')

     b. Under 'Java->Code Style->Formatter' click on 'Import...'

  3. Browse to OG-Platform/eclipse/ and choose 'OpenGamma-Java-Eclipse-Formatter.xml'

     a. Click 'Open'

  4. Install more Eclipse plugins

     a. Install Spring IDE [http://springide.org/updatesite] (Core/Spring IDE), although make sure
        you disable spring file validation if you do as it's rather slow.

Tips if you have problems
-------------------------

  * Make sure you have the latest Eclipse (Indigo at the time of writing). The settings files
    don't work with Galileo properly: specifically, if you get an error about 'path fragments',
    that is because you're trying to use Galileo.

  * Eclipse doesn't always refresh things when you think it will.  Try highlighting all projects,
    right-clicking and choose 'Refresh' on the menu

  * Sometimes IvyDE needs a kick to resolve/refresh artifacts, there's a button on the task bar
    for refreshing

  * Running command line unit tests can occasionally conflict with a running copy of Eclipse, in
    which case a workaround is to shut down Eclipse while you run tests

  * If you have the Spring plug-in installed, turn off spring file validation

  * If you do a fresh pull from the git repository, you'll need to refresh all the projects