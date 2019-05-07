<!-- ********************************************************************
     Copyright 2018 Ellucian Company L.P. and its affiliates.
******************************************************************** -->

#Banner General UI SS plugin documentation

####Status
Production quality, although subsequent changes may not be backward compatible.  Remember to include this software in export compliance reviews when shipping a solution that uses this plugin.

####Overview
This provides functionality for SS application that are not ui components related. For ui-ss components refer to banner_ui_ss plugin.

Key features provided by the plugin include:

* TBD
* TBD

#Installation and quickstart
The recommended approach is to install the plugin as a git submodule.

###Add Git submodule
To add the plugin as a Git submodule under a 'plugins' directory:

        $ git submodule add ssh://git@git.ellucian.com:7999/banxeplug/banner_general_common_ui_ss.git plugins/banner_general_common_ui_ss.git
        Cloning into 'plugins/banner_general_common_ui_ss.git'...
        remote: Counting objects: 1585, done.
        remote: Compressing objects: 100% (925/925), done.
        remote: Total 1585 (delta 545), reused 309 (delta 72)
        Receiving objects: 100% (1585/1585), 294.45 KiB | 215 KiB/s, done.
        Resolving deltas: 100% (545/545), done.

Then add the in-place plugin definition to build.gradle:

        compile project (":banner-codenarc")
        
Then add the plugin path in settings.gradle
        include ':banner-codenarc'
        project(':banner-codenarc').projectDir = new File('../banner_codenarc.git')
        

Note that adding the plugin this way will the latest commit on the master branch at the time you ran the submodule command.  If you want to use an official release instead, go to the plugin directory and checkout a specific version, e.g.:

    cd plugins/banner_general_common_ui_ss.git
    git checkout pub-9.29

Don't forget to go back to your project root and commit the change, as this will establish your project's git submodule dependency to the desired commit of the plugin.


###2. Configure plugin dependencies
The plugin depends on ... TBD..
       
