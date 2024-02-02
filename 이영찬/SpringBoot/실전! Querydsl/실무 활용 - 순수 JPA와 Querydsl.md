# 순수 JPA와 Querydsl을 함께 사용한 쿼리

### 순수 JPA 리포지토리와 Querydsl

![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/554c252f-66f5-4a5a-93c6-7dd2a626deab)

![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/989c7967-d8b3-42f5-b511-0f104192a89b)

JPAQueryFactory를 어플리케이션 시작 시 스프링 빈으로 등록 후 리포지토리에서 @RequiredArgsConstructor를 통해 생성자로 바로 생성

+ findAll_Querydsl()

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/b4f7ccd6-5e1c-4096-a11d-63070767fe67)

  모든 멤버를 Querydsl을 통해 조회

+ findByUsername

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/1a57f681-8599-4f89-8c0f-3192e3510302)

  특정 이름의 멤버를 Querydsl을 통해 조회

### Builder를 이용한 동적 쿼리

+ MemberSearchCondition

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/bab6c369-1cce-4e7c-b46d-0f911da9db42)

  유저 검색 조건 저장 DTO
  유저 이름, 팀 이름, 최소 나이, 최대 나이
  
+ searchByBulder

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/4314ffc3-daa6-440a-a1d9-79aca488c402)

  앞서 테스트로 생성한 Builder 이용 Querydsl과 마찬가지로 BooleanBuilder를 이용하여 조건 생성
  이 때 모든 조건에 대해 Null인지 판별하고 조건을 추가해야하기 때문에 각 조건에 대한 if문을 모두 생성해야 함

+ search

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/e59cfc22-6c5d-4717-aed7-0f2610634fdd)

  Where 절 파라미터를 이용해 조건 생성
  각 조건을 모두 메소드 형태로 생성하여 사용 함

  + 각 조건에 대한 메소드

    ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/5cc7ac29-cb58-40ff-9d95-449cbe3e6fe6)

    주어진 조건이 Null인지 판별 후 BooleanExpression 타입으로 리턴
    이 조건 메소드를 조합하여 여러 조건을 하나로 묶어 조건으로 생성할 수 있음

    + 조건 묶음

      ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/e2f0e9b6-b7a0-476e-8d27-7e9d9aedbc82)

      멤버의 나이 조건을 사이값 조건으로 만들 수 있음

  또한 해당 조건 메소드를 타입에 상관없이 재사용 가능함

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/6f99dc5b-4b72-49b1-bcb2-0a7703950a9c)

  같은 조건 메소드를 이용하여 MemberTeamDto 아닌 Member 엔티티 자체를 꺼내도 문제 없음

### 조회 API 컨트롤러 개발

+ profile 설정

  테스트 코드와 메인 코드의 init 설정을 따로 하기 위해 설정

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/8cfbfb02-9163-4dcd-b674-f365735d6072)

  메인은 local로 테스트는 test로 설정

+ InitMember

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/2ca9fb02-5fa7-41be-a369-e5d748d18cc4)

  프로파일 local로 설정

+ InitMemberService

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/3298605f-f6d2-4eed-bef4-81979073d900)

  멤버 100명 teamA or teamB 설정하여 선 저장

+ MemberController

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/6517c9a0-c636-4f97-a245-4119e582c680)

  멤버 데이터를 MemberTeamDto에 담아서 꺼내기

+ postman 결과

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/41908c3b-a179-4806-9924-64e01af58420)

  특정 조건에 따라 값이 달라짐
