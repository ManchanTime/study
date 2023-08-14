# 관계형 DB AND 객체

+ 관계형 DB
  테이블을 유기적으로 설정해서 필요하다면 서로가 서로를 외래키로 연결하여 저장하는 방식

+ 객체 지향
  데이터를 객체 형태로 만들어서 사용하는 방식이다.

이 때 객체를 관계형 DB에서 관리한다면 상당히 편리할 것 이다!!(객체 중심 개발)

+ SQL 중심 개발의 문제점
  기존 SQL 중심 개발은 무한 반복, 지루한 코드의 연속이었다.(CRUD!!)
  자바 객체를 SQL로 SQL을 자바 객체로 변환 반복...
  이 때 만약 객체에 새로운 필드가 추가된다면????? -> 모든 SQL 쿼리문에 새 필드를 추가해야한다....

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/2c73b818-3da0-4a83-831d-65ac8141d712)

  그렇다면 이렇게 안쓰면 될거 아닌가?? 사실 힘들다.

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/1375b8ad-b5ae-418c-a819-7213a3c49c1e)

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/5ca5d2eb-3b2f-4491-9e5c-30ed925b117d)

  객체를 버릴 수도 관계형 데이터베이스를 버릴 수 도없다.

  이렇게 된다면 개발자 = SQL 매퍼 라고도 볼 수 있다.

+ 객체와 관계형 데이터베이스의 차이
  + 상속
    
    ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/0e8d988b-406e-48e8-8938-fd55b656a3fe)

    만약 위 그림에서 Album에 새로운 데이터를 저장한다면 먼저 객체를 분해해서 각 데이터 값을 아이템과 앨범 모두에 넣어야한다.
    Album을 조회한다면 각각 테이블에 맞는 조인 SQL을 작성하고 각 객체를 생성하고 데이터를 각각 넣어주고 어우 못한다.
    그래서 DB에 저장할 객체는 상속 관계 안쓴다. 객체 쓰는 이유가 줄어든다.

    근데 자바 컬렉션에 저장한다면 list.add(album);으로 조회한다면 Album album = list.get(albumId);, Item item = list.get(albumId);
    등 상속관계를 사용할 수 있다.
    
  + 연관관계

    ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/893f7afe-e4b8-4420-83a3-df0b00e311ce)

    객체는 참조를 사용하고 테이블은 외래 키를 사용한다. 이를 서로 맞게 모델링한다면 객체는

    ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/146237a1-7418-4ad2-9833-ddce9f19cf3f)

    방식으로 모델링하고 테이블에 저장할 때는

    ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/d5fd3276-3ec0-47b4-9bf4-32b2b314b881)

    으로 저장한다. 하지만 객체 다운 모델링은

    ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/8b13c19a-a33c-4622-b41f-bdc12bd4acc8)

    방식으로 사용하는 것이 옳다고 볼 수 있다. 하지만 이러한 방식은 테이블에 저장하기에 비효율적이고 관리하기도 힘들다.

    조회할 때

    ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/9f704807-ba55-49f3-a148-becab56bc0f5)

    같은 방식을 사용해야한다.
    이 방식을 객체 모델링, 컬렉션으로 바꿔보자
    list.add(member);
    Member member = list.get(memberId);
    Team team = member.getTeam();
    끝이다.
    뿐만 아니라 객체는 서로의 연관관계 즉 객체 그래프를 탐색할 수 있어야 한다.

    ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/b8c613e5-cb9c-431e-bbea-f5a042605b54)

    하지만 SQL은 처음 실행하는 쿼리문에 따라 탐색 범위가 결정되고 자유롭지 못하다.
    예를 들어 쿼리문이 멤버와 팀의 조인이라면 이 쿼리문에서는 오더에 대한 정보를 꺼낼 수 없다.
    또한 엔티티 신뢰 문제가 발생한다. memberDAO로 꺼내온 멤버 객체가 오더나 딜리버리에 대한 정보를 가지고 있는지 확신할 수 없다.
    이를 위해 모든 객체를 미리 로딩할 수 없다.

    ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/bdfa428a-c9ea-43e1-b3d4-1f634a036883)

    이미지처럼 사실 상 불가능하다.
        
  + 꺼낸 데이터 비교
    
    ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/00fa1195-1c84-4bb0-a69f-75b9375027a8)

    SQL 중심 개발 방식에서 두 객체는 다르다. 이유는 말 그대로 두번 꺼내서 다른 객체에 같은 정보만 담은 것이기 때문이다.

    ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/bff2cf0d-2f99-418c-91e5-fede5ed39e6d)

    자바는? 같다. 그냥 리스트에서 같은 키값의 밸류 객체를 꺼낸 것이기 때문이다.
    
+ 결론
 
  객체답게 모델링 할수록 매핑 작업만 늘어난다.
  이를 객체가 자바 컬렉션에 저장 하듯이 DB에 저장할 수 없는가?

# JPA

Java Persistence API
자바 진영의 ORM 기술 표준

+ ORM?
  Object-relational mapping
  객체는 객체대로 설계한다.
  관계형 데이터베이스는 관계형 데이터베이스 대로 설계한다.
  ORM이 중간에서 서로를 매핑해준다.
  대중적인 언어에서는 대부분 이 기술이 존재한다.

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/35de2483-bce0-4bad-889e-b9dfee1b5057)

  + 저장
    
    ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/276a0ed1-ffb3-4920-b89c-f8d1607c715b)
 
  + 조회

    ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/f917b5e8-831a-4f9b-a14a-f42bf7f79916)

+ 표준 명세
  JPA는 인터페이스의 모음이다.
  3가지 구현체중 하이버네이트를 가장 많이 쓴다.

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/e351714d-7ce1-436e-ae27-60df4e24e981)

+ Why JPA?
  SQL 중심이 아닌 객체 중심 개발이다.
  때문에 객체의 장점인 생산성, 유지보수성을 해결한다.
  패러다임 불일치 문제를 해결하고 성능을 높여준다.
  데이터 접근 추상화와 벤더 독립성이 있고 표준이 있다.

  + 생산성
    저장: jpa.persist(member)
    조회: Member member = jpa.find(memberId)
    수정: member.setName("이름")
    삭제: jpa.remove(member)

  + 유지보수
    위에서 봤던 하나 추가할 때마다 모두 수정 안해도됨. SQL은 모두 JPA가 처리하고 개발자는 필드만 추가하면됨

  + JPA와 패러다임의 불일치 해결
    + 상속
      상속 문제를 개발자가 신경안써도 됨 저장만 한다면 JPA가 알아서 상속 관계를 파악하여 insert 해줌
      조회할 때도 그냥 JPA 코드만 작성하면 JPA가 필요한만큼 찾아줌

    + 연관관계 & 객체 그래프 탐색

      ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/2ba02804-6e5b-47c1-a867-74ed8c577b28)

      간단하다.
      이 덕분에 엔티티 간의 탐색이 자유로워지고 신뢰할 수 있다.

      ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/837f873a-e592-4848-9090-5a9e8130f96d)

      동일 트랜잭션에서는 jpa를 통해 꺼낸 객체가 동일함을 보장한다.

  + 성능 최적화 기능
    + 1차 캐시와 동일성 보장
      위에서 볼 수 있드시 같은 트랜잭션 안에서는 같은 엔티티를 반환하여 약간의 조회 성능을 향상시켰고
      캐시를 통해 같은 것을 두 번 조회시 같은 것을 알 수 있다.

    + 트랜잭션을 지원하는 쓰기 지연 -INSERT
      트랜잭션 커밋 전까지 모든 Insert 쿼리를 모아서 커밋 시 한번에 보낸다.

    + ,, - UPDATE
      나중에 정리

    + 지연 로딩과 즉시 로딩
      지연 로딩: 객체가 실제 사용될 때 로딩한다. -> 필요할 때마다 쿼리문 날림

      ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/0dfd6ca6-d575-4bd2-9962-e4bd39b8ddf5)

      즉시 로딩: 연관된 객체까지 함께 로딩 -> 같이 사용될만한 객체를 따로 호출없이 쿼리문 날림

      ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/e69a7bd4-4a50-45da-884c-1bded92b21ff)

      
