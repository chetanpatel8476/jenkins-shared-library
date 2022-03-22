#!/usr/bin/env groovy

def call(String repoUrl) {
  pipeline {
       agent any

       tools {
           maven 'Maven 3.6.3'
           jdk 'jdk8'
       }

       environment {
           AWS_ACCESS_KEY_ID = credentials('access_key_id')
           AWS_SECRET_ACCESS_KEY = credentials('secret_key_id')
           dockerImage = ''
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
                   script {
                       def dockerfile = 'Dockerfile'
                       dockerImage = docker.build("mydevopslab.jfrog.io/default-docker-local/student-app:$BUILD_NUMBER", "-f ${dockerfile} .")
                       //withDockerRegistry(credentialsId: 'Docker_Creds', url: 'https://index.docker.io/v1/') {
                         //  def dockerImage = docker.build("chetanpatel/student-application:$BUILD_NUMBER",'.').push()
                       //}
                   }
               }
           }
           stage('Push the docker image to Artifactory'){
               steps{
                   rtDockerPush(
                       serverId: "artifactory-mydevopslab",
                       image: "$dockerImage",
                       host: 'tcp://localhost:2375',
                       targetRepo: 'default-docker-local'
                   )
               }
           }
       }
   }
}