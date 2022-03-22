#!/usr/bin/env groovy

def call(String repoUrl) {
  pipeline {
       agent any

       tools {
           maven 'Maven 3.6.0'
           jdk 'jdk8'
       }

       environment {
           AWS_ACCESS_KEY_ID = credentials('access_key_id')
           AWS_SECRET_ACCESS_KEY = credentials('secret_key_id')
       }

       stages {
           stage("Tools initialization") {
               steps {
                   sh "mvn --version"
                   sh "java -version"
               }
           }
           stage("Checkout Code") {
               steps {
                   git branch: 'master',
                       url: "${repoUrl}"
               }
           }
           stage("Cleaning workspace") {
               steps {
                   sh "mvn clean"
               }
           }
           stage("Running Testcase") {
              steps {
                   sh "mvn test"
               }
           }
           stage("Packing Application") {
               steps {
                   sh "mvn clean package -Daccess_key=${env.AWS_ACCESS_KEY_ID} -Dsecret_key=${env.AWS_SECRET_ACCESS_KEY} -DskipTests"
               }
           }
       }
   }
}