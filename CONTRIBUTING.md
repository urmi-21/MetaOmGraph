# Contributing

Before contributing to this project please discuss the suggested changes via issues or email.
To contribute to MetaOmGraph, please fork this repository, make changes and then send a pull request.
Please note that this project is released with a Contributor [Code of Conduct](https://github.com/urmi-21/MetaOmGraph/blob/master/CODE_OF_CONDUCT.md). By participating in this project you agree to abide by its terms.


# Getting Started
Here, information is provided for contributers who would like to contribute to MetaOmGraph.


## Cloning MetaOmGraph
To download the code please clone MetaOmGraph's repository using the command:

```
git clone https://github.com/urmi-21/MetaOmGraph.git
```
## Accessing the code
Eclipse IDE is suggested to open the MetaOmGraph project.

## Docker run
You can use docker (if installed) to quickly test your work or run the application without needing to memorize a bunch of commands.
```bash
docker build . -t mog
docker run mog
```

## Dependencies
* Install Java version 8 from [here](https://www.oracle.com/technetwork/java/javase/downloads/index.html). After installing add JDK 8 to MOG project in eclipse.
See this [link](https://stackoverflow.com/questions/13635563/setting-jdk-in-eclipse) for help.
* All dependencies are handeled through Maven (see [pom.xml](https://github.com/urmi-21/MetaOmGraph/blob/master/pom.xml))
* Download `l2fprod-common-all.jar` from [here](http://www.java2s.com/Code/Jar/l/Downloadl2fprodcommonalljar.htm)
* Download 'hierarchical-clustering-1.2.0.jar' from [here](https://github.com/lbehnke/hierarchical-clustering-java/releases/tag/v1.2.0)
* Add [src/lib/CustomBrowserLauncher.jar](https://github.com/urmi-21/MetaOmGraph/tree/master/src/lib) and `l2fprod-common-all.jar` locally to maven by running:
```
mvn install:install-file -Dfile='src/lib/l2fprod-common-all.jar' -DgroupId='com.l2fprod' -DartifactId='l2fprod-common-all' -Dversion='0.1' -Dpackaging=jar -DgeneratePom=true

mvn install:install-file -Dfile='src/lib/CustomBrowserLauncher.jar' -DgroupId='edu.iastate.metnet' -DartifactId='custombrowserlauncher' -Dversion='0.0.1' -Dpackaging=jar -DgeneratePom=true

<<<<<<< HEAD
mvn install:install-file -Dfile='CustomBrowserLauncher.jar' -DgroupId='edu.iastate.metnet' -DartifactId='custombrowserlauncher' -Dversion='0.0.1' -Dpackaging=jar -DgeneratePom=true

mvn install:install-file -Dfile='hierarchical-clustering-1.2.0.jar' -DgroupId='com.apporiented' -DartifactId='hierarchical-clustering' -Dversion='1.2.0' -Dpackaging=jar -DgeneratePom=true
=======
mvn install:install-file -Dfile='src/lib/hierarchical-clustering-1.2.0.jar' -DgroupId='com.apporiented' -DartifactId='hierarchical-clustering' -Dversion='1.2.0' -Dpackaging=jar -DgeneratePom=true
>>>>>>> xmlremoved
```
#### NOTE: If using Windows CMD remove the `'`(single quotes) from the above `mvn install` commands
 



## Running
The main class is _MetaOmGraph_. Run the _MetaOmGraph.java_ project to start MetaOmGraph.

## Exporting runnable JAR
From Eclipse, run `maven build`. The `.jar` file will be compiled and stored in the `MetaOmGraph/target` directory.

## Getting help
Please contact the developers through [email](http://metnetweb.gdcb.iastate.edu/MetNet_MetaOmGraph_download.php) if any help is required regarding setting up MetaOmGraph.
