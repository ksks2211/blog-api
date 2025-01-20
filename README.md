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

### Relational Database
- [ ] spring data jpa
- [ ] dynamic query
- [ ] n + 1 problem


### Security
- [x] JWT Login
- [ ] OAuth2.0 Login
- [ ] Session for OAuth2.0 Login Request

### Caching
- [ ] cache with redis

### Environments
- [ ] .env
- [ ] application-{profile}.yml
- [ ] secrets

### Documents
- [ ] swagger

### Deployments
- [ ] docker


---

## Getting Started

### Run dev server
- `./gradlew bootRun --args='--spring.profiles.active=dev''`