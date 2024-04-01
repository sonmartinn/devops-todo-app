#!/usr/bin/env groovy

import groovy.transform.Field

@Field
String DOCKER_USER_REF = 'sontrung-dockerhub-id'
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
                sh "ls -la"
                sh "docker build -t sonmartin/devops-todo-apps:0.0.2 ."
            }
        }
        stage("Docker login and push docker image") {
            steps {
                withCredentials([usernamePassword(credentialsId: DOCKER_USER_REF, usernameVariable: 'USER', passwordVariable: 'PASSWD')]) {
                    sh 'docker login -u "$USER" -p "$PASSWD"'
                    sh 'docker push sonmartin/devops-todo-apps:0.0.2'
                }
            }
        }
        stage("deploy") {
            steps {
                sshagent(credentials: [SSH_ID_REF]) {
                        sh '''
                        ssh -o StrictHostKeyChecking=no root@ec2-18-143-167-76.ap-southeast-1.compute.amazonaws.com
                        "docker run -d --rm --name todo-app-sontrung -p 8060:8000 sonmartin/devops-todo-apps:0.0.2 &&
                        docker ps"
                        '''
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