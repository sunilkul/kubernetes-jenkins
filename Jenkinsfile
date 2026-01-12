pipeline {
    agent any
    environment {
        IMAGE_NAME = "myapp:latest"
    }
    stages {
        stage('Build JAR') {
            steps {
                sh 'mvn clean package'
            }
        }
        stage('Build Docker Image') {
            steps {
                sh "docker build -t $IMAGE_NAME ."
            }
        }
        stage('Run Docker Container') {
            steps {
                // Stop and remove any existing container with the same name
                sh "docker rm -f myapp || true"
                // Run the new container
                sh "docker run -d --name myapp -p 8080:8080 $IMAGE_NAME"
            }
        }
    }
}