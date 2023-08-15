# 시작
Maven을 사용한다.

JPA의 특징

+ 데이터베이스 방언
  JPA는 특정 데이터베이스에 종속되지 않는다.
  즉, 데이터베이스 별로 다르게 제공하는 SQL 문법과 함수에 상관없이 작동한다.

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/c73d1847-591b-4a0f-b5b2-9eaddeefa895)

  그림처럼 Dialect를 통해서 각각의 데이터베이스 방언에 맞게 작동할 수 있다.
  하이버네이트는 40가지 이상의 데이터베이스 방언을 지원한다.

# 사용하기

+ JPA 구동방식

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/30146eeb-5c78-43cf-af70-3fdb295f987d)

  EntityManagerFactory를 통해서 EntityManager를 생성하고 이를 통해서 JPA가 구동하기 때문에

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/cb519525-654f-465b-91b6-23502e580248)

  방식으로 try~catch~final을 통해서 try에서 JPA를 사용하고 catch로 문제를 잡은 후 꼭 final에서 트랜잭션과 엔티티매니저를 종료해야한다.
  이후 엔티티매니저팩토리까지 종료해야 문제가 발생하지 않는다.

  + 주의
    엔티티 매니저 팩토리는 하나만 생성해서 애플리케이션 전체에서 공유하지만 엔티티 매니저는 꼭 쓰레드 간에서 공유해서는 안되고 사용이 끝나면 버려야한다.
    이 때 모든 JPA의 데이터 변경은 트랜잭션 안에서 실행되어야 한다.

  + JPQL
    JPA를 사용한다면 엔티티 객체를 중심으로 개발할 수 있다.
    하지만 이 때 검색 쿼리에서 문제가 발생한다. 검색해서 꺼내올 때 객체가 아닌 SQL 쿼리문이 필요하기 때문이다.
    모든 DB 데이터를 객체로 변환하여 검색하는 것은 불가능하다. 때문에 필요한 데이터만 DB에서 불러오려면 결국 SQL이 필요하다.

    이 문제들을 모두 해결하기 위해서 JPQL이 존재한다.
    JPQL은 SQL을 추상화한 객체 지향 쿼리 언어이다. SQL과 문법이 유사하다.
    JPQL은 엔티티 객체를 대상으로 쿼리한다.

    테이블이 아닌 객체를 대상으로 검색할 수 있다.(select m from Member as m)
    SQL을 추상화했기 때문에 특정 데이터베이스 SQL에 의존하지 않는다.
