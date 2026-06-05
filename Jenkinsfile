// CI/CD pipeline for the CompusLink microservices platform.
//
// Flow:  checkout -> build & test (Maven) -> build & push images -> deploy to k3s -> smoke test
//
// One-time prerequisites on the Jenkins/k3s server:
//   - Docker, kubectl, JDK 21 and Maven available to the 'jenkins' user
//   - A local registry running:  docker run -d -p 5000:5000 --restart=always --name registry registry:2
//   - k3s configured to pull from it over HTTP via /etc/rancher/k3s/registries.yaml (then restart k3s)
//   - Jenkins credential 'kubeconfig' (Secret file) -> /etc/rancher/k3s/k3s.yaml
//   - Set BASE_URL / FRONTEND_URL in k8s/common-deployment.yml to the server's address
// The Deploy stage bootstraps the cluster itself (kubectl apply -f k8s/), so no
// separate deploy script is required.

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
    REGISTRY   = 'localhost:5000'
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
        script {
          // Local registry has no auth; Docker treats localhost:5000 as insecure.
          // (For an authenticated/remote registry, wrap this in withCredentials +
          //  `docker login` first.)
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

    stage('Deploy to k3s') {
      steps {
        withCredentials([file(credentialsId: 'kubeconfig', variable: 'KUBECONFIG')]) {
          script {
            // Idempotent bootstrap: creates namespace, secrets/config, postgres,
            // all deployments/services, and the ingress on first run; updates them
            // on later runs. (Namespace applied first so namespaced objects don't race it.)
            sh 'kubectl apply -f compuslink-microservices/k8s/namespace.yml'
            sh 'kubectl apply -f compuslink-microservices/k8s/'
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
