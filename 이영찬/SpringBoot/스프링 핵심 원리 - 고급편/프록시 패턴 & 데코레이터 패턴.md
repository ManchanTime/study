# 비즈니스 로직 수정없이 로그 기능 추가

+ 기존 방식 문제점
  + 콜백 패턴, 템플릿 메서드 패턴 모두 기존 비즈니스 로직 코드 수정 불가피

+ 요구사항 추가
  + 비즈니스 로직 코드 수정x 로그 추적기 적용
  + 특정 로직은 로그 출력x

### 프록시 패턴 & 데코레이터 패턴

+ 프록시
  ![image](https://github.com/ManchanTime/study/assets/127479677/de41ce55-47fe-405b-aa37-2bedbdaa71a3)

  + 프록시란?

    + 클라이언트가 요청한 결과를 서버에 직접 요청하는 것이 아닌 중간 프록시 객체를 거치고 요청하는 방식을 말한다.
      
  + 특징
    
    + 클라이언트가 요청할 때 클라이언트는 요청이 프록시로 가는지 서버로 직접 가는지 알 수 없다. 즉 서버와 프록시는 같은 인터페이스를 사용한다.

      ![image](https://github.com/ManchanTime/study/assets/127479677/f8f817e7-f72d-47b8-b6c1-53fcaeee9ac9)

     + 즉 이 방식을 이용하여 서버 객체를 프록시 객체로 변경하고 요청을 받아도 핵심 클라이언트 코드는 변경되지 않는다.
   
     + 중간 프록시 객체를 넣어 Client -> Proxy -> Server 런타임 순서를 변경해도 클라이언트는 해당 사실을 알 수 없고 DI를 사용하여 클라이언트 코드 변경없이 유연하게 프록시를 주입할 수 있다.

    + 주요 기능

      + 접근 제어

        + 권한에 따른 접근을 프록시를 통해 차단할 수 있다.
       
        + 캐싱 기능을 사용하여 중복된 서버 접근을 막을 수 있다.
       
        + 지연 로딩을 적용하여 불필요한 데이터 접근을 막을 수 있다.
       
      + 부가 기능 추가
     
        + 서버가 제공하는 기능에 더해서 부가 기능을 넣을 수 있다.(ex 로그 추적기)
       
        + 요청 값이나, 응답 값을 중간에 변형한다.
       
    + GOF 디자인 패턴
   
      + GOF 디자인 패턴 기준으로 프록시 패턴과 데코레이터 패턴은 사용하는 의도에 따라 구분한다.
     
        + 프록시 패턴: 접근 제어가 목적
          
        + 데코레이터 패턴: 새로운 기능 추가가 목적

+ 프록시 패턴 예제

  ![image](https://github.com/ManchanTime/study/assets/127479677/c141f183-2d8a-4451-a7dc-11ef0ed6c347)

  해당 의존관계를 프록시 패턴으로 변경

  + Subject 인터페이스
    
        public interface Subject {
          String operation();
        }

  + RealSubject

        @Slf4j
        public class RealSubject implements Subject{
      
          @Override
          public String operation() {
              log.info("실제 객체 호출");
              sleep(1000);
              return "data";
          }
      
          private void sleep(int mills) {
              try {
                  Thread.sleep(mills);
              } catch (InterruptedException e) {
                  e.printStackTrace();
              }
          }
        }

    실제로 기능을 수행하는 Subject 구현 클래스
    실제 호출하는 값에 1초 딜레이를 주어 큰 부하를 준다고 가정

  + ProxyPatternClient
 
        public class ProxyPatternClient {
    
          private Subject subject;
      
          public ProxyPatternClient(Subject subject) {
              this.subject = subject;
          }
      
          public void execute() {
              subject.operation();
          }
        }

    Subject 인터페이스에 의존하여 Subject를 호출하는 클라이언트 코드로
    execute()를 통해 Subject.operation()을 호출하여 값을 가져온다.

  + 테스트 코드

        @Test
        void noProxyTest() {
          RealSubject realSubject = new RealSubject();
          ProxyPatternClient client = new ProxyPatternClient(realSubject);
          client.execute();
          client.execute();
          client.execute();
        }

    해당 코드 실행 시 데이터를 가져오는데 1초의 딜레이가 있기 때문에 3초의 시간이 걸린다.

  + CacheProxy
 
        @Slf4j
        public class CacheProxy implements Subject {
      
          private Subject target;
          private String cacheValue;
      
          public CacheProxy(Subject target) {
              this.target = target;
          }
      
          @Override
          public String operation() {
              log.info("프록시 호출");
              if (cacheValue == null) {
                  cacheValue = target.operation();
              }
              return cacheValue;
          }
        }

    프록시 패턴을 적용하여 캐싱 기능을 수행하는 프록시 객체로 기존에 서버에서 받은 값을 저장하여 같은 값 조회 시 서버가 아닌 프록시 객체에서 값을 반환함

  + 테스트 코드
  
        @Test
        void cacheProxyTest() {
          RealSubject realSubject = new RealSubject();
          CacheProxy cacheProxy = new CacheProxy(realSubject);
          ProxyPatternClient client = new ProxyPatternClient(cacheProxy);
  
          client.execute();
          client.execute();
          client.execute();
        }
 
    ![image](https://github.com/ManchanTime/study/assets/127479677/06c684cc-d7fc-41a6-9d59-25e9ca462839)

    CacheProxy가 처음 RealSubject에서 받아온 값을 저장하고 있기 때문에 처음 한번을 제외하고 프록시에서 값을 반환함

  + 프록시 적용 후 의존 관계

    ![image](https://github.com/ManchanTime/study/assets/127479677/9c038e8c-9d05-4a74-930e-eac3c2fe2a6e)

+ 데코레이터 패턴 예제

  ![image](https://github.com/ManchanTime/study/assets/127479677/0bd3808d-3b0d-48f5-8114-982d610b1aca)

  ![image](https://github.com/ManchanTime/study/assets/127479677/7a7e6879-503a-4fec-ae36-7f6f46bbb5f5)


  프록시 패턴과 구조는 동일 but 시간 측정, 메세지 변경 기능 추가, 프록시 체인 형태로 구현

  + RealComponent

        @Slf4j
        public class RealComponent implements Component {
          @Override
          public String operation() {
            log.info("RealComponent 실행");
            return "data";
          }
        }

    실제로 값을 반환하는 Server 역할의 컴포넌트 구체 클래스

  + MessageDecorator

        @Slf4j
        public class MessageDecorator implements Component {
      
          private Component component;
      
          public MessageDecorator(Component component) {
              this.component = component;
          }
      
          @Override
          public String operation() {
              log.info("MessageDecorator 실행");
              String result = component.operation();
              String decoResult = "*****" + result + "*****";
              log.info("MessageDecorator 꾸미기 적용 전={}, 적용 후={}", result, decoResult);
              return decoResult;
          }
        }

    프록시 패턴과 마찬가지로 Component 인터페이스의 구현체로 프록시 객체 생성

  + TimeDecorator

        @Slf4j
        public class TimeDecorator implements Component {
      
          private Component component;
      
          public TimeDecorator(Component component) {
              this.component = component;
          }
      
          @Override
          public String operation() {
              log.info("TimeDecorator 실행");
              long startTime = System.currentTimeMillis();
              String result = component.operation();
              long endTime = System.currentTimeMillis();
              long resultTime = endTime - startTime;
              log.info("TimeDecorator 종료 resultTime={}ms", resultTime);
              return result;
          }
        }

    위 MessageDecorator와 마찬가지로 Component 구현체로 걸린 시간을 로그로 출력해주는 프록시 객체

  + 테스트 결과

        @Test
        void decorator2() {
          RealComponent realComponent = new RealComponent();
          MessageDecorator messageDecorator = new MessageDecorator(realComponent);
          TimeDecorator timeDecorator = new TimeDecorator(messageDecorator);
          DecoratorPatternClient client = new DecoratorPatternClient(timeDecorator);
          client.execute();
        }

    ![image](https://github.com/ManchanTime/study/assets/127479677/45dbbeed-5faf-4fb4-832a-3d790387f8ca)

    값을 꺼내오는데 걸리는 시간과 꺼낸 값을 수정하는 프록시 객체가 모두 실행되는 것을 볼 수 있다.

  + 리팩토링

    Decorator 프록시 객체를 보면 Component를 가져오는 중복 코드가 존재함. 해당 코드를 추상 클래스를 통해서 제거할 수 있음.

    ![image](https://github.com/ManchanTime/study/assets/127479677/5a226841-6674-4c4b-95e9-d17c3a99f21b)
