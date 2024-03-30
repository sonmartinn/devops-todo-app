#!/usr/bin/env groovy

import groovy.transform.Field

@Field
String DOCKER_USER_REF = 'sonmartin'
@Field
String SSH_ID_REF = 'ssh-credentials-id'

pipeline {
    agent any

    tools {
        dockerTool 'docker'
    }

    stages {
        stage("build and test") {
            steps {
                // sh 'npm install'    // ví dụ: cài đặt các phụ thuộc npm
                // sh 'node app.js'
                // sh 'npm test'     // ví dụ: chạy các bài kiểm thử
            }
        }
        stage("Docker login and push docker image") {
            steps {
                withBuildConfiguration {
                    sh "docker build -t sonmartin/devops-todo-apps:0.0.2 ."
                    sh "docker push sonmartin/devops-todo-apps:0.0.2"
                }
            }
        }
        stage("deploy") {
            steps {
                withBuildConfiguration {
                    sshagent(credentials: [SSH_ID_REF]) {
                        sh "ssh -o StrictHostKeyChecking=no root@ec2-18-141-234-249.ap-southeast-1.compute.amazonaws.com"
                        sh "docker pull sonmartin/devops-todo-apps:0.0.1"
                        sh "docker run -p 8000:8000 sonmartin/devops-todo-apps:0.0.1"
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