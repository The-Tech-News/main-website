#!/bin/bash

set -euo pipefail

cd "$(cd "$(dirname "$0")" && pwd)"

docker build -f ./Dockerfile -t "anhvlt/the-tech-news:latest" .