Jenkins GTMetrix Plugin
=================================

Description
-----------

Jenkins plugin to create and display a report from GTMetrix.


How To Begin
============

1) Create an account at http://gtmetrix.com

2) Go to http://gtmetrix.com/api/ and generate an API key for your account

3) Compile and run the plugin (see Compiling)

4) Add your email and API key to your Jenkins global configuration (Jenkins > Manage Jenkins > Configure System)

4) Create a project or modify a new project

5) Configure your project to have the Build step "Perform GT Metrix" and set the URL for the site you want to test

6) Build!


Compiling
=========

To compile you'll need maven. Run the following commands from the root directory of the plugin

Compile plugin:

```mvn package```

Run development Jenkins server with plugin.

```mvn hpi:run```


Future Plans / TODOs
====================

- Optimize
- Implement Jenkins/Hudson best practices
- Allow user to select test locations and browsers
- Allow users to run multiple URLs per build and multiple locations/browsers.