# 조회 성능 최적화
데이터베이스에서 데이터를 꺼낼 때 하나의 테이블에서만 조회하는 경우는 드물다.
때문에 여러 테이블을 함께 조회할 때 성능을 최적화 해야한다.
ManyToOne, OneToOne 경우에 대해 조회

### 간단 주문 조회

##### v1 엔티티를 직접 노출

+ OrderSimpleApiController

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/47b84b89-a513-451b-afe9-ca7ca4f8d84d)

  말 그대로 주문에 대한 모든 정보를 엔티티를 통해서 꺼낸다.

+ Postman 실행

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/d9037754-44a0-47e4-8478-95897330f77a)

  에러 출력
  Order -> Member -> Order가 무한 반복되기 때문이다. 이를 해결하기 위해서 양방향 매핑중 List쪽에 @JsonIgnore를 통해 조회를 막아야한다.
  또한 해당 Bean을 통해서 프록시 객체 타입과 엔티티 타입이 달라 생기는 문제를 방지해야한다.

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/3603bf23-2048-4a8a-9f4b-c65d02887a8f)

+ 특징
  어지간하면 엔티티를 API에 노출시키는 것은 좋지 않다. 따라서 DTO로 변환하는 것이 무조건 좋다.
  또한 EAGER로 설정해서는 절대로 안된다. 연관관계가 필요없는 경우에도 모든 값을 바로 꺼내오기 때문에 성능 문제가 발생할 수 있다.
  EAGER 대신 fetch join을 이용한다.

##### v2 엔티티를 DTO로 반환

+ OrderSimpleApiController

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/5a07452b-fd97-49d9-a63a-88cd8affd2c5)

+ DTO

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/54ba713c-b0ee-4659-976a-39af13ff3fc1)

+ Postman 실행

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/09b25382-2a91-40d1-8026-c873ea0e82d6)

+ 쿼리문

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/6705637f-86b2-438b-aa3f-37e9a0777ccb)

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/3ac939f9-1803-4d59-af81-ba97884eb4e1)

+ 특징
  값을 엔티티 그대로 반환하는 것이 아닌 SimpleOrderDto 형식으로 맞춰서 반환하고 값도 원하는대로 잘 꺼낼 수 있다.
  문제는 쿼리문에서 발생한다. Order에 대한 값을 꺼내면서 Member와 Delivery에 관련된 값을 한번에 가져오는 것이 아니라 Order에 대한 값을 꺼내고 거기에 대한
  Id를 바탕으로 Member, Delivery에 따로따로 조회하여 값을 꺼낸다. 또한 Order 값도 하나씩 조회하여 N+1 문제가 발생하는 것을 볼 수 있다.
  이 또한 fetch join을 통해 해결할 수 있다.

##### v3 엔티티를 DTO로 반환 - 페치 조인 최적화

+ orderSimpleApiController

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/77346596-9365-4340-b5de-65659c42a1ce)

+ OrderRepository - findAllWithMemberDelivery()

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/8e156e2a-e5c9-4c68-9a79-048373787c22)

+ Postman 실행

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/d4d59762-3529-4616-81bc-73a38cb53fbb)

+ 쿼리문

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/39b0eea4-406d-4279-8c6c-ca03962018ae)

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/e192dbc6-082f-45cf-b63d-b389c2ce3328)

+ 특징
  출력 값은 v2와 똑같다. 가장 큰 차이는 쿼리문에서 나타난다. 이전 Order를 조회하고 조회한 값을 토대로 각 Member, Delivery를 하나씩 조회하던 것과는 다르게
  값을 inner join을 통해서 한번에 꺼내와 하나의 쿼리문으로 해결하는 것을 볼 수 있다.
  이를 통해 성능 문제를 해결할 수 있다.
  하지만 조회하는 모든 값을 꺼내오는 문제가 있다. 원하는 값만을 뽑아내기 위해서 JPA에서 바로 DTO로 조회하는 방법이 있다.
  
##### v4 JPA에서 DTO로 바로 조회

+ orderSimpleController

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/6b90417d-a4a5-4e3f-98b4-a41cda7e11be)

+ Dto

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/5f3be291-efd1-49b7-a5f0-bc9a036e51d3)

+ OrderSimpleQueryRepository - findOrderDto()

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/dc76cd07-cd2c-46c2-84a1-23202ff3fb29)

+ Postman 실행

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/6947c2fc-67e3-498f-8407-f65c2df9058f)

+ 쿼리문

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/92ecdd1d-c5c3-4c2c-97ca-a77ef85d651f)

+ 특징
  결과는 당연히 이전과 같다. 하지만 쿼리문에서 DTO에 필요한 값들만 꺼내오는 것을 볼 수 있다.
  이 방법과 v3 모두 각각 장단점이 있다.

  v3
  + 장점
    Repository에서 기본적인 쿼리문으로 해결할 수 있다는 점이다. 특정 DTO에 치우치지않고 값을 꺼내오기 때문에 DTO가 변경되거나 삭제되어도 문제가없다.

  + 단점
    사용하는 값에 비해서 꺼내오는 값이 많을수록 성능 저하 문제가 발생한다.

  v4
  + 장점
    DTO에 맞게 필요한 값들만 꺼낼 수 있어서 성능이 v3에 비해 좋다.

  + 단점
    DTO에 너무 치중되어 있어 DTO가 변경되거나 삭제될 때 유지보수가 힘들다. 또한 특정 DTO에만 맞게 값을 꺼내서 코드 유연성이 떨어진다.
    이를 관리하기 위해서 Repository를 따로 생성하여 관리하는 편이 좋다.

### 결론

쿼리 방식 선택 권장 순서
1. 우선 엔티티를 DTO로 변환하는 방법을 선택한다.
2. 필요하다면 fetch join을 통해 성능을 최적화한다. -> 여기서 대부분 해결된다.
3. DTO로 직접 조회한다.
4. JPA가 제공하는 네이티브 SQL이나 스프링 JDBC, JDBC Template을 사용하여 SQL을 직접 생성한다.
