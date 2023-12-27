# 스프링 데이터 JPA 분석

### 스프링 데이터 JPA 구현체 분석

스프링 데이터 JPA의 구현체인 SimpleJpaRepository를 확인해보자.

![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/795d1b20-cd19-4758-b923-840a546c406b)

@Transactional의 속성이 readOnly = true로 설정되어있다.

+ save

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/de6d858a-1486-4673-ad83-829ad1c38b0f)

  db의 값이 저장되는 경우에는 @Transactional로 되어있다. 이 때 주의할 점은 엔티티가 새로운 값이라면 persist로 저장하고 새로운 값이 아니라면 merge로 병합해버린다.
  merge의 경우에는 select 구문이 먼저 나가고 값이 있는지 확인 후 수정이기 때문에 성능 저하가 발생할 수 있다.
  물론 @GeneratedValue를 이용하여 Long값의 아이디를 자동 생성할 경우에는 save()호출 시점에 식별자가 생성되지 않아서 새로운 엔티티로 감지하고 정상 동작한다.
  하지만 Id값을 @GeneratedValue가 아닌 @Id만 사용하고 String 값 등 다른 커스텀 값을 사용하게 되면 이미 식별자가 생성되어 있기 때문에 merge가 작동한다.
  이를 해결하는 방법으로 Persistable이 있다.
  
  + 예시로 Item 엔티티를 사용해보자
 
    + Persistable 사용 x

      ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/5c7a6ac4-1862-4cf0-8a4b-a4a7488c4bc6)

      @GeneratedValue를 사용하지 않고 String 값으로 id를 생성한다.

      + 테스트 코드

        ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/46d00b8c-1421-4c27-9719-b53ecd6564aa)

        Id가 ""인 Item 객체를 생성하고 넣는다.

      + 결과

        ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/d6b2e0fe-49ff-4dbf-857f-0b78bb5461ab)

        select 구문이 한번 나간 후 값이 없는 것을 확인하고 insert하는 모습을 볼 수 있다.
        
    + Persistable 사용 시

      ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/6b66206c-7280-421a-940a-856a54033d45)
  
      Persistable 인터페이스를 상속받고 해당 인터페이스의 구현체로 isNew, getId 메소드를 상속받아 사용한다. 이 때 isNew 메소드에 생성 날짜를 기준으로 새로운 것인지 원래 있던 것인지 구별한다.
      생성 시간이 없다면 새로운 값이기 때문이다.
  
      + 테스트 코드
  
        ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/22a7888c-faad-48ab-b16c-5163b78a02c2)
  
        아이디가 ""인 단순한 값을 넣는 테스트 코드다.
  
      + 결과
  
        ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/3d7c4f2e-05ce-4eed-907d-4f4176c09308)
  
        select 구문 없이 바로 insert 된다.  
