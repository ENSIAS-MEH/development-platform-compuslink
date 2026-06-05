#!/bin/bash
set -e

NAMESPACE="compuslink"

# Connexion au démon Docker interne de Minikube.
# (Sur k3s : remplacer "minikube image build" par un docker build + import :
#   docker build -t compuslink/<svc>:latest <dir>
#   docker save compuslink/<svc>:latest | sudo k3s ctr images import -)
eval $(minikube docker-env)

# --- Pré-requis : namespace + secrets/config, sinon AUCUN pod ne démarre ---
echo "Création du namespace et des secrets/config..."
kubectl apply -f k8s/namespace.yml
kubectl apply -f k8s/common-deployment.yml   # Secret compuslink-secrets + ConfigMap compuslink-config

# --- Base de données ---
echo "Déploiement de Postgres..."
kubectl apply -f k8s/postgres.yaml
kubectl rollout status deployment/postgres -n $NAMESPACE

# --- Cœur réseau ---
echo "Déploiement de Eureka et de l'API Gateway..."
kubectl apply -f k8s/eureka-deployment.yml
kubectl apply -f k8s/gateway-deployment.yml
kubectl rollout status deployment/eureka -n $NAMESPACE
kubectl rollout status deployment/api-gateway -n $NAMESPACE

# --- Microservices applicatifs ---
# common-service est inclus (saved/favoris + signalements).
MICROSERVICES=("user" "colocation" "marketplace" "offer" "event" "messaging" "common")

# Construction séquentielle pour préserver la RAM (8 Go) : on construit, on vérifie,
# puis on éteint le pod avant de passer au suivant. Tout est rallumé à la fin.
for svc in "${MICROSERVICES[@]}"; do
    echo "Traitement en cours : $svc-service"

    # 1. Construction de l'image (le dossier source est toujours <svc>-service)
    minikube image build -t compuslink/$svc-service:latest ./$svc-service

    # 2. Déploiement
    #    common-service utilise un nom de manifeste différent des autres.
    if [ "$svc" = "common" ]; then
        kubectl apply -f k8s/common-service-deployment.yml
    else
        kubectl apply -f k8s/$svc-deployment.yml
    fi

    # 3. Vérification du statut
    kubectl rollout status deployment/$svc-service -n $NAMESPACE

    # 4. Extinction temporaire pour libérer la mémoire pendant la build suivante
    kubectl scale deployment $svc-service --replicas=0 -n $NAMESPACE

    echo "$svc-service validé."
done

# --- Frontend (construit depuis le dépôt frontend séparé) ---
echo "Traitement : frontend"
minikube image build -t compuslink/frontend:latest ../campuslink-frontend
kubectl apply -f k8s/frontend-deployment.yml
kubectl rollout status deployment/frontend -n $NAMESPACE
kubectl scale deployment frontend --replicas=0 -n $NAMESPACE

# --- Routage HTTP ---
echo "Déploiement de l'Ingress..."
kubectl apply -f k8s/ingress.yml

# --- Démarrage de l'ensemble du système ---
# Rallume tout (les services mis à l'échelle 0 pendant la build reviennent à 1).
# ATTENTION 8 Go : faire tourner ~10 JVM + Postgres en même temps est serré.
# Si la RAM sature, commenter cette ligne et démarrer les services au besoin
# (kubectl scale deployment <nom> --replicas=1 -n compuslink).
echo "Démarrage de l'ensemble du système..."
kubectl scale deployment --all --replicas=1 -n $NAMESPACE

echo "Déploiement terminé. État des pods :"
kubectl get pods -n $NAMESPACE
