# 스프링 데이터 JPA와 Querydsl을 함께 사용

### 스프링 데이터 JPA 적용

+ MemberRepository

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/760cd926-ae09-480c-8e39-e44f57c265d7)

+ MemberRepositoryCustom

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/d47de18f-24fb-4c2c-baeb-83026e88336f)

+ MemberRepositoryImpl

  + search

    ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/b2b3b129-9fa2-47b6-af5c-482327f5d3ef)

    기본 조건 메소드는 기본 JPA에서 사용했던 것과 동일

### 페이징

+ searchPageSimple

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/312808d5-e43e-43c7-a045-9cc762b096b0)

  간단한 페이징이다. 조인이 복잡하지 않을 때 꺼낸 값들의 수를 반환한다.
  조인이 복잡할 경우 카운트 쿼리문이 굳이 모든 조인을 진행 후 계산할 필요가 없어 간단한 쿼리문을 고민해야 한다.

+ searchPageComplex

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/0d824c77-6cdc-43ff-b82b-40d90572ec7f)

  복잡한 값을 계산할 경우 카운트 쿼리를 리팩토링 해준다. 이 때 fetchCount를 나중에 적용하여 리턴하는 PageableExecutionUtils.getPage
  객체를 사용하여 조회한 값들의 수가 PageSize보다 적거나 총 데이터 수보다 조회할 값의 수가 많을 때 카운트 쿼리없이 컨텐츠 수로 반환해준다.

+ 컨트롤러

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/975085bc-fc39-41e0-b519-4bca1fa37a6e)

  특이한건 없다.

+ postman

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/55e3fbde-7ff7-4f55-8b06-786019b60170)

  pageNumber가 0이고 pageSize가 3일 때 값을 3개 가져오고 토탈 수, pageNumber 등 각종 디테일한 값을 반환한다
