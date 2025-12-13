/* groovylint-disable CompileStatic */
pipeline {
    agent any
    environment {
        DOCKER_IMAGE = 'yadavpk/microservice-employee'
        DOCKER_TAG = 'latest'
        DOCKER_CREDENTIALS = credentials('dockerhub-creds')
        CHANGESET_NUMBER = ''
    }
    stages {
        stage('Checkout From Master Branch') {
            steps {
                git branch: 'main', url: 'https://github.com/praveen-host/microservice-employee.git'
            }
        }
        stage('Build Docker Image') {
            steps {
                sh "docker build -t ${DOCKER_IMAGE}:${CHANGESET_NUMBER} ."
            }
        }
        stage('login to DockerHub') {
            steps {
                /* groovylint-disable-next-line DuplicateStringLiteral */
                withCredentials([usernamePassword(credentialsId: 'dockerhub-creds', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                    sh """
                echo $DOCKER_PASS | docker login -u $DOCKER_USER --password-stdin
            """
                }
            }
        }
        stage('Push image to dockerhub') {
            steps {
                sh "docker push ${DOCKER_IMAGE}:${CHANGESET_NUMBER}"
            }
        }
     }
}
