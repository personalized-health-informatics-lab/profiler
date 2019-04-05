# Profiler

Profiler is a powerful tool that searches Pubmed based on your list of names and codes. It produces a spreadsheet that features lots of useful information about the authors, including how many publications per year, which journals they are featured in, etc. Once the search is complete, you can download the spreadsheet with its analysis!. 

## Prerequisites
```
Maven
```

## Installation

The installation of Apache Maven is a simple process of extracting the archive and adding the bin folder with the mvn command to the PATH.
Detailed steps are
Ensure JAVA_HOME environment variable is set and points to your JDK installation
Extract distribution archive in any directory

```
unzip apache-maven-3.6.0-bin.zip
```
Add the bin directory of the created directory apache-maven-3.6.0 to the PATH environment variable
Confirm with mvn -v in a new shell. The result should look similar to

Go to where you put the Profiler, open CMD and run
```
mvn install
```
Go to target file and type
```
java -jar crawler.jar
```
Then open your browser access to httplocalhost3000

## Instruction 

1.	Profiler accepts only Excel Spreadsheets, formatted with headers
2.	Column 'A' should have a header like ‘Researcher Name’; names in this column should follow the format ‘Last Name, First Name’
3.	Column 'B' should have a header like 'Program Name'; you can choose to put a two- or three-digit code here, and the authors will be sorted according to these codes
4.	When ready to upload your spreadsheet, click ‘Browse’, then select your file
5.	To begin the search, click 'Upload and Search'
6. When the search is complete, click 'Get Results!' to download your results.

