#!/bin/bash

set -euo pipefail

cd "$(cd "$(dirname "$0")" && pwd)"

docker build -f ./.docker/dockerfile-web -t "anhvlt/the-tech-news:web-latest" .
docker build -f ./.docker/dockerfile-db -t "anhvlt/the-tech-news:db-latest" .