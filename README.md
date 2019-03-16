# cs122b-winter19-team-31

Welcome To the CS122B-Winter19-Team-31 Github Repo!


Hello!

Within this folder contains the following important files that you will need to know,


This project repository will show the various branches that were created in order to build from the very early projects starting from project1 all the way to project5. The application we built in this course is an ImDb-esque website where users must login, search for movies, click on movies, view a movie's list of stars, and even search for stars as well. It is an implementation of features that any concurrent website would have, and we even have our own checkout functionality! It works! Below is a list of thedeliverables that can be seen the root directory.

----------------
Project 5 Report
----------------
This PDF contains the necessary information for the overall project containing the information of where to find the necessary directories to inspect the various parts of the project. It also contains snapshots of our code to showcase the various places where we used the required code to complete functions such as connection pooling and using PreparedStatements.

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

-------------
JMeter-Report
-------------
This folder contains the necessary images and information for the 9 different test cases for JMeter. It also contains all the graph images from each test to showcase the speed and execution throughput of each varying test.
