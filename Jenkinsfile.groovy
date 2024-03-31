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
                sh "docker build -t sonmartin/devops-todo-apps:0.0.2 ."
            }
        }
        stage("Docker login and push docker image") {
            steps {
                // withBuildConfiguration {
                //     sh 'docker login -u "$repository_username" -p "$repository_password"'
                //     sh "docker push sonmartin/devops-todo-apps:0.0.2"
                // }
                withCredentials([usernamePassword(credentialsId: DOCKER_USER_REF, usernameVariable: 'USER', passwordVariable: 'PASSWD')]) {
                    sh 'docker login -u "$USER" -p "$PASSWD"'
                    sh 'docker push sonmartin/devops-todo-apps:0.0.2'
                }
            }
        }
        stage("deploy") {
            steps {
                sshagent(credentials: [SSH_ID_REF]) {
                        // sh "ssh -tt -vvv root@ec2-18-141-234-249.ap-southeast-1.compute.amazonaws.com"
                        // sh "ssh -tt -vvv root@ec2-18-142-231-213.ap-southeast-1.compute.amazonaws.com"
                        // sh "ssh -tt -vvv ec2-54-252-190-112.ap-southeast-2.compute.amazonaws.com"
                        // sh "docker pull sonmartin/devops-todo-apps:0.0.1"
                        // sh "docker run -p 8000:8000 sonmartin/devops-todo-apps:0.0.1"
                        sh '''
                        docker run -d --rm --name todo-app -p 8000:8000 sonmartin/devops-todo-apps:0.0.2
                        docker ps
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