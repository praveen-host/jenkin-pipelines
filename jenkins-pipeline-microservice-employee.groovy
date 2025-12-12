/* groovylint-disable CompileStatic */
pipeline {
    agent any
    environment {
        DOCKER_IMAGE = 'yadavpk/microservice-employee'
        DOCKER_TAG = 'latest'
        DOCKER_CREDENTIALS = credentials('dockerhub-creds')
        CHANGESET_NUMBER=''
    }
    stages {
        stage('Checkout From Master Branch') {
            steps {
                git branch: 'main', url: 'https://github.com/praveen-host/microservice-employee.git'
            }
        }
        stage('Parese ReadMe File') {
            steps {
                script {
                    def readmeContent = readFile 'README.md'
                    def versionMatch   = (readmeContent =~ /(?m)^\s*Version\s*:\s*([^\s]+)\s*$/)
                    if (versionMatch.find()) {
                        env.DOCKER_TAG = versionMatch.group(1)
                    }                    
                    env.CHANGESET_NUMBER = sh(script: 'git rev-list --count HEAD', returnStdout: true).trim()
                }                
            }
        }
        stage('Build Docker Image') {
            steps {
                sh "docker build -t ${DOCKER_IMAGE}:${DOCKER_TAG} ."
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
                sh "docker push ${DOCKER_IMAGE}:${DOCKER_TAG}.${CHANGESET_NUMBER}"
            }
        }
     }
}
