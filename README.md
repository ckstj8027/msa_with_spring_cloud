Spring Cloud 기반 MSA 프로젝트
이 프로젝트는 Spring Cloud를 기반으로 구성된 마이크로서비스 아키텍처(MSA) 공부입니다. 
서비스 간의 독립성과 확장성 확보를 목표로 하며, 실시간 설정 반영, 비동기 메시지 처리, 서비스 디스커버리 및 API 게이트웨이와 같은 MSA 핵심 요소를 포함하고 있습니다.




config-server     중앙 설정 서버
eureka-server     서비스 디스커버리 서버
gateway-server    API 게이트웨이
order-service     주문 서비스
category-service  카테고리 서비스
user-service      사용자 서비스


기능 및 아키텍처 설명
1. Config Server (설정 서버)
 -Spring Cloud Config를 이용하여 모든 마이크로서비스의 설정 정보를 중앙에서 관리합니다.
 -Git에 저장된 application.yml을 각 서비스가 동적으로 참조합니다.

 -Actuator 및 Spring Cloud Bus 연동
  각 마이크로서비스는 spring-boot-actuator를 통해 헬스 체크 및 메트릭 노출 기능을 지원합니다.
  설정 변경 시, Spring Cloud Bus를 이용한 POST /actuator/busrefresh 호출로 모든 서비스의 설정을 실시간으로 동기화합니다.

2. Eureka Server (서비스 디스커버리)
 -각 서비스는 Eureka Client로 등록되며, Eureka Server를 통해 서비스 위치를 자동으로 탐색합니다.
  이를 통해 동적 라우팅 및 부하 분산이 가능합니다.

3. Gateway Server (API 게이트웨이)
 -Spring Cloud Gateway를 사용하여 모든 요청을 단일 진입점에서 관리합니다.
  Eureka와 연동하여 서비스 이름 기반의 라우팅을 지원합니다.
  필터 기능으로 인증, 로깅 등의 공통 처리를 수행합니다.

4. Kafka를 이용한 비동기 메시징 처리
 -주문(order)을 생성하면, 해당 정보를 Kafka Topic에 비동기적으로 전송합니다.
 Kafka Connect Sink Connector를 이용해 외부 시스템(DB, ElasticSearch 등)에 연동 가능.
 이를 통해 서비스 간 결합도를 낮추고, 확장성과 신뢰성을 강화합니다.

5. 마이크로서비스 구성
 -각 서비스는 독립적인 Spring Boot 애플리케이션으로 구성됩니다.
 order-service, category-service, user-service는 각자의 DB 및 도메인을 가지고 운영됩니다.
 서비스 간 통신은 REST 기반 또는 Kafka 메시지로 처리됩니다.
