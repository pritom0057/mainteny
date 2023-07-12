# Getting Started
A job runner that can execute jobs

#### This is consist of 2 project
* Job Remote - Run remote service that will perform jobs and update the status in the DB
* Job Runner - A multithreaded application that triggers http request in the Job remote to start and get status of the jobs

### Job Remote is a spring boot back end application. Used MySQL as DB

To run the application
* install MySQL in the system
* import it as maven project
* create a db in my sql called "job_runner"
* change the db properties from application.properties
* build and run the application using following command

* Build the project using Maven:
    ```shell
    mvn clean install
* Run the application:
    ```shell
   mvn spring-boot:run
* The application will start running on http://localhost:8080.

### Job Runner is a Java application that will do following 
* Send a http request to trigger an action on a remote service , assume the action will take several minutes to finish
* send a http request to check the status of the job
* print the status of the job

To change the number of thread just modify the NUMBER_OF_JOB variable in the JobManager.java file.



