===============
Getting Started
===============

The example-app illustrates the use of OpenGamma as a server and a library

OpenGamma as a server
=====================

Set up
------

Run ``mvn package -DskipTests`` to unpack the web resources

Running the fullstack server from you IDE
-----------------------------------------

Run ``OpenGammaServer`` from example server in your IDE. Ensure that the working directory is set to the module directory,
for example ``$MODULE_DIR$`` in Intellij. This makes sure that the web resources are on the classpath.

This will start the OpenGamma Component Server using the configuration the config/fullstack folder.
The following can be added to your VM options ``-Dlogback.configurationFile=com/opengamma/util/warn-logback.xml`` to manage the logging level.

The example resources will be loaded and persisted in memory while the server is running.
The loaded data will be visible at http://your-server-ip:8080/jax/

You will now be able to run the ``ExampleRemoteClientTool`` or the remote integration tests.

OpenGamma as a library
======================

Run ``CreditPricingTool`` or ``CurveBundleProviderTool`` from the example-library-app in your IDE.

The in-memory components needed to run the calculation are created and populated.
The credit example loads the credit-import-data and outputs the resulting PV and CS01 to the console.
The curve bundle example loads the curve-import-data and outputs the resulting bundle to the console.
