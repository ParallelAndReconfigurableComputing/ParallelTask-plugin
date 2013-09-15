This folder contains all ParaTask Eclipse plugin source files.

If you chock out this folder, it is assumed that you are about to edit the source code.

Before you do that, please read this documents carefully on how to use the source code.

ParaTask Eclipse plugin is an Eclipse Plugin, you should have at least Java, Eclipse, Eclipse SDK and Eclipse Plugin Development Environment (PDE) installed.

ParaTask Eclipse plugin is highly dependent on ParaTask Project, you should always check out and compile the latest version of PTRuntime.jar and PTCompiler.jar

It is recommanded that you import the project into the Eclipse Platform through "Import Existing Projects into Workspace".

In general, Eclipse will automatically compile all the files for you, however you can conpile through the Ant scripts we provided just right under the folder.

build.xml AND autogen.xml

Important : Please DO NOT touch autogen.xml, this is used to package the project to a plugin zip file. And it is used by build.xml internally.

            You must modify the build.xml before you conpile and package the project. 

	    You must desigante the Eclipse Home, which you can set through "<property name="ECLIPSE_HOME" value="set Eclipse Home here"/>".

	    You are allowed to modify other properties if necessary.


The generated ptplugin.zip will be right under the folder. Place it to the Eclipse Plugin folder and restart the Eclipse. Then you can use ParaTask Eclipse Plugin.

