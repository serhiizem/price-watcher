pipeline {
    agent any

    stages {
        stage('Hello') {
            steps {
                sh """
                    docker --version
                    echo 'Hello World'
                """
            }
        }
    }
}
