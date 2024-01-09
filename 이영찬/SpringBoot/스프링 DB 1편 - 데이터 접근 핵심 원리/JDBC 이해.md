# JDBC

### JDBC 등장 이유

데이터베이스 보관 시 데이터를 저장하거나 조회할 때 DB 별로 다른 쿼리문을 편하게 사용하기 위

+ 클라이언트, 애플리케이션 서버, DB
  
  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/e9e5aeb5-4c2f-4984-a28b-e1e996bcefc1)

+ 애플리케이션 서버와 DB - 일반적인 사용법

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/d4c006b7-047c-49ef-83e5-d538fced8b55)

+ DB 변경

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/6970569b-4632-4379-acd4-ebcf848d339d)

  각각의 DB 별로 사용하는 쿼리문이 조금씩 다르기 때문에 사용 코드도 함께 바뀌어야 함

+ JDBC 표준 인터페이스

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/1cf66652-18f1-4633-8abe-2835af555adc)

  Connection, Statement, ResultSet을 이용하여 DB에 접근하고 데이터를 꺼내거나 저장한다

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/821ab4f8-d9d3-4399-bce5-9b2101152258)

  적용된 DB Driver에 따라 쿼리문을 해당 DB에 맞게 바꿔 적용한다.

### JDBC 문제

너무 복잡하고 오래된 기술이다. 사용법은 간단하지만 복잡한 코드와 조건이 있다.

+ 기존 JDBC

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/ff6ff276-35fc-4bbb-97b3-dbe9fa781d42)

+ SQL Mapper

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/2196d755-16f2-4b39-8ed9-8ffcae295fa0)

  SQL Mapper가 중간에서 JDBC 사용을 돕는다.

+ ORM JPA

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/de780cda-015b-456e-8c4f-c454e342fc3c)

  객체 자체를 관계형 데이터베이스 테이블과 매핑해주는 기술이다.

### JDBC 사용

+ DB 연결

  + ConnectionConst

    ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/12ce001c-c560-497b-a151-dcf282d4fa15)

    사용할 DB의 정보를 글로벌 값으로 저장한다.

  + DBConnectionUtil

    ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/c68cb596-39c6-4954-a4c4-959d7d007e79)

    해당 DB와 연결하고 연결 값을 반환한다.
    
+ JDBC 개발 - 등록

  Member를 생성하고 DB에 저장한다.

  + Member

    ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/ac5a0673-a7dd-44c7-b16f-475a4050a743)

  + MemberRepositoryV0

    ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/994e0d22-1ac1-4cf1-b002-811c61dd795b)

    DBConnectionUtil을 통해 받은 Connection 생성
    sql을 직접 생성하여 Connection에 sql을 넣고 PreparedStatement에 저장
    해당 pstmt에 쿼리문에 넣을 파라미터를 지정하고 실행

  + close(con, pstmt, rs)

    ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/8aa4373e-43db-4c1a-9d58-0ebc7b86da0e)

    사용한 모든 DB 커넥션 리소스를 닫아야 누수가 발생하지 않음
    생성한 순서의 반대로 닫아야 함
    이 때 null인지 판단한 후 리소스에 대한 try~catch를 각각 적용해야 사용한 모든 값을 안전하게 닫을 수 있음
    이렇게 않하면 해당 try~catch에서 예외 발생 시 다른 리소스가 닫히지 않을 가능성이 존재함
    
+ JDBC 개발 - 조회

  memberId를 통해 해당 Member를 조회

  + MemberRepositoryV0

    ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/327d49e5-ed7f-4364-97f0-c9f2d51ed12f)

    위와 마찬가지로 con, pstmt를 통해 DB에 접근 후 값 조회
    ResultSet을 통해서 받아온 값을 사용할 수 있음
    이 때 cursor라는 것이 존재하는데 가져온 값을 하나씩 지정함 -> rs.next()를 사용해야 첫번째 값부터 조회할 수 있

+ JDBC 개발 - 수정, 삭제

  해당 Member를 삭제하거나 수정함

  + MemberRepositoryV0

    ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/084179ee-c584-4fe9-9143-b5a0e3c33f45)

    수정할 Member의 아이디와 수정할 값을 받아와서 쿼리문 생성 후 pstmt에 담아 실행함
    위 등록 코드와 비슷함

    ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/d6bea5ab-5844-46f0-bae8-e5d6885f167f)

    삭제도 위와 비슷함

  + 정리

    잘보면 생각보다 간단하고 할만해 보임
    하지만 중복코드가 지나치게 많고 try~catch 문이 너무 많아 예외처리가 힘듬
    더 좋은게 나옴
