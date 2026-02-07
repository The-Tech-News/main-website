# The Tech News

[![Build Maven Package](https://github.com/The-Tech-News/main-website/actions/workflows/maven.yml/badge.svg)](https://github.com/The-Tech-News/main-website/actions/workflows/maven.yml)
[![Build Web App to Docker Container](https://github.com/The-Tech-News/main-website/actions/workflows/build-docker.yml/badge.svg)](https://github.com/The-Tech-News/main-website/actions/workflows/build-docker.yml)
[![CodeQL](https://github.com/The-Tech-News/main-website/actions/workflows/github-code-scanning/codeql/badge.svg)](https://github.com/The-Tech-News/main-website/actions/workflows/github-code-scanning/codeql)
[![Dependabot Updates](https://github.com/The-Tech-News/main-website/actions/workflows/dependabot/dependabot-updates/badge.svg)](https://github.com/The-Tech-News/main-website/actions/workflows/dependabot/dependabot-updates)

Secondary site (for failover): [![GitLab CI/CD](https://img.shields.io/badge/gitlab-repo-orange?logo=gitlab)](https://devops.theflightsims.com/anhvlttfs/the-tech-news)

This repo contains the main web page of the project for PRJ301

## Build

### Running in development

Try to open the repo with NetBeans

> Require JDK 21 and Tomcat 10.1 to run

### With Docker

```bash
./build_docker.sh
docker compose up -d
```
