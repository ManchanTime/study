# 컬렉션 조회 최적화 part 2

### V4-JPA에서 DTO 직접 조회

+ OrderApiController

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/daf9293c-a3dd-4e34-a64f-bd7a5780673e)

+ OrderQueryDto

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/16cd4b84-7b04-4259-a032-349cfded83f2)

+ OrderItemQueryDto

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/004618ec-8117-4d4a-8c4c-f14c7705ced6)

+ findOrderQueryDtos()

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/b6c31e80-fbcc-480c-9a58-db8c90349f3b)

+ findOrders()

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/e7594d89-ca8b-4f5b-b3c2-90c3dc2c2943)

  Order에 대한 값을 먼저 찾는다. 이 때 ToOne 관계의 값은 페치 조인으로 묶어서 꺼낸다.

+ findOrderItems(Long orderId)

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/7e445358-cb7e-4a1f-b1ff-acd48b7ece7e)

  해당 orderId에 대한 OrderItem과 Item의 값을 꺼낸다.

+ Postman 실행

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/58428c93-36d8-43cb-b6eb-ff27db965289)

  DTO에 존재하는 값만 잘 나온다.

+ 쿼리문

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/324b4736-d14b-4361-8a9b-a1087203002b)

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/fc5f7fa9-998c-443c-a9d6-72976d1cb82e)

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/d9199c26-edca-49ef-a251-834e1fd98bed)

  필요한 값에 대해서만 조회한다. 이 때 ToMany 관계는 findOrderItems의 쿼리문으로 따로 조회한다.

+ 특징
  DTO에 맞는 값들만 꺼내올 수 있다. 하지만 쿼리문을 여러개 작성해야할 수 있다.

### V5-JPA에서 DTO로 직접 조회, 컬렉션 조회 최적화

+ OrderApiController

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/3909c7d6-e299-4229-86bb-d88c45ec1d84)

+ findAllByDto_optimization()

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/584e06a1-6223-4ce2-9d19-e35e976c1cf6)

+ toOrderIds(List<OrderQueryDto> orders)

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/64b5d934-3fb5-4f23-8aa5-802ceedcfe25)

  OrderQueryDto 리스트에서 orderId만 뽑아낸다.
  
+ findOrderItemMap(toOrderIds(orders))

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/bf38ac8c-e4c5-4cbe-a633-7c126f4750f4)

  위에서 받은 orderId 리스트를 기준으로 해당 id에 대한 DTO에 맞는 값들을 뽑아내어 OrderItemQueryDto에 담는다.
  이후 해당 값들을 담은 리스트를 orderId가 키값인 Map으로 바꾼다. 이후 findAllByDto_optimization에서 forEach를 통해 Map의 value 값을 key값(orderId)에 맞는 DTO의
  Itemlist에 채운다.

+ Postman 실행

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/a7ca67c7-0ef8-42ba-833a-6b7c68bdc46b)

+ 쿼리문

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/d5fc3cf3-ace8-4d58-a4fe-e4ba8625a606)

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/ca9bdada-9bd9-4f5a-bd15-c1374651e105)

+ 특징
  Order에 대한 쿼리 한번, 컬렉션에 대한 쿼리 한번으로 총 2번의 쿼리로 모든 값을 꺼낼 수 있다.
  V4와 ToOne까지는 페치 조인으로 이후 컬렉션은 지연로딩으로 찾는 방식은 비슷하지만 미리 찾은 orderId로 ToMany 관계인 OrderItem을 한꺼번에 조회하여
  Map으로 만드는 차이가 있다.
  성능은 V4보다 좋다. 하지만 코드가 복잡해진다.

### V6-JPA에서 DTO로 직접 조회, 플렛 데이터 최적화

+ OrderApiController

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/57280823-8043-4cc5-b01c-ca8aa7d2f4e6)

  findAllByDto_flat으로 받아온 OrderFlatDto 리스트를 orderId 기준으로 grouping한다. 이를 다시 매핑하여 값을 뽑아내고 OrderQueryDto로 변환하여 리턴한다.
  
+ OrderFlatDto

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/d0db2ae2-2a93-4d4f-90e3-f5078ca13e6d)

+ findAllByDto_flat()

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/0253bdf6-e7d9-4cc5-82d5-7421230aae6b)

+ Postman 실행

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/3b84ee2d-025f-4d05-a27f-ddcb3b9a3b88)

+ 쿼리문

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/80a9bd7c-2c71-453a-b216-8f4c4ec7b8fa)

+ 특징
  쿼리문 하나로 값을 모두 조회할 수 있다. 하지만 DB에서 어플리케이션에 전달하는 데이터의 중복 데이터가 추가되기 때문에 V5보다 느릴 수 있다.
  페이징이 불가능하다.

### API 개발 고급 정리

+ 엔티티 조회
  + 엔티티 조회 후 그대로 반환 -> 사용x
  + 엔티티로 조회 후 DTO에 담아서 반환 -> 추천
  + 페치 조인으로 쿼리 최적화 -> ToOne관계
  + 컬렉션
    + 페치 조인 시 페이징 불가능
    + 배치 사이즈를 사용하여 최적화, 페이징까지

+ DTO 직접 조회
  + JPA에서 DTO 직접 조회
  + 컬렉션 조회 최적화 -> Map 사용
  + 플렛 데이터 최적화 -> Join 결과를 그대로 조회 후 어플리케이션에 맞게 변환

+ 권장 순서
  + 엔티티 조회 방식으로 우선 접근
    + 페치조인으로 쿼리 수 최적화
    + 컬렉션
      + 페이징 필요 시 -> 배치 사이즈
      + 페이징 필요 없을 시 -> 페치 조인 사용

  + 엔티티 접근
    + 배치 사이즈를 이용하여 코드를 거의 수정하지 않고 옵션 변경만으로 다양한 성능 최적화가 가능하다.

  + DTO 접근
    + DTO에 따라 성능 최적화에 많은 코드를 생성하고 변경하여야 한다.
