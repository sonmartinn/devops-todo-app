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
                sh 'npm install'    // ví dụ: cài đặt các phụ thuộc npm
                sh 'node app.js'
                sh 'npm test'     // ví dụ: chạy các bài kiểm thử
            }
        }
        stage("Docker login and push docker image") {
            steps {
                withBuildConfiguration {
                    sh "docker build -t sonmartin/devops-todo-apps:0.0.1 ."
                    sh "docker push sonmartin/devops-todo-apps:0.0.1"
                }
            }
        }
        stage("deploy") {
            steps {
                withBuildConfiguration {
                    sshagent(credentials: [ssh-credentials-id]) {
                        sh "ssh -o StrictHostKeyChecking=no root@ec2-18-141-234-249.ap-southeast-1.compute.amazonaws.com"
                    }
                }
            }
        }
    }
}

void withBuildConfiguration(Closure body) {
    withCredentials([usernamePassword(credentialsId: v-docker-hub, usernameVariable: 'repository_username', passwordVariable: 'repository_password')]) {
        body()
    }
}