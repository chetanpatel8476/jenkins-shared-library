#!/usr/bin/env groovy 

def call(String IMAGE_NAME='',String IMAGE_TAG='latest',String DOCKER_REPO='') {
    //def nodeip = sh (script: 'curl http://169.254.169.254/latest/meta-data/local-ipv4',returnStdout: true).trim()
    def server = Artifactory.server 'artifactory-mydevopslab'
    def rtDocker = Artifactory.docker server: server //, host: "tcp://"+"${nodeip}"+":2375"
    rtDocker.addProperty("Jenkins-build", "${BUILD_URL}".toLowerCase()).addProperty("Git-Url", "${GIT_URL}".toLowerCase())
    def buildInfo = rtDocker.push "${DOCKER_REGISTRY}/${DOCKER_REPO}/${IMAGE_NAME}:${IMAGE_TAG}", "${DOCKER_REPO}"
    server.publishBuildInfo buildInfo
}