#!/usr/bin/env bash

set -e

if [ $# -ne 1 ]; then
  echo "Usage: $0 [user-name]"
  echo ''
  echo 'Options:'
  echo '  user-name  User name'
  exit 1
fi

NAME=$1

function service_endpoint() {
   aws cloudformation describe-stacks --stack-name "${NAME}-$1" \
    --query 'Stacks[0].Outputs[?OutputKey==`ServiceEndpoint`].OutputValue' --output text
}

cat << EOF > data.json
[
  {
    "AuthorizationServer":"$(service_endpoint stack)",
    "MockService" : "$(service_endpoint sample)"
  }
]
EOF

docker pull postman/newman_ubuntu1404:latest
docker run -it -v "`pwd`:/mnt/" postman/newman_ubuntu1404 \
  --collection='/mnt/tests.postman_collection.json' --data='/mnt/data.json'
