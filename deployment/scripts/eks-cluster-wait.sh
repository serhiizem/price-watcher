#!/bin/bash

CLUSTER_NAME="price-watcher"
REGION="us-east-1"
MAX_WAIT_TIME=300  # 5 minutes in seconds
SLEEP_INTERVAL=20  # Time to wait between checks
START_TIME=$(date +%s)  # Record the start time

get_cluster_status() {
  aws eks describe-cluster --region $REGION --name $CLUSTER_NAME --query 'cluster.status' --output text
}

while true; do
  CURRENT_TIME=$(date +%s)
  ELAPSED_TIME=$((CURRENT_TIME - START_TIME))

  # Check if the elapsed time has exceeded the max wait time
  if [ $ELAPSED_TIME -gt $MAX_WAIT_TIME ]; then
    exit 1
  fi

  CLUSTER_STATUS=$(get_cluster_status)

  if [ "$CLUSTER_STATUS" == "ACTIVE" ]; then
    break
  else
    sleep $SLEEP_INTERVAL
  fi
done