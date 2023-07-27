# 스프링 DB 접근 기술은 크게 3가지

+ ### 구현 클래스 변경
  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/a636ed1f-1d0e-43c0-b4fd-e1e008b9d791)

  스프링 빈과 의존관계에서 컴포넌트 스캔과 직접 작성하는 것 둘중에서 변경이 이루어질 수 있다면 직접 작성하는 것이 유리하다.
  -> 이 때문에 SpringConfig를 사용하여 레포지토리를 Memory에서 Jdbc or JdbcTemplate, JPA로 간단하게 변경할 수 있다.
  
  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/516558b5-24c7-4661-b873-4f3c55725317)


+ ### 순수 Jdbc
  순수 Jdbc는 말그대로 Jdbc의 모든 쿼리문을 직접 작성해주는 것이다.

  ex) 회원가입

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/3f2d525c-16ab-4c08-9124-51ff81aa2396)

  이는 지나치게 많은 try~catch문과 각 DB 접근 connetion을 매번 종료해주어야한다는 단점이 있다. 이 때문에 현재는 사용되지않고 있다.
  
+ ### JdbcTemplate
  순수 Jdbc에서 쿼리문을 키, 밸류 값으로 자동으로 만들어 준다.

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/1d604fda-508f-490f-a1c2-bdd7740a4eb3)

  필요한 객체를 생성하고 객체에 result값을 받아서 객체를 완성한 값을 RowMapper<>값으로 리턴해준다.

  ex) 회원가입

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/184851d0-f68c-4bee-a42b-28972518d0ac)

  ex) 아이디로 찾기

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/f7eeefc0-fddd-483d-b904-dc866815abe2)

  커넥션을 직접 작성하여 여닫는 것이 아닌 작성한 쿼리문에 따라 자동으로 stream을 여닫아 실행해준다. 쿼리문에 대한 리턴값을 리스트로 받아서 필요한 값을 뽑아 사용할 수 있다. but query문은 직접 작성해야한다.



+ ### JPA
  JPA는 JdbcTemplate의 기능을 넘어서 반복 코드와 기본적인 SQL도 작성해준다.
  이를 통해 SQL과 데이터 중심 설계에서 객체 중심의 설계를 가능하게 해준다.

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/0863385f-6c1d-4c4a-9803-1429818c982f)

  상당히 간단하다!! EntityManager를 통해 자동으로 데이터베이스에 접근을 해결해주고 내장된 메소드로 데이터에 접근, 추출, 수정할 수 있다. 이 때
  데이터의 수정이 이루어질 수 있는 서비스는 @Transactional 어노테이션을 추가하여 트랜잭션을 포함시켜주어야한다.

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/0b632b45-e39c-48d1-8e15-66df8279416e)

  Query문 자체도 (select m from Member m where m.name = :name, Member.class).setParameter("name", name)을 사용하여 받은 값을 매핑시켜줄 필요없이 객체 자체를 매핑하여 추출하고 사용할 수 있다.

  JPA는 config에서 EntityManager를 사용하기 때문에 생성자를 생성하여 사용해야한다.
  
  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/6b05a86d-9f7d-45da-908f-0f03b6ff03af)

+ ### 스프링 데이터 JPA
  스프링 부트와 JPA만을 사용해도 객체 중심의 설계를 통해 생산성을 크게 증가시키고 코드양을 확연히 줄일 수 있다. 여기서 스프링 데이터 JPA를 사용한다면 레포지토리 구현 클래스없 인터페이스만으로도 개발을 완료할 수 있다.

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/36862e36-0d6b-433a-a3de-7243bd1df420)

  상당히 간단하다. 원래 있던 MemberRepository를 extends하고 추가로 JpaRepository<객체, Long(ID)>를 extends하면 끝이다.

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/17309e4a-68cd-4345-9407-0bbc91d28789)

  위 이미지에서 볼 수 있드시 상당한 양의 메소드를 제공하기 때문에 추가할 메소드 양을 많이 줄일 수 있다. 또한 findBy~() 메소드를 제공하여 뒤에 이름만 붙인다 조회 기능을 바로 제공해준다. 또한 페이징 기능까지 제공하여 코드를 줄여준다.

  + ##### 주의: 스프링 데이터 JPA는 JPA를 돕기위한 도구일 뿐이다. 따라서 스프링 데이터 JPA만이 아닌 JPA의 선행 학습 또한 필수적이다!! 또한 기본적인 기능을 많이 제공하지만 복잡한 Query문은 QuerySdl 라이브러리를 사용하여 해결하거 JdbcTemplate나 JPA에서 제공하는 네이티브 SQL을 이용하여 해결한다.

+ ### 추신: 스프링 통합 테스트
  스프링 컨테이너와 DB까지 연결하여 통합 테스트를 진행하는 것이다. 즉, 단순 유닛 테스트가 아닌 실제로 데이터베이스에 잘 들어가는지 확인하는 것!!
  + ##### @SpringBootTest
    테스트하고자하는 코드를 스프링 컨테이너와 함께 테스트하는 것을 알려주는 어노테이션
  + ##### @Transactional
    스프링 컨테이너를 이용하여 DB에 실제로 삽입, 삭제하는 기능을 테스트하는 것이기 때문에 각 테스트 마다 DB의 값을 변경할 수 있음

    -> 이 때문에 각 테스트가 성공적임에도 불구하고 DB의 값에 따라 실패할 가능성이 있음. 비록 테스트 DB를 이용하여 테스트한다고 해도 각 테스트 케이스끼리 간섭가능

    -> 이를 방지하기 위해서 원래 사용했던 @BeforeEach @AfterEach 사용가능 but @Transactional을 이용한다면 각 테스트 시작 전에 트랜잭션을 시작하고 완료 후 롤백되기 때문에 DB에 데이터 변경이 이루어지지 않음
