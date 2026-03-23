#!/bin/bash

set -euo pipefail

cd "$(cd "$(dirname "$0")" && pwd)"

docker build -f ./dockerfile-web -t "anhvlttfs/the-tech-news:web-latest" ..
docker build -f ./dockerfile-db -t "anhvlttfs/the-tech-news:db-latest" ..