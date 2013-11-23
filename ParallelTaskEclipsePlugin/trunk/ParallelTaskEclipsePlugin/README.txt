This is the ParaTask Eclipse plug-in project. Please check out nz.ac.auckland.ptjavafeature and
nz.ac.auckland.ptjavaupdatesite together into the same workspace.

In order to build and make modifications to this project, please install Eclipse SDK with PDE 
(plug-in development environment).

As expected, the ParaTask plug-in depends on the ParaTask libraries, including PTCompiler.jar and
PTRuntime.jar. Please make sure that these two jar files are the right version to work with.

To build and package this project as an Eclipse plug-in jar file, you could use Eclipse -> File ->
Export -> Plug-in Development -> Deployable plug-ins and fragments. But the most convenient way is
to use the updatesite project to generate it with the feature jar file: open the site.xml with the
Site Manifest Editor (its default editor), update feature versions and click 'Build All'.