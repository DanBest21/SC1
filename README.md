## Description:
Allows the user to input an email ID, which is then used to attempt to find the name associated with that ID on the ECS website, or the intranet should that fail.

## Setup/Configuration
This application is designed to be ran in a console:
  - Copy each file to a single directory and unzip the lib.zip file.
  - Run "javac -cp .;lib\* LoginDetails.java" and "javac -cp .;lib\* ECS_Profile.java" in the console.
  - Run "java -cp .;lib\* ECS_Profile" to begin the application.
    
## Dependencies
The HtmlUnit library is used in this application, which is what is contained within the lib folder.
