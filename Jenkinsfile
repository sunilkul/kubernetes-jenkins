pipeline {
  agent any
  environment {
    IMAGE = "myapp:latest"
    CONTAINER = "myapp-ci"
    APP_PORT = "8080"
    JAR_GLOB = "build/libs/*.jar"
    EXPECTED_JAR = "build/libs/myapp.jar"
  }
  stages {
    stage('Checkout') {
      steps { checkout scm }
    }

    stage('Build JVM artifact') {
      steps {
        script {
          if (isUnix()) {
            sh './gradlew clean bootJar -x test'
          } else {
            bat 'gradlew.bat clean bootJar -x test'
          }
        }
      }
    }

    stage('Ensure JAR exists') {
      steps {
        script {
          if (isUnix()) {
            sh '''
              set -e
              J=$(ls ${JAR_GLOB} 2>/dev/null | head -n1 || true)
              if [ -z "$J" ]; then echo "No jar found in build/libs"; exit 1; fi
              cp "$J" ${EXPECTED_JAR}
              ls -la build/libs
            '''
          } else {
            bat """
              @echo off
              for /f "delims=" %%F in ('dir /b ${JAR_GLOB} 2^>nul') do set JAR=%%F & goto :found
              echo No jar found in build\\libs & exit /b 1
              :found
              copy /Y build\\libs\\%JAR% ${EXPECTED_JAR} >nul
              dir build\\libs
            """
          }
        }
      }
    }

    stage('Build Docker image') {
      steps {
        script {
          if (isUnix()) {
            sh "docker build -t ${IMAGE} ."
            sh "docker images --filter=reference=${IMAGE}"
          } else {
            bat "docker build -t ${IMAGE} ."
            bat "docker images --filter=reference=${IMAGE}"
          }
        }
      }
    }

    stage('Run container and show startup logs') {
      steps {
        script {
          if (isUnix()) {
            sh """
              docker rm -f ${CONTAINER} || true
              docker run -d --name ${CONTAINER} -p ${APP_PORT}:${APP_PORT} ${IMAGE}
              sleep 8
              echo '--- docker ps ---'
              docker ps --filter name=${CONTAINER}
              echo '--- logs (tail) ---'
              docker logs --tail 200 ${CONTAINER} || true
              echo 'Application URL: http://localhost:${APP_PORT}'
            """
          } else {
            bat """
              @echo off
              docker rm -f ${CONTAINER} || echo ignored
              docker run -d --name ${CONTAINER} -p ${APP_PORT}:${APP_PORT} ${IMAGE}
              timeout /t 8 /nobreak >nul
              echo --- docker ps ---
              docker ps --filter "name=${CONTAINER}"
              echo --- logs (tail) ---
              docker logs --tail 200 ${CONTAINER} || echo ignored
              echo Application URL: http://localhost:${APP_PORT}
            """
          }
        }
      }
    }
  }

  post {
    always {
      script {
        echo 'Pipeline finished. If container started you can stream logs with: docker logs -f ' + env.CONTAINER
      }
    }
    failure {
      echo 'Pipeline failed - check earlier stage logs'
    }
  }
}
