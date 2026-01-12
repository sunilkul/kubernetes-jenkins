pipeline {
    agent any
    stages {
        stage('Build') {
            steps {
                sh 'mvn clean package' // or './gradlew build' for Gradle
            }
        }
        stage('Build Docker Image') {
            steps {
                sh 'docker build -t myapp:latest .'
            }
        }
    }
}