pipeline {
  agent any
  environment {
    IMAGE_NAME = "myapp:latest"
    CONTAINER_NAME = "myapp"
    APP_PORT = "8080"
  }
  stages {
    stage('Build') {
      steps {
        script {
          if (isUnix()) {
            sh 'mvn clean package -DskipTests'
          } else {
            bat 'mvn clean package -DskipTests'
          }
        }
      }
    }

    stage('Build Docker Image') {
      steps {
        script {
          if (isUnix()) {
            sh "docker build -t ${env.IMAGE_NAME} ."
          } else {
            bat "docker build -t ${env.IMAGE_NAME} ."
          }
        }
      }
    }

    stage('Run container and show logs') {
      steps {
        script {
          if (isUnix()) {
            sh """
              set -e
              docker rm -f ${CONTAINER_NAME} || true
              docker run -d --name ${CONTAINER_NAME} -p ${APP_PORT}:${APP_PORT} ${IMAGE_NAME}
              sleep 8
              echo '--- Container status ---'
              docker ps --filter \"name=${CONTAINER_NAME}\"
              echo '--- Startup logs (tail) ---'
              docker logs --tail 200 ${CONTAINER_NAME} || true
              echo 'Application should be available at: http://localhost:${APP_PORT}'
            """
          } else {
            bat """
              @echo off
              docker rm -f ${CONTAINER_NAME} || echo ignored
              docker run -d --name ${CONTAINER_NAME} -p ${APP_PORT}:${APP_PORT} ${IMAGE_NAME}
              timeout /t 8 /nobreak >nul
              echo --- Container status ---
              docker ps --filter "name=${CONTAINER_NAME}"
              echo --- Startup logs (tail) ---
              docker logs --tail 200 ${CONTAINER_NAME} || echo ignored
              echo Application should be available at: http://localhost:${APP_PORT}
            """
          }
        }
      }
    }
  }

  post {
    always {
      script {
        echo "Finished pipeline. If you want continuous logs use: docker logs -f ${CONTAINER_NAME}"
      }
    }
  }
}
