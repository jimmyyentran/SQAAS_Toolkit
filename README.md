# SQAAS Toolkit

A work in progress to improve the life of a SQAAS member

## Prerequisites

You will need [Rally API key](https://rally1.rallydev.com/login). 

## Running

If you are on Windows just double click the .exe file

## For Developers

Windows is the primary platform but Mac is also supported.

You will need [Maven](https://maven.apache.org/install.html) and [Java JDK 1.8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html).

### Compile source

```
cd SQAAS_Toolkit
```

Install project from scratch

```
mvn clean install
```

Run the project

```
mvn exec:java
```

### Compile to executable jar

```
mvn clean compile assembly:single
```

Run the jar

```
java -jar target/sqaas-toolit-vx.x.x-jar-with-dependencies.jar
```
