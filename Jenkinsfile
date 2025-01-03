pipeline {
    agent any
    parameters {
        choice(
           name: 'BUILD_TYPE',
           choices: ['BUILD', 'RELEASE']
       )
    }
    stages {
        stage('Debug') {
            steps {
                script {
                    echo "BRANCH_NAME: ${env.BRANCH_NAME}"
                    echo "GIT_URL: ${env.GIT_URL}"
                }
            }
        }
        stage('Checkout') {
            steps {
                git branch: "${env.BRANCH_NAME}", url: "${env.GIT_URL}"
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
                withCredentials([string(credentialsId: 'github-pat', variable: 'GITHUB_TOKEN')]) {
                    script {
                        PROJECT_PATH = "${env.GIT_URL}".replaceFirst("https://", "")
                    }
                    sh "git config --global user.name 'Jenkins'"
                    sh "git config --global user.email ''"
                    sh "git remote set-url origin https://${GITHUB_TOKEN}@${PROJECT_PATH}"
                    sh "./gradlew release -Prelease.useAutomaticVersion=true"
                }
            }
        }
    }
}