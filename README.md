# CZ3004 Algorithm Simulator

This project aims to simulate the robot algorithm 

## Requirements 

This project uses [Gradle](https://gradle.org/install/) to link and build the Java Application 
(Tested with gradle 6).   
The project is compatible with [Java JDK or JRE](https://www.oracle.com/java/technologies/javase-downloads.html) version 8 or higher. 
The project uses [Kotlin](https://kotlinlang.org/) version [1.4.0](https://github.com/JetBrains/kotlin/releases/tag/v1.4.0)

## Folder structure 
Source codes will be located under `/src/main`. 
It will be separated by two main directories, `java` and `kotlin`, 
which corresponds to the languages the classes are written. 

This will be further divided with several packages: 
*   `../data`  
    This directory consists of a data classes used in the project. 
*   `../utils`  
    This directory consists of classes utilities which usage is shared among multiple classes in the project.
*   `.../constants`
    THis directory consists of constants to be used in the project.

`data` and `utils` are further divided into several packages based on its functionality, namely: 
*   `map`
    Map-related classes
*   `roobt`
    Robot-related classes 

More details will be updated. 

## Building and Running the App 

The project is set up to include a wrapper script, therefore one can just run `./gradlew <command>`, 
where command can be any commands listed on `./gradlew tasks`

Note: Windows user may run `gradlew <command>` or `gradlew.bat <command>` instead

1.  The following command can be used to build the project

    ```bash
    ./gradlew build
    ```
    

2.  To run the test cases, run: 
    
    ```bash 
    ./gradlew test
    ```

Tutorial on running gradle app: https://guides.gradle.org/building-java-libraries/

To run the app for development, for now just create a class with `main()` function in it, 
then run the class using the built-in IntelliJ IDEA Runner. 

More details will be updated later. 

## Current assumptions for coding: 
Hardly reconfigurable: 
*   Robot fills 3x3 cells, with the center is used as a reference
*   Arena map uses 0-based indexing
*   `row` is defined as the long side of the maze (20 blocks)
*   `col` is defined as the short side of the maze (15 blocks)
*   Virtual walls are 1 block in size
*   There are 4 directions, namely North, South, East and West with operations as follows: 
    -   North: row+1
    -   South: row-1
    -   East: col+1
    -   West: col-1

Easily reconfigurable: 
*   Arena size is 15 by 20
*   Robot starts from North direction

More details will be updated asap. 

## Additional notes 

It is recommended to use [IntelliJ IDEA](https://www.jetbrains.com/idea/) as the IDE to 
automatically detect and handle the compilation, linking, and execution of the project. 
