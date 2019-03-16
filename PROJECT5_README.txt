Hello!

Within this folder contains the following important files that you will need to know,

HEAD BACK TO THE MAIN DIRECTORY PAGE TO VIEW THE FULL README FILE!!!
MORE INFORMATION IS AVAILABLE AT THE README.MD FILE ON GITHUB!

------------
PROJECT1.WAR
------------
The project1.war file within the same directory (same directory as this file) contains the war file that allows the website to be tested through
the JMeter program. We have commented out the LoginFilter.java and modified the LoginServlet.java to be able to handle the JMeter tests from repeated
requests through the query_loads.csv that can be found in the course's website. We have disabled the Google Recaptcha to avoid the errors that would happen from
attempting to test the website through JMeter.

---------------------
LogFileProcessor.Java
---------------------
The LogFileProcessor java file is within the directories that can be found on the project 5 report. It essentially parses the information
gathered from the http requests in jmeter. After calculating the nanoseconds we then find the average of the computation time (TS and TJ).

-----------
JMeter-Logs
-----------
The file contains 9 files that each contain 2642 requests from the various test cases as given in Task 3.3 on the website. LogFileProcessor.java
uses these files to get the average times of TS and TJ.
