# popool-server 
> 인사내역을 거래하고 관리하는 시스템
> 한이음 프로젝트에서 개발한 MSA 기반 인사내역 거래 시스템을 바탕으로 리팩토링하는 서버입니다.

- 프로젝트 기간 : 2022.11.-continue

## 프로젝트를 진행하며 정리한 글
- <a href="https://hongdosan.tistory.com/entry/%EC%9A%B0%EC%95%84%ED%95%9C-%EB%A9%80%ED%8B%B0-%EB%AA%A8%EB%93%88-%EC%84%B8%EB%AF%B8%EB%82%98-%EC%A0%95%EB%A6%AC-%ED%95%9C%EC%9D%B4%EC%9D%8C%EC%9D%84-%ED%86%B5%ED%95%B4-%EC%8B%9C%EC%9E%91%ED%95%9C-MSA%EC%99%80-%EB%A9%80%ED%8B%B0-%EB%AA%A8%EB%93%88">멀티 모듈 완벽 정리</a><br/><br/>

## 개요
한이음 MSA Project 진행을 했습니다.
이 프로젝트에서 Eureka, API Gateway, Kafka, Spring OAuth, Spring Batch, Redis, Spring Security, Json Web Token 등 기술을 공부하고 적용했습니다.

하지만, 프로젝트를 진행하면서 깊게 학습하지 않는 자신을 봤습니다.
저는 그저 구현만 하는 개발자가 되어가고 있었습니다.

때문에, 아직 완벽하지 않은 자신을 되돌아보며, 기술 하나씩 다시 깊게 공부하기 위해,
Monolithic Architecture한 멀티 모듈로 리팩토링을 진행하며 학습해야겠다 생각했습니다.

## Folder Structure
```
.
├── p-application
│   ├── ..
│   └── src
│       ├── main
│       │   ├── java.kr.co.popoolserver
│       │   │               ├── admin..
│       │   │               ├── config..
│       │   │               ├── consumer..
│       │   │               ├── handler..
│       │   │               ├── interceptor..
│       │   │               └── ApiApplication.java
│       │   └── ..
│       └── ..
├── p-core
│   ├── ..
│   └── src
│       ├── main
│       │   ├── java.kr.co.popoolserver
│       │   │               ├── enums..
│       │   │               └── error..
│       │   └── ..
│       └── ..
├── p-domain-admin
│   ├── ..
│   └── src
│       ├── main
│       │   ├── java.kr.co.popoolserver
│       │   │               ├── config..
│       │   │               ├── entity..
│       │   │               ├── repository..
│       │   │               └── DomainAdminApplication.java
├── p-domain-consumer
│   ├── ..
│   └── src
│       ├── main
│       │   ├── java.kr.co.popoolserver
│       │   │               ├── config..
│       │   │               ├── entity..
│       │   │               ├── repository..
│       │   │               └── DomainConsumerApplication.java
│       │   └── ..
│       └── ..
├── p-domain-jwt
│   ├──..
│   └── src
│       ├── main
│       │   ├── java.kr.co.popoolserver
│       │   │               ├── DomainJWTApplication.java
│       │   │               └── provider.JwtProvider.java
├── p-domain-redis
│   ├──..
│   └── src
│       ├── main
│       │   ├── java.kr.co.popoolserver
│       │   │               ├── DomainRedisApplication.java
│       │   │               ├── config.RedisConfig.java
│       │   │               └── service.RedisService.java
│       │   └── ..
│       └── ..
├── p-domain-s3
│   ├── ..
│   └── src
│       ├── main
│       │   ├── java.kr.co.popoolserver
│       │   │               ├── DomainS3Application.java
│       │   │               ├── config.AwsConfig.java
│       │   │               ├── dto.S3Dto.java
│       │   │               └── service.S3Service.java
│       │   └── ..
│       └── ..
├── p-internal
│   ├── ..
└── settings.gradle
```

## 기술 스택
> 해당 프로젝트를 수행하며 사용한 기술 스택 및 사용할 기술 스택
- [x] Java 11, MySQL, Gradle, Spring Boot 2.6.7 
- [x] Spring Security, Json Web Token
- [x] Spirng Swagger, Spring Interceptor
- [x] AWS S3
- [x] Sping Data JPA
- [x] Redis
- [ ] Querydsl
- [ ] Spring Batch, RabbitMQ, Kafka
- [ ] AWS EC2, RDS
- [ ] Docker, Jenkins, nginx

## 서비스 소개
회원들이 자신의 이력을 올리면, 기업에서 이력을 찾아보는 서비스입니다.

## 설계 (바뀔 수 있습니다.)
<img width="753" alt="image" src="https://user-images.githubusercontent.com/31675711/206408233-c821a435-c771-490a-9c65-69ccd3f3ec0e.png">

## API (바뀔 수 있습니다.)

### [Consumer Module]

#### - 회원 API
<img width="1096" alt="image" src="https://user-images.githubusercontent.com/31675711/208947124-0a589159-9a71-40e7-8108-6376bedb3883.png">

#### - 이력서 API
<img width="507" alt="image" src="https://user-images.githubusercontent.com/31675711/206406394-7f10ea57-0a46-43dc-b6f4-8bb6f7225d6d.png">

#### - 상품 API
<img width="1072" alt="image" src="https://user-images.githubusercontent.com/31675711/206407558-03a7fa06-31ae-4155-aca5-07b5957d7927.png">

### [Admin Module]

#### - 관리자 API
<img width="999" alt="image" src="https://user-images.githubusercontent.com/31675711/208947281-3b310382-7617-44e9-b567-decd619fc834.png">

#### - Continue...
현재 nginx 공부 중에 있습니다.
빠른 시일 내로 nginx 공부 후 프로젝트에 적용하러 돌아오겠습니다.
nginx를 공부하니, CI/CD도 다시 공부해보고 싶어, Github Actions를 공부하고 돌아오겠습니다!

