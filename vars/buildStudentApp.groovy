#!/usr/bin/env groovy

def call(Map pipelineParams) {
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
                       url: pipelineParams.GitRepoURL
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
                   sh "mvn package -Daccess_key=$AWS_ACCESS_KEY_ID -Dsecret_key=$AWS_SECRET_ACCESS_KEY -DskipTests"
               }
           }
           stage('SonarQube Analysis') {
               steps {
                   withSonarQubeEnv('sonar'){
                       sh "mvn sonar:sonar"
                   }
               }
           }
           stage('Quality Gate') {
               steps {
                   script {
                       def qualitygate = waitForQualityGate()
                       if (qualitygate.status != "OK"){
                           echo "Pipeline aborted due to quality gate coverage failure: ${qualitygate.status}"
                       } else {
                           echo "SonarQube analysis succesfull"
                       }
                   }
               }
           }
           stage('Build & Push the Docker Image'){
               steps{
                   dockerBuild("${DOCKER_REGISTRY}/$DOCKER_REPO/com.mydevopslab.hms/" + pipelineParams.ApplicationName, "$BUILD_NUMBER")
               }
           }
           stage('Push the docker image to Artifactory'){
               steps{
                   dockerPush("com.mydevopslab.hms/" + pipelineParams.ApplicationName, "$BUILD_NUMBER", "$DOCKER_REPO")
               }
           }
       }
   }
}