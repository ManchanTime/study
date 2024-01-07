# 컬렉션 조회 최적화

ManyToOne, OneToOne에서는 함께 조회할 대상이 하나이기 때문에 쿼리 상 문제가 발생하지 않았음
따라서 fetch join으로 대부분의 문제가 해결됨
하지만 OneToMany, ManyToMany에서는 조회할 대상이 여러개이기 때문에 fetch join만으로는 해결되지 않는다.

### V1-엔티티 직접 호출

+ OrderApiController

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/d820600e-3980-41d7-81d4-1cf9824be2b5)

  기존에 조회하던 정보에서 OrderItem과 Item에 대한 정보가 필요하다.
  다른 메소드 추가없이 받아온 Order 값에서 반복문을 통해서 Member, Delivery에 대한 값을 다시 추출하고
  같은 방식으로 OrderItem을 List로 받아서 Item 정보도 꺼내온다.

+ 쿼리문
  @JasonIgnore를 사용하지 않으면 무한루프에 걸린다.

+ 특징
  엔티티를 직접 노출하기 때문에 사용하지 않는다.

### V2-엔티티를 DTO로 변환

+ OrderApiController

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/9b96d28f-4f9c-4297-b8e8-e6f0713ac1aa)

+ OrderDto

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/2ff27865-5a9a-487d-8aab-aab2e31c9ed7)

+ Postman 실행

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/7cc17388-76d7-4351-9e16-55e709e1e857)

  OrderItem과 Item에 대한 정보가 잘 나온다.

+ 쿼리문

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/b0f10d45-40e1-4836-afa8-17bb001ded2d)

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/7c6e08c1-5064-4c19-9b73-13cf0f9efcf9)

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/78f7f36b-2ff5-48df-b6e1-ee35b1cd0bed)

  Order부터 Item까지 모든 값에 대한 쿼리문이 나간다.

+ 특징
  원하는 값은 잘 나오지만 쿼리문을 보면 Order, Member, Delivery....Item까지 모든 엔티티에 대한 쿼리문이 나가는 것을 볼 수 있다.
  특히, OrderItem과 Order가 OneToMany 관계로 하나의 Order에 두 개의 OrderItem이 존재하여 쿼리문이 두번나가고 해당 OrderItem에 대한 Item의
  쿼리문도 나가는 것으로 N+1 문제가 발생한다.

### V3-엔티티를 DTO로 변환-페치 조인 최적화

+ OrderApiController

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/8beb05d3-bcdd-4d66-9351-08b7d60c5bea)

+ findAllWithItem()

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/fdeb2cfe-525c-4fcb-929a-024464143daa)

  fetch join을 이용하여 Order, Member, Delivery, OrderItem, Item을 묶어서 가져온다.

+ Postman 실행

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/6cadfc2e-fccf-404b-8477-2a3d206ad77e)

+ 쿼리문

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/e67e6e53-df33-4dce-b5f3-754ad31341a3)

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/ed6a214d-3f53-4f01-83a1-a44bb35a4a8d)

+ 특징
  모든 값을 하나의 쿼리문으로 묶어서 가져온다.
  이 때 hibernate 버전에 의해서 distinct가 자동으로 적용되어 중복값은 나오지 않는다.
  하지만 이 쿼리문을 db에 직접 입력해보면

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/80f8d9ce-631b-480a-b86e-6c9bf7e7ee9c)

  + Order 기준으로 값이 4개가 나온다. 이유는 db입장에서 하나라도 값이 다르다면 다른 row로 보기 때문이다. 이 때문에 페이징이 불가능하다.
  + 컬렉션 페치 조인은 1개만 사용할 수 있다. 컬렉션 둘 이상에 페치 조인은 사용할 수 없다.

### V3.1-엔티티를 DTO로 변환-페이징과 한계 돌파

페이징이 필요하지만 페치 조인을 사용하고 싶다.

+ OrderApiController

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/d0bc6934-3136-49c0-b1a7-c66829f7b46b)

  기존 페이징 방식대로 offset과 limit을 파라미터로 제공한다.

+ findAllWithMemberDelivery(offset, limit)

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/706984c6-c23a-4084-a4c5-bfe8539f2899)

  쿼리문도 앞처럼 기존 페이징 방식대로 작성한다.

+ Postman 실행

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/fe751920-fc21-49a3-8f18-b804c3a363b6)

  offset이 0이고 limit이 1일 때 처음 값 하나만 잘 나온다.

+ 쿼리문

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/de421c70-130f-4a92-9c48-9dd020255e96)

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/c316c9da-9f6d-492b-b9ff-112e198cfca1)

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/62c5e623-bfd7-4d42-a04b-66f06d03242a)

  앞선 OneToOne, ManyToOne 관계를 모두 fetch join을 통해 값을 가져온다. -> row 개수의 영향을 미치지 않기 때문에 페이징에 영향을 주지 않는다.
  컬렉션 값은 지연 로딩으로 조회한다.
  @BatchSize, 또는 yml파일에 hibernate.default_batch_fetch_size를 사용하여 성능을 최적화한다.

+ 특징
  + 기존 ToOne 관계를 가져오는 쿼리문은 같지만 이후 컬렉션을 가져오는 쿼리가 엔티티 당 하나로 줄었다.
  + 페치 조인보다 DB 데이터 전송량이 최적화 된다. DB에서 값이 겹치게 나오지 않는다.
  + 페이징이 가능하다.
  + 배치 사이즈는 100~1000 사이를 주로 사용한다. 데이터베이스나 어플리케이션이 버틸 수 있는 정도로 하는 것이 적당하다.
  이 방법을 주로 사용한다.
