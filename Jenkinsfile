pipeline {
    agent any

    stages {
        stage('Hello') {
            agent {
                docker {
                    image 'node:18-alpine'
                }
            }
            steps {
                sh """
                    docker --version
                """
            }
        }
    }
}
