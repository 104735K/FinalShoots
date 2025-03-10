## ⚽️ [FINAL] 풋살 매치 사이트 Shoots 

![Image](https://github.com/user-attachments/assets/c7f91ea5-b554-456c-bf26-fd2f800040e0)


🔗 배포 URL : [https://www.goshoots.site](https://www.goshoots.site/Shoots/main)


## 💻 PROJECT INTRODUCTION
- 목적 
  - 일회성 매치시스템과 커뮤니티를 함께 구현하여 취미 축구인들의 네트워크 구축
  - 기존 풋살장 예약 페이지의 복잡한 UI, 예약 페이지와 커뮤니티 분리로 인한 소통의 불편함을 해소하기 위해 웹 사이트를 간결하고 직관적으로 변경
  - 사용자에게 편의성을 제공함과 동시에 하나의 웹 사이트에 매치기능과 커뮤니티를 함께 구현해 취미 축구인들의 네트워크를 구축
    
- 기능
  - 원하는 장소, 날짜, 시간에 맞는 매치를 조회 및 신청
  - 매치 확정 시 팀원들과의 팀 채팅방 개설로 매치 전 팀원 간의 다양한 소통 가능
  - 현재 위치 기반 가까운 제휴 풋살장 검색 가능
  - 자유게시판을 통해 자유로운 소통 및 중고게시판으로 쉽고 간편한 중고거래

## 🗓️ DEVELOPMENT PERIOD
2024.12.30 - 2025.02

## 👩🏻‍💻🧑🏻‍💻TEAM
|이름|역할|
|:------:|---------------|
|강성현 [조장]|메인페이지, 기업페이지, 결제API, 지도API, 날씨API, Scheduler 자동환불 및 예약 확정/취소 SMS발신|
|김동휘|게시글 및 댓글 신고, 팀 채팅|
|임현빈|자유게시판, 중고 게시판, 댓글 및 대댓글, SMS API 호출|
|최영수|개인 및 기업 회원가입, 로그인, 아이디/비밀번호 찾기, 문의 게시판, 소셜로그인API|
|최주경|관리자, 사용자 마이페이지, 공지사항, FAQ|

## ⚙️ DEVELOPMENT ENVIRONMENT
- Programming Language : Java 17
- Framework : Spring Boot
- Database : MySQL, Redis / MySQL Workbench, DBeaver 
- Front : HTML/CSS, JavaScript, Tymeleaf, Bootstrap
- Tooling/ DevOps : Intellij IDEA, GitHub, Docker, Postman ..
- Environment : MacOS, Window10, AWS, Jenkins
- Etc : Figma, Notion, Slack ..

> <h3>Branch strategy</h3>
- Git-Flow 전략 기반
- main, develop, feature 브랜치 운용
  - main : 배포 가능한 상태만을 관리
  - dev : 통합 브랜치 역할, 개발 단계에서 master 역할
  - feature : 기능 단위로 팀원간의 독립적인 개발환경을 보장하기 위해 사용


## 💡 FEATURE
> <h3>강성현 주요 기능</h3>
### 📍 카카오 지도 API를 활용한 위치 표시

- 사용자의 현재 위치를 Geolocation API 및 카카오 지도 API를 활용하여 마커로 표시
- 기업 위치 데이터를 Redis에 저장하여 빠르게 조회 가능하도록 최적화
- Redis에서 데이터를 불러와 지도에 기업 위치를 마커로 표시해, 사용자 주변 제휴 풋살장 위치 출력


### 💳 결제 API 연동

- 아임포트 API를 활용하여 결제 프로세스 구현
- 예약확정 및 취소 시 결제 상태를 실시간 반영하여 데이터 적합성 유지
- 오류 발생 시 트랜잭션 적용, 결제 실패 처리 및 롤백
- 결제이력 테이블을 추가하여 결제 내역을 체계적으로 관리


### 🔄 자동 환불 스케줄러 및 SMS 발송

- 매치시간에 따른 매치 최소인원 미달로 인한 자동환불 처리
- Spring Scheduler를 활용하여 정해진 시간마다 반복적으로 환불 로직 실행
- 예약 확정 및 예약 취소 시, SMS 발신 API를 연동하여 사용자에게 문자 전송


### ☁️ 날씨 API를 활용한 실시간 날씨 정보 제공

- 공공데이터 API를 활용하여 실시간 날씨 데이터(초단기실황, 초단기예보)를 가져와 메인 페이지에 출력
- API 응답 데이터를 파싱하여 사용자 친화적인 UI로 가공 및 표시


---
### [설계의 주안점 및 로직 기능 구현 과정]

### 📍 지도
- 기업의 주소 저장 및 조회
- 설계의 주안점
  1. 조회 속도 향상, 적은 쿼리 실행 비용, 캐싱
     - Redis는 메모리 기반 저장소이므로 MySQL보다 훨씬 바르게 데이터 조회가 가능할 것으로 예상
     - 기업의 주소는 자주 변경되는 데이터가 아니므로 불필요한 DB조회를 방지하고자 TTL 설정
  2. 데이터 조회의 반복성 최소화
     - 자주 조회되는 데이터는 MySQL을 거치치 않고 Redis에서 즉시 반환
     - 중복 쿼리 발생 방지로 인한 쿼리 실행 비용 절감 기대
  3. 성능 최적화
     - 여러 개의 키를 한번에 조회하여 빠른 응답 도출 필요
     - Redis 파이프라인(Pipeline) 활용, 여러개의 요청을 한번에 처리하여 네트워크 요청 횟수 감소 및 성능 최적화
     - 지도에서는 다수의 기업의 위치를 마커로 표시해야 하므로, 빠른 조회가 필요할 것으로 판단 
- 설계
  - 특정 기업의 주소를 Redis에 저장
  - Key 형식 : business:{businessIdx}:address
  - pipeline을 사용하여 여러 개의 키를 한번에 조회하여 성능 최적화
  - Redis에 데이터가 없는 경우 MyBatis를 사용해 MySQL DB에서 조회 후 Redis에 저장
    
- 캐시 전략
  - Lazy Loading (지연 로딩) + Write-Through 방식 혼합 사용
    1. Lazy Loading
        - 필요한 데이터만 DB에서 가져와 Redis에 저장하여 불필요한 로딩 방지
        - 최초 요청 시에는 DB 조회가 발생하나 이후 요청부터는 Redis에서 빠른 조회 가능
    2. Write-Through
        - 주소 데이터는 자주 변경 되지 않는 데이터로 최신 데이터를 유지하는 방식이 효율적이라 생각
        - 데이터를 저장할 때 DB뿐만 아니라 Redis에도 즉시 반영하여 캐시 일관성 유지
        - 캐시가 항상 최신 상태를 유지하므로 캐시와 DB의 불일치 가능성이 적음
          
### 💳 결제
- 설계의 주안점
  1. DB 설계
      1. 결제 테이블의 결제상태 컬럼의 필요성에 대해 고민
         - 초기 결제 테이블 설계에서는 결제 상태 컬럼을 포함하지 않았음,
           이는 결제가 사용자의 주요 정보이므로 주기적인 데이터 업데이트가 위험하다고 판단했기 때문이었으나 결제 테이블만으로 사용자의 결제 상태를 즉시 확인할 수 없는 문제가 발생

           → 이 경우 결제 상태를 확인하기 위해 결제 이력 테이블과 조인을 수행해야 하는데, 결제 이력 테이블은 1:N 관계로 구성되므로 다음과 같은 비효율성이 존재

         
              ① 조인 비용 증가

         
              ② 정렬 후 최신 결제 이력을 가져오는 추가 연산 필요
                
           → 이를 해결하기 위해 결제 테이블에 결제 상태 컬럼을 추가하는 것이 필요하다고 판단
       3. 결제 테이블 설계 개선
          - 상태가 변하지 않는 정보 (주문번호, 아임포트 결제번호)와 상태가 변하는 정보(결제상태 - 결제, 환불 등)를 기준으로 재설계
          - 재설계로 인한 기대
         
            
            ① 결제 상태를 결제 테이블에서 즉시 확인 가능
         
            
            ② 결제 테이블의 결제 상태 컬럼을 추가함으로써 환불 테이블을 따로 설계하지 않아도 되는 효과발생
         
            
            ③ 결제 정보의 키 값(주문번호, 결제번호)만 가지고 있으면 결제 이력 테이블에서 상태 변화 시점을 조회 가능
         
            

  2. 동기화 오류
     - 서버와 실제 결제시스템 간의 데이터 불일치 또는 동기화 오류로 인해, 정상적 결제처리가 서버에 반영되지 않는 경우에 대한 대처 방법에 대해 생각하게 됨
       - 데이터 불일치 대응 : 결제 상태를 재확인 하기 위한 추가 API 호출로 결제 상태를 검증
       - 트랜잭션 관리 : 결제 과정에서 오류가 발생할 경우 트랜잭션을 관리하여 데이터 무결성을 유지
       - 결제 시스템의 안정성과 데이터 무결성을 유지하는데 중점


- 설계
  - 결제 상태를 확인하는 추가 API 호출 : 결제 상태 (Paid, failed 등)을 확인
  - 결제 상태에 따른 처리 :
    - paid : 이미 결제된 상태로 결제이력 추가, 결제 정보 DB 저장
    - failed : 결제실패 상태 설정, 결제 이력 추가, 실패 메시지 반환
    - 그 외 (paid가 아닌 경우) : 정상 결제 처리 의미, 결제 상태를 paid로 설정, 결제 정보 DB저장
  - 결제 상태에 따라 중복 처리 되는 로직을 정확히 구분, 상태별 처리가 적절히 이루어 지도록 설계
  - 예외 발생 시 결제 상태를 fail로 설정하고 트랜잭션 내에서 오류를 처리하도록 하여, 데이터 무결성 유지
  - 결제 이력 테이블 : 결제 테이블과의 트랜잭션 분리
    - 결제 이력을 별도의 트랜잭션에서 처리하여 결제 처리와 결제 이력 저장 로직을 분리하여 관리
    - 결제 이력의 독립성 보장, 다른 로직의 오류가 이력 저장에 영향을 미치지 않도록 구현

- 보완점 및 추가 고려 사항
  1. 결제 상태 동기화 엔드포인트
      - 결제 시스템과 서버 간의 결제 상태가 불일치하는 경우, 현재는 사용자가 결제 버튼을 한번 더 누르면 세션스토리지에 저장된 주문번호를 통해 결제 상태를 확인하도록 구현되어있으나
        이 방식은 사용자가 직접 결제 API를 다시 호출해야만 상태가 갱신된다는 문제점이 있어 실시간 상태 갱신에 어려움이 존재
    
        → 웹훅(Webhook) 활용을 고려해 봄
          - 결제시스템에서 제공하는 웹훅을 등록하면 결제 상태 변경이 발생하는 즉시 서버가 이를 감지하고 업데이트 됨
          - 다만, 중복 호출 문제나 보안검증이 필요할 것으로 예상
          - 실무에서 웹훅 사용과 배치작업을 통한 검증 로직에 대해 어떤식으로 도입되어있는지 궁금함
  2. 결제 이력테이블의 데이터 기록의 범위 설정
      - 현재 설계에서 결제 이력 테이블을 어떻게 관리할 것인지 명확한 정책을 수립하는 부분이 필요하다고 느낌
      - 이력 테이블에서 모든 변화를 기록하는 방식이 적절하지에 대해 고민하게 됨, 결제의 상태 변경이 많아질 수록 이력의 데이터가 과도하게 증가할 가능성이 있으며,
        이에 따라 데이터 저장 비용 증가 및 조회 성능 저하 문제가 발생할 수있음
      - 현재 중요한 상태 변화만 저장하도록 제한되어있으나, 장기적으로 데이터를 효율적으로 관리하기 위해 정기적인 데이터 아카이빙 또는 삭제 정책을 도입하는 것을 고려할 필요가 있다고 느낌
      - 별도의 아카이브 테이블로 이전하는 것으로 설계할 경우, 데이터 이전의 기준, 보관 기간을 어느정도로 잡는것이 합리적인 건지 궁금함

## 📑 DEMO

|                               메인 페이지                               |                               location 페이지                               |
| :---------------------------------------------------------------------: | :---------------------------------------------------------------------: |
| ![Image](https://github.com/user-attachments/assets/c7f91ea5-b554-456c-bf26-fd2f800040e0) | ![Image](https://github.com/user-attachments/assets/6f7d904c-44f6-4ef0-aa4c-0319cf6edb29)|

|                               매치 리스트 페이지                               |                               매치 상세 페이지                               |
| :---------------------------------------------------------------------: | :---------------------------------------------------------------------: |
| ![Image](https://github.com/user-attachments/assets/05d0a5ad-26f1-4f0b-b7e0-d1d8243541ad) | ![Image](https://github.com/user-attachments/assets/19ae3f1d-c438-4712-89d4-4779daef04ef)|

|                               기업 페이지 - 메인                               |                               기업 페이지 - 매치                               |
| :---------------------------------------------------------------------: | :---------------------------------------------------------------------: |
| ![Image](https://github.com/user-attachments/assets/ce5be3fc-ca4a-4e00-8262-2cf844eef7e0) | ![Image](https://github.com/user-attachments/assets/cecc9f60-42f9-492a-91f4-67e9494eb803)|

|                               기업 페이지 - 매출                               |                               기업 페이지 - 매치 신청인원 정보                               |
| :---------------------------------------------------------------------: | :---------------------------------------------------------------------: |
| ![Image](https://github.com/user-attachments/assets/4c306a8c-82b5-410d-835c-de71d926a97f) | ![Image](https://github.com/user-attachments/assets/755bd3eb-756f-4eb6-a4bc-7c7515a0848d)|

|                               기업 페이지 - 고객                               |                               기업 페이지 - 차단 고객                               |
| :---------------------------------------------------------------------: | :---------------------------------------------------------------------: |
| ![Image](https://github.com/user-attachments/assets/02bad17e-8f0b-46ba-bf61-7432385ee2a8) | ![Image](https://github.com/user-attachments/assets/11417949-a100-47a8-86ef-d30a96bb81c1)|

|                               기업 페이지 - 차트                               |  
| :---------------------------------------------------------------------: | 
| ![Image](https://github.com/user-attachments/assets/6c18cfff-d0e7-4b0d-953e-6cbf6df664b9) | 





## 📂 PROJECT STRUCTURE
```
src
│   ├── main
│   │   ├── java
│   │   │   ├── com
│   │   │   │   └── Shoots
│   │   │   │       ├── AppConfig.java
│   │   │   │       ├── BusinessUserSecurityConfig.java
│   │   │   │       ├── MailConfig.java
│   │   │   │       ├── RefundScheduler.java
│   │   │   │       ├── RegularUserSecurityConfig.java
│   │   │   │       ├── ShootsApplication.java
│   │   │   │       ├── WebConfig.java
│   │   │   │       ├── controller
│   │   │   │       │   ├── AdminController.java
│   │   │   │       │   ├── BcblacklistController.java
│   │   │   │       │   ├── BusinessController.java
│   │   │   │       │   ├── ...
│   │   │   │       ├── domain
│   │   │   │       │   ├── BcBlacklist.java
│   │   │   │       │   ├── Board.java
│   │   │   │       │   ├── BusinessInfo.java
│   │   │   │       │   ├── ...
│   │   │   │       ├── livechat
│   │   │   │       │   ├── ChatController.java
│   │   │   │       │   ├── ..
│   │   │   │       │   └── config
│   │   │   │       ├── mybatis
│   │   │   │       │   └── mapper
│   │   │   │       │       ├── BcBlacklistMapper.java
│   │   │   │       │       ├── BusinessInfoMapper.java
│   │   │   │       │       ├── BusinessUserMapper.java
│   │   │   │       │       ├── ...
│   │   │   │       ├── redis
│   │   │   │       │   ├── RedisConfig.java
│   │   │   │       │   └── RedisService.java
│   │   │   │       ├── security
│   │   │   │       │   ├── BusinessUserDetailsService.java
│   │   │   │       │   ├── CustomAccessDeniedHandler.java
│   │   │   │       │   ├── ...
│   │   │   │       ├── service
│   │   │   │       │   ├── BcBlacklistService.java
│   │   │   │       │   ├── BcBlacklistServiceImpl.java
│   │   │   │       │   ├── BusinessInfoService.java
│   │   │   │       │   ├── ...
│   │   │   │       │   └── weather
│   │   │   │       │       └── WeatherService.java
│   │   │   │       └── task
│   │   │   │           ├── SendMail.java
│   │   │   │           └── SendMailText.java
│   │   │   └── provider
│   │   │       └── SmsProvider.java
│   │   └── resources
│   │       ├── application-deploy.properties
│   │       ├── application.properties
│   │       ├── location.xlsx
│   │       ├── mybatis
│   │       │   ├── config
│   │       │   │   └── mybatis-config.xml
│   │       │   └── mapper
│   │       │       ├── BcBlacklist.xml
│   │       │       ├── BusinessInfo.xml
│   │       │       ├── BusinessUser.xml
│   │       │       ├── ...
│   │       ├── static
│   │       │   ├── css
│   │       │   ├── img
│   │       │   ├── js
│   │       │   └── sql
│   │       └── templates
│   │           ├── admin
│   │           ├── business
│   │           ├── error
│   │           ├── faq
│   │           ├── fragments
│   │           ├── home
│   │           ├── inquiry
│   │           ├── livechat
│   │           ├── map
│   │           ├── match
│   │           ├── myPage
│   │           ├── notice
│   │           ├── post
│   │           └── report
└── test
    └── java
        └── com
            └── shoots
                └── ShootsApplicationTests.java

```
