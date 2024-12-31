pipeline {
    agent {
        docker {
            image 'openjdk:17-alpine'
        }
    }
    parameters {
            choice(
                name: 'BUILD_TYPE',
                choices: ['BUILD', 'RELEASE']
            )
        }
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        stage('Build') {
            steps {
                sh './gradlew clean build'
            }
        }
        stage('Test') {
            steps {
                sh './gradlew test'
            }
        }
        stage('Release') {
            when {
                expression { params.BUILD_TYPE == 'RELEASE' }
            }
            steps {
                withCredentials([string(credentialsId: 'jenkins-git-pat', variable: 'GITHUB_TOKEN')]) {
                    sh './gradlew release'
                }
            }
        }
    }
    post {
        success {
            echo 'Build completed successfully!'
        }
        failure {
            echo 'Build failed. Check logs for details.'
        }
    }
}