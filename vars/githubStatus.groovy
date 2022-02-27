#!/usr/bin/env groovy
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.URL
import java.net.URLConnection

def call(String buildStatus = 'STARTED') {
    println " buildstatus is ${buildStatus}"
  //buildStatus =  buildStatus ?: 'succes'
    if (buildStatus == 'STARTED') {
        ghstatus = 'pending'
    } else if (buildStatus == 'SUCCESS') {
    // build status of null means successful
        ghstatus = 'success'
    } else {
        ghstatus = 'error'
    }

    GIT_URL = 'https://github.com/chetanpatel8476/Student-DynamoDB-Application.git'
    GIT_COMMIT = '5d253cd7b0b55fad0641c02431a38e5ec365af28'
    BUILD_URL = 'https://github.com/chetanpatel8476/Student-DynamoDB-Application/commit/5d253cd7b0b55fad0641c02431a38e5ec365af28'
    gh_api_endpoint = 'https://github.com/chetanpatel8476'
    repos = ("${GIT_URL}" =~ /.*:(.+)\.git/)[ 0 ][ 1 ]
    println "extracted repos string is ${repos}"
    status_url = "${gh_api_endpoint}/repos/${repos}/statuses/${GIT_COMMIT}"
    msg = """
        { "state": "${ghstatus}",
        "target_url": "${BUILD_URL}" }
    """
    httpRequest acceptType: 'APPLICATION_JSON', contentType: 'APPLICATION_JSON', httpMode: 'POST', requestBody: "${msg}", url: "${status_url}"
}