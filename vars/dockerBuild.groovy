#!/usr/bin/env groovy 

def call(String IMAGE_NAME='mydevopslab.jfrog.io/undefined',
String IMAGE_TAG='latest',
String DOCKER_IMAGE_NAME='Dockerfile',
String DOCKER_FILE_PATH='.',
String DOCKER_EXTRA_ARGS='') 
{
    docker.build("${IMAGE_NAME}:${IMAGE_TAG}","-f ${DOCKER_IMAGE_NAME} ${DOCKER_FILE_PATH} ${DOCKER_EXTRA_ARGS}")
}