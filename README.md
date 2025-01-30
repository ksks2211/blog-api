# Post API

## Overview
Spring Boot REST API for Blog system

---

## Checklist

### Web Basic Features
- [x] error handling
- [ ] logging
- [ ] unit test
- [x] request : validation
- [ ] response : structured
- [ ] internal : mapper (mapstruct)
- [ ] constants, enums

### Storages
- [x] Handle Image files upload
- [x] S3 + CDN(cloudfront)

### Relational Database
- [ ] spring data jpa
- [ ] dynamic query
- [ ] n + 1 problem

### Security
- [x] JWT Login
- [x] Refresh Token Login
- [x] OAuth2.0 Login
- [x] Session for OAuth2.0 Login Request

### Caching
- [x] cache with redis

### Environments
- [x] .env
- [ ] application-{profile}.yml
- [ ] secrets

### Documents
- [ ] swagger

### Deployments
- [x] docker


---

## Getting Started

### Run dev server
- `./gradlew bootRun --args='--spring.profiles.active=dev'`

### Build docker image
- `docker build -t blog-api:latest ./`

### Run docker image
```shell
docker run -d \
   -p 8080:8080 \
   -v ~/.aws:/root/.aws \
   --name blog-api \
   --env-file .env \
   blog-api:latest
```