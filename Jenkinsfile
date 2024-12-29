pipeline {
    agent {
        docker {
            image 'openjdk:17-alpine'
        }
    }
    environment {
        GRADLE_USER_HOME = '/tmp/gradle'
    }
    parameters {
            choice(
                name: 'BUILD_TYPE',
                choices: ['Build', 'Release']
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
                expression { params.BUILD_TYPE == 'Release' }
            }
            steps {
                script {
                    sh './gradlew release'
                }
            }
        }
    }
    post {
        always {
            echo 'Cleaning up workspace...'
            cleanWs()
        }
        success {
            echo 'Build completed successfully!'
        }
        failure {
            echo 'Build failed. Check logs for details.'
        }
    }
}