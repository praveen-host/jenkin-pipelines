/* groovylint-disable CompileStatic */
pipeline {
    agent any
    environment {
        DOCKER_IMAGE = 'yadavpk/microservice-employee'
        DOCKER_TAG = 'latest'
        DOCKER_CREDENTIALS = credentials('dockerhub-creds')
    }
    stages {
        stage('Checkout From Master Branch') {
            steps {
                git branch: 'main', url: 'https://github.com/praveen-host/microservice-employee.git'
            }
        }
        stage('Restore Dependencies') {
            steps {
                sh 'dotnet restore microservice-employee.csproj'
            }
        }
        stage('Build') {
            steps {
                sh 'dotnet build  microservice-employee.csproj --configuration release'
            }
        }
        stage('login to DockerHub') {
            // steps {
            //     withcredentials([usernamePassword(credentialsId: 'dockerhub-creds', passwordVariable: 'DOCKER_CREDENTIALS_PSW', usernameVariable: 'DOCKER_CREDENTIALS_USR')]) {
            //         sh "echo ${DOCKER_CREDENTIALS_PSW} | docker login -u ${DOCKER_CREDENTIALS_USR} --password-stdin"
            //     }
            // }
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerhub-creds', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                    sh """
                echo $DOCKER_PASS | docker login -u $DOCKER_USER --password-stdin
            """
                }
            }
        }
    }
}
