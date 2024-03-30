#!/usr/bin/env groovy

import groovy.transform.Field

@Field
String DOCKER_USER_REF = '<DOCKERHUB_ID_PLACEHOLDER>'
@Field
String SSH_ID_REF = '<SSH_ID_PLACEHOLDER>'

pipeline {
    agent any

    tools {
        dockerTool 'docker'
    }

    stages {
        stage("build and test") {
            steps {
                // TODO here
            }
        }
        stage("Docker login and push docker image") {
            steps {
                withBuildConfiguration {
                    // TODO here
                }
            }
        }
        stage("deploy") {
            steps {
                withBuildConfiguration {
                    sshagent(credentials: [SSH_ID_REF]) {
                        sh '''
                            // TODO here
                        '''
                    }
                }
            }
        }
    }
}

void withBuildConfiguration(Closure body) {
    withCredentials([usernamePassword(credentialsId: DOCKER_USER_REF, usernameVariable: 'repository_username', passwordVariable: 'repository_password')]) {
        body()
    }
}