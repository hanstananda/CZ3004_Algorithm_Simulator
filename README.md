# CZ3004 Algorithm Simulator

This project aims to provide a simulation platform to simulate the actual robot algorithm 

## Requirements 

This project uses [Gradle](https://gradle.org/install/) to link and build the Java Application 
(Tested with gradle 6).   
The project should be compatible with [Java JDK or JRE](https://www.oracle.com/java/technologies/javase-downloads.html) version 8 or higher. 
The authors of this project used Java 11 to develop this project.  
The project uses [Kotlin](https://kotlinlang.org/) version [1.4.0](https://github.com/JetBrains/kotlin/releases/tag/v1.4.0)

## Folder structure 
Below are the details of the folder structure: 
*   Source codes are located under `/src/main`. 
*   Unit test codes are located under `/src/test`. 
*   Integration test codes are located under `src/intTest`.

It will be further separated by two main directories, `java` and `kotlin`, 
which corresponds to the languages the classes are written. 

This will be further divided with several packages: 
*   `data`  
    This directory consists of a data classes used in the project. 
*   `utils`  
    This directory consists of classes utilities which usage is shared among multiple classes in the project.
*   `constants`  
    This directory consists of constants to be used in the project.
*   `simulator`  
    This directory consists of Simulator UI as well as the Simulator Server used in the project.
*   `examples`  
    This directory consists of Kotlin example files used to debug the project

`data` and `utils` are further divided into several packages based on its functionality, namely: 
*   `comm`
    Communication-related classes
*   `map`
    Map-related classes
*   `roobt`
    Robot-related classes 

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
    
3.  To run the Simulation Server, run the entire project using 
    ```bash
    ./gradlew run
    ```
    
The server will be accessible from `ws://0.0.0.0:8080/robot`. 
The list of the available commands is available on the [Readme of RPi server communication and the and PC](https://github.com/weicong96/mdp/tree/master/rpi/pc).

As can be seen from the url, websocket connection is needed to access the server. 
For debugging purposes, you can use a [firefox extension](https://addons.mozilla.org/en-US/firefox/addon/simple-websocket-client/) 
or [chrome extension](https://chrome.google.com/webstore/detail/simple-websocket-client/pfdhoblngboilpfeibdedpjgfnlcodoo) 
([alternative chrome extension](https://chrome.google.com/webstore/detail/smart-websocket-client/omalebghpgejjiaoknljcfmglgbpocdp))

A more comprehensive tutorial on running gradle app(s) can be accessed from [here](https://guides.gradle.org/building-java-libraries/)

## Main External Libraries used in this project
*   [Ktor](https://ktor.io/)  
    Used as an asynchronous framework to build the websocket server. This includes the [netty] network application framework installed as well. 
*   [Gson](https://github.com/google/gson)  
    Used to serialize & deserialize payload to be communicated with the backend algo server 
*   [kotlin-logging](https://github.com/MicroUtils/kotlin-logging)  
    Used as a logging framework for Kotlin 
*   [Logback](http://logback.qos.ch/)  
    Used as an alternative for [SLF4J](http://www.slf4j.org/) which serves as a Logging Facade for Java applications. 
*   [Junit5](https://junit.org/junit5/)  
    Used to aid unit testing of the server 


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
*   The sensor will have a certain effective range. 
    * If it detects anything below the effective range, it will return 0.
    * If it does not detect anything on the effective range, it will return -1. 
    * Otherwise, it will return an integer which corresponds to the distance(in number blocks) of the nearest obstacle/wall in front of the sensor the is currently located.

Easily reconfigurable: 
*   Arena size is 15 by 20
*   Robot starts from North direction
*   Robot starting position is (1,1)
*   Sensors are organized based on the current robot configurations as follows: 
    ```
              ^   ^   ^
             IRS IRS IRS
        <IRL [X] [X] [X] IRS >
             [X] [X] [X]
             [X] [X] [X] IRS >
    ```
    Following are the definitions of the sensors:
    * IRS = Infrared Short Range Sensor
    * IRL = Infrared Long Range Sensor
    * IRS_FL = front-facing IRS positioned on the left
    * IRS_FM = front-facing IRS positioned on the middle
    * IRS_FR = front-facing IRS positioned on the right
    * IRS_RF = right-facing IRS positioned on the front
    * IRS_RB = right-facing IRS positioned on the back
    * IRL_LF = left-facing IRL positioned on the front
    Following are the available ranges of the robot sensors: 
    * IRS: 1-3 blocks
    * IRL: 1-5 blocks
    

## Additional notes 

It is recommended to use [IntelliJ IDEA](https://www.jetbrains.com/idea/) as the IDE to 
automatically detect and handle the compilation, linking, and execution of the project. 
