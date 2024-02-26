# 상속 문제 해결하기

### 전략 패턴

전략 패턴은 변하지 않는 부분을 부모 클래스가 아닌 Context라는 곳에 두고 변하는 부분을 Strategy라는 인터페이스를 만들고 해당 구현체를 통해 해결한다.
상속이 아닌 위임으로 해결하는 것이다. 여기서 Context는 변하지 않는 템플릿 역할을 Strategy는 변하는 알고리즘 역할을 한다.

+ 구조

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/7e89bf06-5edf-4351-974c-c534e908aa8f)

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/770d38dc-7af6-4f8a-8580-c5345895f815)

  Context는 클라이언트로부터 실행 명령을 받고 Strategy는 call을 통해서 구현체의 알고리즘을 수행한다.

+ 콜백 패턴

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/cfed6ec4-3a34-4d85-bb73-3a7b41d50831)

  콜백 패턴은 코드가 호출은 되지만 코드를 넘겨준 곳의 뒤에서 실행되는 패턴이다.
  위 Context, Strategy에서 Context의 콜백은 Strategy가 되는 것이다. Context의 execute가 실행될 때 Strategy를 넘겨주고 Context 뒤에서 Strategy가 실행되는 것이다.
  이는 JDBCTemplate, RestTemplate 등 여러 곳에서 사용된다.

+ TraceCallback
  
    public interface TraceCallback<T> {
      T call();
    }

+ TraceTemplate

      public class TraceTemplate {
  
        private final LogTrace trace;
    
        public TraceTemplate(LogTrace trace) {
            this.trace = trace;
        }
    
        public <T> T execute(String message, TraceCallback<T> callback) {
            TraceStatus status = null;
            try {
                status = trace.begin(message);
                T result = callback.call();
                trace.end(status);
                return result;
            } catch (Exception e) {
                trace.exception(status, e);
                throw e;
            }
        }
      }

  execute 메서드를 통해서 로그를 생성하고 callback 객체를 파라미터로 받아서 call 메서드를 통해 비즈니스 로직을 실행한다.

+ 사용 예시

  + OrderControllerV6

        @RestController
        public class OrderControllerV6 {
      
          private final OrderServiceV6 orderService;
          private final TraceTemplate template;
      
          public OrderControllerV6(OrderServiceV6 orderService, LogTrace trace) {
              this.orderService = orderService;
              this.template = new TraceTemplate(trace);
          }
      
          @GetMapping("/v6/request")
          public String request(String itemId) {
              return template.execute("OrderController.request()", () -> {
                  orderService.orderItem(itemId);
                  return "ok";
              });
          }
        }

    TraceTemplate 객체를 생성하고 스프링 빈으로 주입받은 LogTrace 객체를 파라미터로 넣어준다.
    TraceTemplate 객체의 execute를 통해서 로그 생성과 비즈니스 로직을 모두 수행한다. 이 때 람다 함수를 통해서 코드를 줄일 수 있다.

  + 결과

    ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/b8aece20-94f1-45b3-be07-5991e638a0c6)

    잘 나온다.

+ 결론

  의존 관계 문제를 위임을 통해 해결했고 코드를 더욱 간결하게 줄일 수 있었다.
  저기에 추가로 TraceTemplate 또한 LogTrace처럼 스프링 빈을 주입한다면 더 줄일 수 있을 것이다.

+ 한계

  템플릿 메서드 패턴이나 전략 패턴을 사용한다고 해도 결국 로그 추적기를 사용하기 위해서 본 코드를 수정하는 것은 변하지 않는다.
  이를 해결하는 방법 또한 사용해보겠다.
