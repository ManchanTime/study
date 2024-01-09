# OSIV 성능 최적화

### OSIV(Open Session In View)

기본값은 true로 설정되어 있다.
OSIV는 트랜잭션 시작처럼 최초 데이터베이스 커넥션 시점부터 API 응답이 끝날 때까지 즉, 유저에게 값이 리턴될 때까지 영속성 컨텍스트와 데이터베이스 커넥션을 유지한다.
때문에 Controller나 View Template에서 지연 로딩이 가능하다.
문제는 데이터베이스 커넥션을 계속 유지하게 되면 실시간 트래픽이 중요한 어플리케이션에서 커넥션이 부족하여 장애로 이어질 수 있다.

+ OSIV ON

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/a9eff456-d7ea-40f4-900d-e1d45036e994)

  기본 설정 값이다. 영속성 컨텍스트가 계속 살아있고 요청부터 반환까지 데이터베이스 커넥션도 살아있기 때문에 지연 로딩이 어디서든 가능하다.

+ OSIV OFF

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/895de474-3958-44c5-a30a-76f55d7b3d50)

  영속성 컨텍스트와 데이터베이스 커넥션이 트랜잭션 범위에서만 살아있기 때문에 Controller나 View에서는 지연로딩이 불가능하여 페치 조인으로 값을 트랜잭션 단계에서 뽑아내고
  사용해야 한다. 또는 모든 지연로딩을 트랜잭션 범위 내에서 해결해야 한다.

+ 커맨드와 커리 분리
  OSIV를 항상 켜두고 사용하기에는 실시간 트래픽이 많고 사용자가 많은 어플리케이션에서는 과부화가 걸릴 수 있다. 때문에 OSIV를 끈 상태로 복잡성을 관리해야할 때가 있다.
  하지만 지연로딩을 모두 트랜잭션 범위에서 해결하거나 페치조인으로 모두 땡겨오는 것은 한계가 있다. 이를 해결하기 위해서 Command와 Query를 분리하는 것이다.
  복잡한 화면을 출력하기 위해서 화면에 맞추어 성능을 최적화 하기 위한 쿼리문은 핵심 비즈니스에는 크게 영향을 미치지 않는다.
  화면 용 쿼리는 화면이 바뀔 때 마다 API 형태도 달라지는 라이프 사이클이 짧은 편이고 핵심 비즈니스는 주로 바뀔 일이 없는 라이프 사이클이 길기 때문에 서로 구별하는 편이 좋다.
  따라서 크고 복잡한 어플리케이션이라면 이 둘을 확실하게 분리하여 유지보수를 따로 하는 것이 유리하다. 

+ 예시
  OrderQueryService로 OrderApiController의 V3를 OSIV OFF를 기준으로 변경
  
  + yml 파일

    ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/e091e25b-8643-41f1-9506-7d4c6ab014aa)

  + OrderApiController

    ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/b4c6ad6e-106d-4e5d-9540-18db12ce7807)

    트랜잭션 단계에서 모든 지연로딩을 처리하기 위해서 OrderQueryService라는 새로운 서비스를 생성

  + OrderQueryService

    ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/9ded4a83-7e8a-4e04-9ee7-8a430dd6cb98)

    모든 지연로딩을 해당 클래스에서 해결한 후 반환한다.

  + Postman 실행

    ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/e157249d-be9b-4cee-be56-04f968f9eb4c)

    잘 나온다.

    + V2를 실행한다면

      ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/b592a4ea-13ef-483d-baf6-9da9e45cc6a2)

      지연로딩을 컨트롤러 단계에서 실행하니 Session이 없어 실행되지 않는다.

+ 정리
  어플리케이션이 단순하거나 사용자가 적은 즉, 데이터베이스 커넥션이 적은 곳에서는 OSIV를 활성화하여 성능을 향상 시키고
  어플리케이션이 복잡하거나 사용자가 많은 데이터베이스 커넥션이 많은 곳에서는 OSIV를 비활성화하고 트랜잭션 단계에서 모든 지연로딩을 해결하는 것이 좋다.
