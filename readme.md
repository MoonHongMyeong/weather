# 포트폴리오용 날씨 앱
포트폴리오용 날씨 어플리케이션.
[https://study-weather.koreacentral.cloudapp.azure.com](https://study-weather.koreacentral.cloudapp.azure.com)

## 개발 환경
- Java 17
- Spring Boot 3.3.7
- Gradle:8.5-jdk17
- Mybatis 3.0.3
- H2 Database 2.2.220
- Azure SQL Database
- Docker 27.3.1

## 프로젝트 구조
1. 아키텍처
   1. Azure VM
       1. Docker Container
            - 멀티 스테이지 빌드를 통해 Gradle로 빌드 후, JRE만 포함된 eclipse-temurin:17-jre-jammy 이미지로 실행되는 컨테이너
       2. Nginx
            - HTTP 트래픽을 HTTPS로 자동 전환하고, 보안 연결된 트래픽을 서비스가 실행 중인 내부 포트로 안전하게 전달
    2. Azure SQL Server
        - Azure SQL Database 접근을 위한 클라우드 레벨의 프록시 서버<br>인바운드/아웃바운드 규칙을 통한 접근 제어 관리
    3. Azure SQL Database
        - 기상 예보 정보를 저장하는 MSSQL 기반 데이터베이스
    
    > 로컬에서 빌드한 이미지를 Docker Hub Private Repository에 업로드하고 해당 이미지로 컨테이너를 실행합니다.
    배포된 애플리케이션은 VNet Private Endpoint를 통해 데이터베이스에 접근하여 데이터를 조회하고 화면에 표시합니다.

2. 패키지 구조
   1. scheduler
        - Spring Boot 내장 스케줄러를 사용하여 기상청 API(단기예보, 중기육상예보, 중기기온예보, 기상특보)를 주기적으로 호출하고 데이터베이스에 저장
   2. api
        - REST API를 통해 저장된 예보 정보를 조회하고 응답
   3. web
        - 웹 페이지 렌더링 및 서비스 제공
   4. common
        - Configuration 등 공통 모듈

### 실제 Docker 이미지 빌드 및 배포
실제로 Docker 이미지 빌드, 컨테이너 실행을 하려면  resources 폴더 내 해당 파일들이 필요합니다.

1. application-api.yml
```yml
kweather:
  api-hub:
    # 기상청 API 허브에서 발급받는 인증키
    auth-key: {your-auth-key}
  service:
    # 단기예보 조회서비스
    villageForecastService:
      # 단기예보조회
      villageForecast: https://apihub.kma.go.kr/api/typ02/openApi/VilageFcstInfoService_2.0/getVilageFcst
    # 중기예보 조회서비스
    MidForecastService:
      # 중기 육상예보
      landForecast: https://apihub.kma.go.kr/api/typ01/url/fct_afs_wl.php
      # 중기 기온예보
      tempForecast: https://apihub.kma.go.kr/api/typ01/url/fct_afs_wc.php
    # 기상특보 조회서비스
    weatherWarnService: https://apihub.kma.go.kr/api/typ01/url/wrn_met_data.php
    livingWeatherIndexService:
      UVIndex: https://apihub.kma.go.kr/api/typ02/openApi/LivingWthrIdxServiceV3/getUVIdxV3
      AirDiffusionIndex: https://apihub.kma.go.kr/api/typ02/openApi/LivingWthrIdxServiceV3/getAirDiffusionIdxV3
    # 위성영상 조회서비스
    satelliteImgInfoService: https://apihub.kma.go.kr/api/typ03/cgi/sat/nph-gk2a_img
    # 레이더영상 조회서비스
    radarImgInfoService: https://apihub.kma.go.kr/api/typ03/cgi/rdr/nph-rdr_cmp1_img
    # 낙뢰분포도 조회서비스
    lightningDistributionInfoService: https://apihub.kma.go.kr/api/typ03/cgi/lgt/nph-lgt_str_img

```
2. application-prod.yml
```yml
spring:
  jackson:
    time-zone: Asia/Seoul
  datasource:
    # local datasource
    driver-class-name: {your-driver-class-name}
    url: {your-jdbc-url}
    username: {your-username}
    password: {your-password}
    # Azure SQL Database의 경우 Connect String만 사용
    # driver-class-name: com.microsoft.sqlserver.jdbc.SQLServerDriver
    # url: {user-azure-connect-string}

logging:
  level:
    root: info
    com.portfolio.weather: info
    org.springframework: info
    org.mybatis: info
    org.mybatis.spring: info
```
이후 docker build 명령어를 통해 이미지를 빌드하고 run 명령어를 통해 컨테이너를 실행합니다.
