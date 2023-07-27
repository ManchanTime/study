# AOP란?
AOP는 시간 측정을 필요로하는 핵심 기능들의 시작시간과 종료시간을 측정하여 결과값을 도출할 수 있도록 해주는 하나의 클래스이다.

+ ### AOP를 왜 사용할까?
  예를 들어 프로그램의 모든 메소드의 호출 시간을 AOP를 사용하지 않고 측정한다고 가정해보자. 예를 들어
  
  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/8232a7ab-03ba-4942-9793-667b3f2e7a31)

  위와 같은 코드를 추가하여 측정한다면 모든 메소드의 각 코드를 추가하여야하고 이것은 크나큰 손실로 이어진다. 또한 각 코드는 중복 코드로 하나하나 작성하기에는 시간 낭비가 너무 심하고 try~catch문으로
  각 핵심 로직을 감싸고 있다보니 유지보수 측면에서도 어렵다. 마지막으로 측정 코드의 변경이 생길시에는 모든 메소드의 측정 로직을 수정하여야한다.

+ ### AOP 사용

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/4d739046-974b-4ba0-a2ec-0b62940c4050)

  AOP 클래스를 생성하여 @Aspect 어노테이션으로 AOP임을 알려주고 @Component 어노테이션으로 빈을 등록해준다.
  측정 로직 메소드를 생성한 후 @Around 어노테이션으로 AOP가 적용될 범위를 정해준다.
  생각보다 간단하다?

  위에서 언급했던 문제들을 모두 해결할 수 있다. 로직 수정이 간단하고 프로그램의 핵심 로직은 건드리지않았으며 중복 코드 또한 존재하지 않는다!!

  + ### 스프링에서 AOP 원리
      
    ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/7ad1c92d-46c2-4108-a392-4bf21e3b24fd)

    AOP를 적용하지 않은 프로그램은 위와 같이 작동한다.

    ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/5306a4e3-42e8-4fed-8ccd-3956c7a47f03)

    AOP를 적용한 프로그램에서는 각 AOP 적용 객체들은 프록시라고 불리는 복제 객체를 통해 측정하고 joinPoint.proceed()를 통해서 실제 객체가 실행된다.

    ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/6c37fec3-002d-4038-a01b-aa91c29ebb2b)

    AOP가 적용된 모든 객체가 같은 방식으로 진행된다.
  ---
