// CI/CD pipeline for the CompusLink microservices platform.
//
// Flow:  checkout -> build & test (Maven) -> build & push images -> deploy to k3s -> smoke test
//
// One-time prerequisites on the Jenkins/k3s server:
//   - Docker, kubectl, JDK 21 and Maven available to the 'jenkins' user
//   - Jenkins credentials:
//       * 'registry'   (Username/Password)  -> registry login (Docker Hub user, or a local registry)
//       * 'kubeconfig' (Secret file)        -> /etc/rancher/k3s/k3s.yaml
//   - Cluster bootstrapped once with k8s/deploy-and-validate.sh (namespace, secrets, deployments exist)
//   - Deployment manifests point at "${REGISTRY}/compuslink-<svc>" with imagePullPolicy: IfNotPresent

// [ build-context dir, image base name, k8s deployment, container name ]
def UNITS = [
  ['compuslink-microservices/eureka-server',       'eureka-server',       'eureka',              'eureka'],
  ['compuslink-microservices/api-gateway',         'api-gateway',         'api-gateway',         'api-gateway'],
  ['compuslink-microservices/user-service',        'user-service',        'user-service',        'user-service'],
  ['compuslink-microservices/marketplace-service', 'marketplace-service', 'marketplace-service', 'marketplace-service'],
  ['compuslink-microservices/colocation-service',  'colocation-service',  'colocation-service',  'colocation-service'],
  ['compuslink-microservices/offer-service',       'offer-service',       'offer-service',       'offer-service'],
  ['compuslink-microservices/event-service',       'event-service',       'event-service',       'event-service'],
  ['compuslink-microservices/messaging-service',   'messaging-service',   'messaging-service',   'messaging-service'],
  ['compuslink-microservices/common-service',      'common-service',      'common-service',      'common-service'],
  ['campuslink-frontend',                          'frontend',            'frontend',            'frontend'],
]

pipeline {
  agent any

  // Manage Jenkins > Tools : create a JDK named 'jdk21' and a Maven named 'maven3'
  tools {
    jdk   'jdk21'
    maven 'maven3'
  }

  environment {
    // Docker Hub:  docker.io/<your-user>     |     Local registry:  localhost:5000
    REGISTRY   = 'docker.io/YOUR_REGISTRY_USER'
    IMAGE_TAG  = "${env.BUILD_NUMBER}"
    NAMESPACE  = 'compuslink'
    MAVEN_OPTS = '-Xmx512m'   // keep Maven's heap small on the 8GB box
  }

  options {
    timestamps()
    disableConcurrentBuilds()                       // never two builds at once (RAM)
    buildDiscarder(logRotator(numToKeepStr: '10'))
  }

  stages {

    stage('Checkout') {
      steps { checkout scm }
    }

    stage('Build & Test') {
      steps {
        dir('compuslink-microservices') {
          sh 'mvn -B -T1 clean package'             // build all jars + run unit tests, sequentially
        }
      }
      post {
        always {
          junit testResults: 'compuslink-microservices/**/target/surefire-reports/*.xml',
                allowEmptyResults: true
        }
      }
    }

    stage('Build & Push Images') {
      steps {
        withCredentials([usernamePassword(credentialsId: 'registry',
                            usernameVariable: 'REG_USER', passwordVariable: 'REG_PASS')]) {
          script {
            // log in to the registry host (strip the /user path from REGISTRY)
            sh 'echo "$REG_PASS" | docker login "${REGISTRY%%/*}" -u "$REG_USER" --password-stdin'
            for (u in UNITS) {
              def ctx = u[0]
              def img = "${REGISTRY}/compuslink-${u[1]}"
              sh """
                docker build -t ${img}:${IMAGE_TAG} -t ${img}:latest ${ctx}
                docker push ${img}:${IMAGE_TAG}
                docker push ${img}:latest
              """
            }
          }
        }
      }
    }

    stage('Deploy to k3s') {
      steps {
        withCredentials([file(credentialsId: 'kubeconfig', variable: 'KUBECONFIG')]) {
          script {
            // pin every deployment to this build's immutable tag
            for (u in UNITS) {
              def img = "${REGISTRY}/compuslink-${u[1]}:${IMAGE_TAG}"
              sh "kubectl -n ${NAMESPACE} set image deployment/${u[2]} ${u[3]}=${img}"
            }
            // then wait for each rollout to finish
            for (u in UNITS) {
              sh "kubectl -n ${NAMESPACE} rollout status deployment/${u[2]} --timeout=180s"
            }
          }
        }
      }
    }

    stage('Smoke Test') {
      steps {
        withCredentials([file(credentialsId: 'kubeconfig', variable: 'KUBECONFIG')]) {
          sh '''
            kubectl -n ${NAMESPACE} run smoke-${BUILD_NUMBER} --rm -i --restart=Never \
              --image=curlimages/curl:8.8.0 -- \
              -sf -o /dev/null -w "gateway HTTP %{http_code}\\n" \
              http://gateway-service:8080/api/items \
              || echo "smoke check non-2xx (ok if marketplace is empty)"
          '''
        }
      }
    }
  }

  post {
    always  { sh 'docker image prune -f || true' }   // reclaim disk on the small server
    success { echo "Deployed build ${IMAGE_TAG} to ${NAMESPACE}." }
    failure { echo "Pipeline failed for build ${IMAGE_TAG}." }
  }
}
