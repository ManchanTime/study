# 로그 추적기 중복 코드 줄이기

### 템플릿 메서드 패턴

수동 작성 로그의 동시성 문제를 ThreadLocal을 통해서 해결했다. 하지만 코드 자체만 놓고 보면 실 사용하기에 문제가 있다.
로그를 남기고 싶은 모든 비즈니스 로직에 try-catch문을 적용해야하기 때문이다.
try-catch문을 사용하는 것은 문제가 되지 않으나 사용하는 의도가 문제가 된다. 로그를 남기는 것 자체는 비즈니스 로직과 상관없은 부가 기능이기 때문이다.
또한 로그 추적기 자체는 변하지 않는 부분이기 때문에 변하는 부분인 비즈니스 로직과는 분리하는 것이 좋다.
이를 템플릿 메서드 패턴을 이용하여 해결하겠다.

+ 정의

  템플릿 메서드 패턴을 GOF 디자인 패턴에서 하위 클래스가 알고리즘의 구조를 변경하지 않고 알고리즘의 특정 단계를 재정의 할 수 있는 패턴이라 한다.

+ 구조

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/c781783a-d554-4c89-9f0e-76e2ce5939ba)

  추상 클래스를 생성하여 로그 추적 기능을 담아두고 call이라는 추상 메서드를 생성하여 여기에 원하는 비즈니스 로직을 넣어 실행하는 방식으로
  로그 추적 기능과 비즈니스 로직을 분리할 수 있다.
  
+ AbstractTemplate

      public abstract class AbstractTemplate<T> {
  
        private final LogTrace trace;
    
        public AbstractTemplate(LogTrace trace) {
            this.trace = trace;
        }
    
        public T execute(String message) {
            TraceStatus status = null;
            try {
                status = trace.begin(message);
                T result = call();
                trace.end(status);
                return result;
            } catch (Exception e) {
                trace.exception(status, e);
                throw e;
            }
        }

        protected abstract T call();
      }

+ 적용 예시

  + OrderControllerV5

        @RestController
        @RequiredArgsConstructor
        public class OrderControllerV5 {
      
          private final OrderServiceV5 orderService;
          private final LogTrace trace;
      
          @GetMapping("/v5/request")
          public String request(String itemId) {
              AbstractTemplate<String> template = new AbstractTemplate<>(trace) {
                  @Override
                  protected String call() {
                      orderService.orderItem(itemId);
                      return "ok";
                  }
              };
              return template.execute("OrderController.request()");
          }
        }
    AbstractTemplate이라는 추상 클래스를 생성하고 해당 클래스에 존재하는 call이라는 추상 메서드를 상속받아 원하는 비즈니스 로직을 넣어 실행한다.
    이 때 로그 추적 기능 자체는 AbstractTemplate에 존재하기 때문에 call 메서드를 통해서 비즈니스 로직만 담아 서로 분리시킬 수 있다.

+ 결과

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/b334bc6d-dbda-422f-b234-b924237a7fe0)

  기존 기능과 같다. 하지만 로그 추적 기능을 매 로직마다 생성할 필요가 없고 로그 추적 기능에 변경사항이 생길 때 모든 로직을 수정할 필요없이
  추상 클래스에 존재하는 로그 생성 로직만 수정하면 되기 때문에 코드 유연성이 높다.

+ 단점

  템플릿 메서드 패턴은 추상 클래스를 서브 클래스가 상속하여 call과 같은 추상 메소드를 오버라이딩하여 사용한다.
  이 때 상속에서 오는 단점들을 모두 안고 가야한다.
  특히 자식 클래스가 부모 클래스와 컴파일 시점에서 강하게 결합되는 문제가 있다. 이는 의존관계에 대한 문제이다.
  해당 추상 클래스와 상속 클래스의 관계를 보면 상속 클래스는 부모 클래스인 추상 클래스의 기능을 하나도 사용하지 않는다.
  이는 로그 추적과 비즈니스 로직을 분리하기 위함이었다.
  이 때문에 자식 클래스는 부모 클래스 기능을 사용하지 않는 사실 상 분리된 관계이지만 부모 클래스를 알아야한다.
  이 때문에 부모 클래스의 수정이 자식 클래스에도 영향을 미칠 수 있다.
  또한 비즈니스 로직을 생성할 때마다 추상 클래스 객체를 생성하고 해당 객체에 익명 내부 클래스를 만들어야한다는 문제도 있다.
  이를 전략 패턴을 통해 해결하겠다.
