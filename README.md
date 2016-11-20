# SQAAS Toolkit

A work in progress to improve the life of a SQAAS member

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development.

### Prerequisites

Windows is the primary platform but Mac is also supported as well.

You will need [Maven](https://maven.apache.org/install.html) and [Java JDK 1.8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html).

You will also need [Rally API key](https://rally1.rallydev.com/login). 

### Installing & Running

After installing Maven and Java and getting access to your Rally API Key, you can begin installing.

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

### How it works

Enter your API key, login, and click EOD or SSU (story status update)
