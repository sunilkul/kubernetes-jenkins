pipeline {
  agent any

  parameters {
    booleanParam(name: 'PUSH_TO_REGISTRY', defaultValue: false, description: 'Push built image to registry (e.g., Docker Hub)')
    string(name: 'IMAGE_REPO', defaultValue: 'myuser/myapp', description: 'Image repository (repo/name)')
    string(name: 'IMAGE_TAG', defaultValue: 'latest', description: 'Image tag')
    string(name: 'HELM_RELEASE', defaultValue: 'myapp', description: 'Helm release name')
    string(name: 'HELM_CHART_PATH', defaultValue: './helm/myapp', description: 'Path to Helm chart')
    string(name: 'KUBE_CONTEXT', defaultValue: 'docker-desktop', description: 'Kubernetes context (Docker Desktop)')
  }

  environment {
    IMAGE = "${params.IMAGE_REPO}:${params.IMAGE_TAG}"
  }

  stages {
    stage('Build') {
      steps {
        // Use wrapper if available
        sh './gradlew clean bootJar || ./gradlew clean build'
      }
    }

    stage('Prepare Artifact') {
      steps {
        sh '''
          set -e
          JAR=$(ls build/libs/*.jar | head -n1)
          if [ -z "$JAR" ]; then
            echo "No jar found in build/libs"
            exit 1
          fi
          cp "$JAR" build/libs/kubernetes-0.0.1-SNAPSHOT.jar
          ls -la build/libs
        '''
      }
    }

    stage('Build Docker Image') {
      steps {
        sh "docker build -t ${IMAGE} ."
      }
    }

    stage('Push Docker Image') {
      when {
        expression { return params.PUSH_TO_REGISTRY == true }
      }
      steps {
        // Expects a Jenkins credential of type "Username with password" with id 'dockerhub'
        withCredentials([usernamePassword(credentialsId: 'dockerhub', usernameVariable: 'kulkarni11782@gmail.com', passwordVariable: 'Sunil@111082')]) {
          sh '''
            echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin
            docker push ${IMAGE}
            docker logout
          '''
        }
      }
    }

    stage('Deploy with Helm') {
      steps {
        script {
          // Select image pull policy based on whether image was pushed to registry
          def pullPolicy = params.PUSH_TO_REGISTRY ? 'Always' : 'IfNotPresent'

          sh """
            # ensure kube context points to Docker Desktop
            kubectl config use-context ${params.KUBE_CONTEXT}

            # Helm upgrade/install pointing to local chart
            helm upgrade --install ${params.HELM_RELEASE} ${params.HELM_CHART_PATH} \\
              --kube-context ${params.KUBE_CONTEXT} \\
              --set image.repository=${params.IMAGE_REPO} \\
              --set image.tag=${params.IMAGE_TAG} \\
              --set image.pullPolicy=${pullPolicy} \\
              --wait --timeout 5m
          """
        }
      }
    }
  }

  post {
    always {
      sh 'kubectl --context ${KUBE_CONTEXT} get pods || true'
    }
    failure {
      echo 'Deployment failed'
    }
  }
}
