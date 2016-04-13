This is the ParaTask Eclipse plug-in project. Please check out nz.ac.auckland.ptjavafeature and
nz.ac.auckland.ptjavaupdatesite together into the same workspace.

In order to build and make modifications to this project, please install Eclipse SDK with PDE 
(plug-in development environment).

As expected, the ParaTask plug-in depends on the ParaTask libraries, including PTCompiler.jar and
PTRuntime.jar. Please make sure that these two jar files are the right version to work with.

To build and package this project as an Eclipse plug-in jar file, you could use Eclipse -> File ->
Export -> Plug-in Development -> Deployable plug-ins and fragments. 

However, the most convenient way is to use updatesite projects to generate the plugin with its
corresponding feature jar file: 

In oder to do so, create a new "updatesite" project. File>New>other>Plug-in Development>Update Site Project,
alternatively you can use an already created updatesite project. 
In the next step, click on "site.xml", and use the "Site Map" tag to alter the attributes of the project.

Category, defines a classification under which your plugin appears, when a user wants to install it.
You can either create a new category, or use one that is already created. 

Each category should contain one or more "feature". Features, are the actual plugins that appear for install
when a user clicks on the category, in Eclipse software installation section. In order to import a feature
for a category, select the feature, and then press the "Add Feature" button. Eclipse opens a "feature selection"
dialog box, from which you can select a feature project. 

Before progressing further, make sure that the attributes specified in the "META-INF/MINIFEST.MF" file of the
plugin project, and the "feature.xml" file of the feature project are set up correctly; otherwise, the product
version no. will be incorrect. 

Then, type in the name of the feature project corresponding to the desired plugin project. The Eclipse dialog
box helps with finding matching projects. Then select the project. Double check "site.xml" tag to ensure that 
attributes have been set up correctly. Go back to the "Site Map" tage, and press the "Build All" button. 

The siteupdate project then creates:

        1- a "features" folder that contains the jar file for the corresponding feature project.
        2- a "plugins" folder that contains the jar file for the plugin to which the feature project refers.
        3- and "artifacts.jar" and "content.jar" files which hold information that is used by Eclipse software
           installation application when retrieving data about your plugin and its categories and features.
           
The advantage of using "Build All" is that the updatesite project automatically generates that, and guarantess that
the generated products are in sync with each other. Upload the content of the updatesite project (the items mentioned
above) to the plugin server. For Parallel task, the plugin server is: 

					https://svn.ece.auckland.ac.nz/svn/taschto/html-parallel/plugins/ParallelTaskEclipsePlugin   
					
*********************************************** 13-04-2016 BY MOSTAFA MEHRABI ***********************************************
