pipeline {
    agent any
    parameters {
        choice(
           name: 'BUILD_TYPE',
           choices: ['BUILD', 'RELEASE']
       )
    }
    stages {
        stage('Checkout') {
            steps {
                git branch: 'master', url: 'https://github.com/serhiizem/price-watcher.git'
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
            environment {
                GIT_REPO_NAME = "price-watcher"
                GIT_USER_NAME = "serhiizem"
            }
            steps {
                withCredentials([string(credentialsId: 'github-pat', variable: 'GITHUB_TOKEN')]) {
                    sh """
                        "git config --global user.name 'serhiizem'"
                        "git config --global user.email 'serhiizem@gmail.com'"
                        "git remote set-url origin https://${GITHUB_TOKEN}@github.com/${GIT_USER_NAME}/${GIT_REPO_NAME}"
                        "./gradlew release -Prelease.useAutomaticVersion=true"
                    """
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