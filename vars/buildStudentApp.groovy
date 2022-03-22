#!/usr/bin/env groovy

def call(String repoUrl, String applicationName) {
  pipeline {
       agent any

       tools {
           maven 'Maven 3.6.3'
           jdk 'jdk8'
       }

       environment {
           DOCKER_REGISTRY = 'mydevopslab.jfrog.io'
           DOCKER_REPO = 'devops-docker-release-local'
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
                   sh "mvn clean package -Daccess_key=$AWS_ACCESS_KEY_ID -Dsecret_key=$AWS_SECRET_ACCESS_KEY -DskipTests"
               }
           }
           stage('Build & Push the Docker Image'){
               steps{
                   dockerBuild("${DOCKER_REGISTRY}/$DOCKER_REPO/$applicationName", "$BUILD_NUMBER")
                   //script {
                     //  dockerBuild("${DOCKER_REGISTRY}/$DOCKER_REPO/$applicationName", "$BUILD_NUMBER")
                   //}
               }
           }
           stage('Push the docker image to Artifactory'){
               steps{
                   dockerPush("$applicationName", "$BUILD_NUMBER", "$DOCKER_REPO")
                   //script {
                     //  dockerPush("$applicationName", "$BUILD_NUMBER", "$DOCKER_REPO")
                   //}
               }
           }
       }
   }
}