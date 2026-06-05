#!/bin/bash

NAMESPACE="compuslink"

# Connexion au démon Docker interne de Minikube
eval $(minikube docker-env)

# Déploiement et attente de la base de données
echo "Déploiement de Postgres..."
kubectl apply -f k8s/postgres.yaml
kubectl rollout status deployment/postgres -n $NAMESPACE

# Déploiement et attente du cœur réseau
echo "Déploiement de Eureka et de l'API Gateway..."
kubectl apply -f k8s/eureka-deployment.yml
kubectl apply -f k8s/gateway-deployment.yml
kubectl rollout status deployment/eureka -n $NAMESPACE
kubectl rollout status deployment/api-gateway -n $NAMESPACE

# Liste des microservices à traiter
MICROSERVICES=("user" "colocation" "marketplace" "offer" "event" "messaging")

# Validation séquentielle pour préserver la RAM
for svc in "${MICROSERVICES[@]}"; do
    echo "Traitement en cours : $svc-service"

    # 1. Construction de l'image
    minikube image build -t compuslink/$svc-service:latest ./$svc-service

    # 2. Déploiement
    kubectl apply -f k8s/$svc-deployment.yml

    # 3. Vérification du statut
    kubectl rollout status deployment/$svc-service -n $NAMESPACE

    # 4. Extinction pour libérer la mémoire
    kubectl scale deployment $svc-service --replicas=0 -n $NAMESPACE
    
    echo "$svc-service validé."
done

echo "Séquence de validation terminée avec succès."