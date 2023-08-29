## Background
CyPlot (written in Java) is a Cytoscape App, and it provides the ability to plot data through GUI or CyCommands/REST. cyPlot uses plotly.js (https://github.com/plotly/plotly.js/) to achieve plot related functions. cyPlot also customizes plotly.js and PlotlyEditor to suit the needs of CyPlot.


## Setup
To get started with development,follow the steps in the Cytoscape App Ladder.

Once the setup is complete and the build for the sample app is successful,start by understanding how the OSGi framework is used in the development of the app.

To give a basic overview of Cyplot app codebase,we mainly need to understand a few things when we are making changes:
Pom.xml -this file is basically the fundamental unit of work in Maven. It is an XML file that contains information about the project and configuration details used by Maven to build the project. 
TaskFactory and Task -These are mainly the factories of the tasks that have to be performed,for example to add the Histogram plot,one needs to create 2 new classes in the tasks folder.One is a TaskFactory ,this class is part of a Cytoscape plugin or extension that provides functionality for creating the tasks  plots. When Cytoscape needs to create a plot, it can use an instance of this factory class to generate the appropriate Task (the other class) for the job.
Utils -this mainly contains contains the various classes with different tools used to plot the plots,the code for plotly.js is also generated here.
Cyactivator - This is basically the activator for all the taskfactories you have and is used to manage them through the CyApplicationManager





### Modifying plotly.js to add new features
This is a very simple task,all you need to understand is currently all the code related to plotly.js is in the JSUtils.java class under the Utils folder.The basic idea usd here is building the normal plotly.js as a string through a builder in java and finally supplying it to the html file while loading the page.
This just involves you putting the right plotly.js code from plotly.js documentation,as a string and pushing it into the builder in java using builder.append(“--plotly.js code–’’).Keep in mind that when supplying data from java classes ,they need to be supplied as strings as well.Going through the code will give you the clarity about how different elements are supposed to be appended in the builder.

Overall JSUtils.java has all the codes for different plots and they all follow the same basic template so you can just adjust one of them to achieve the functionality for other plots.

