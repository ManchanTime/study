# 기존 Order 로직에 프록시를 통한 로그 추적기 적용

### 인터페이스 기반 프록시

+ 인터페이스 기반 Controller, Service, Repository 스프링 컨테이너 등록

  ![image](https://github.com/ManchanTime/study/assets/127479677/04331c87-d40f-4102-b791-ef5005ab0af6)

  ![image](https://github.com/ManchanTime/study/assets/127479677/d2f1ff42-0337-48c2-bda5-fac1ecdf9a53)

  + OrderControllerV1Impl
  
    OrderControllerV1 인터페이스 구현체
    
        public class OrderControllerV1Impl implements OrderControllerV1 {
    
          private final OrderServiceV1 orderService;
      
          public OrderControllerV1Impl(OrderServiceV1 orderService) {
              this.orderService = orderService;
          }
      
      
          @Override
          public String request(String itemId) {
              orderService.orderItem(itemId);
              return "ok";
          }
      
          @Override
          public String noLog() {
              return "ok";
          }
        }
  
  + OrderServiceV1Impl
  
    OrderServiceV1 인터페이스 구현체
  
        public class OrderServiceV1Impl implements OrderServiceV1 {
      
          private final OrderRepositoryV1 orderRepository;
      
          public OrderServiceV1Impl(OrderRepositoryV1 orderRepository) {
              this.orderRepository = orderRepository;
          }
      
          @Override
          public void orderItem(String itemId) {
              orderRepository.save(itemId);
          }
        }
  
  + OrderRepositoryV1Impl
  
    OrderRepositoryV1 인터페이스 구현체
  
        public class OrderRepositoryV1Impl implements OrderRepositoryV1 {
          @Override
          public void save(String itemId) {
              //저장 로직
              if (itemId.equals("ex")) {
                  throw new IllegalStateException("예외 발생!");
              }
              sleep(1000);
          }
      
          private void sleep(int mills) {
              try {
                  Thread.sleep(mills);
              } catch (InterruptedException e) {
                  throw new RuntimeException(e);
              }
          }
        }
  
  + 프록시 적용x 스프링 컨테이너 등록

        @Configuration
        public class AppV1Config {
      
          @Bean
          public OrderControllerV1 orderControllerV1() {
              return new OrderControllerV1Impl(orderServiceV1());
          }
      
          @Bean
          public OrderServiceV1 orderServiceV1() {
              return new OrderServiceV1Impl(orderRepositoryV1());
          }
      
          @Bean
          public OrderRepositoryV1 orderRepositoryV1() {
              return new OrderRepositoryV1Impl();
          }
        }

  + 프록시 적용

    + OrderControllerInterfaceProxy

      OrderControllerV1 인터페이스 구현체로 로그 추적기 적용 프록시 객체

          @RequiredArgsConstructor
          public class OrderControllerInterfaceProxy implements OrderControllerV1 {
        
            private final OrderControllerV1 target;
            private final LogTrace logTrace;
        
            @Override
            public String request(String itemId) {
                TraceStatus status = null;
                try {
                    status = logTrace.begin("OrderController.request()");
                    //target 호출
                    String result = target.request(itemId);
                    logTrace.end(status);
                    return result;
                } catch (Exception e) {
                    logTrace.exception(status, e);
                    throw e;
                }
            }
        
            @Override
            public String noLog() {
                return target.noLog();
            }
          }

    + OrderServiceInterfaceProxy

      위와 동일
      
          @RequiredArgsConstructor
          public class OrderServiceInterfaceProxy implements OrderServiceV1 {
        
            private final OrderServiceV1 target;
            private final LogTrace logTrace;
        
            @Override
            public void orderItem(String itemId) {
                TraceStatus status = null;
                try{
                    status = logTrace.begin("OrderService.orderItem()");
                    target.orderItem(itemId);
                    logTrace.end(status);
                }catch (Exception e){
                    logTrace.exception(status, e);
                    throw e;
                }
            }
          }

    + OrderRepsitoryInterfaceProxy

      위와 동일

          @RequiredArgsConstructor
          public class OrderRepositoryInterfaceProxy implements OrderRepositoryV1 {
        
            private final OrderRepositoryV1 target;
            private final LogTrace logTrace;
        
            @Override
            public void save(String itemId) {
                TraceStatus status = null;
                try {
                    status = logTrace.begin("OrderRepository.save()");
                    //target 호출
                    target.save(itemId);
                    logTrace.end(status);
                } catch (Exception e) {
                    logTrace.exception(status, e);
                    throw e;
                }
            }
          }

    + InterfaceProxyConfig

      프록시 객체를 기반으로 스프링 컨테이너 등록, 등록된 LogTrace를 파라미터로 받아 프록시를 컨테이너로 등록하여 Client -> Proxy -> Server 런타임 순서 변

          @Configuration
          public class InterfaceProxyConfig {
        
            @Bean
            public OrderControllerV1 orderController(LogTrace logTrace) {
                OrderControllerV1Impl controllerImpl = new OrderControllerV1Impl(orderService(logTrace));
                return new OrderControllerInterfaceProxy(controllerImpl, logTrace);
            }
        
            @Bean
            public OrderServiceV1 orderService(LogTrace logTrace) {
                OrderServiceV1Impl serviceImpl = new OrderServiceV1Impl(orderRepository(logTrace));
                return new OrderServiceInterfaceProxy(serviceImpl, logTrace);
            }
        
            @Bean
            public OrderRepositoryV1 orderRepository(LogTrace logTrace) {
                OrderRepositoryV1Impl repositoryImpl = new OrderRepositoryV1Impl();
                return new OrderRepositoryInterfaceProxy(repositoryImpl, logTrace);
            }
          }

+ 장점

  + 핵심 로직인 기존 비즈니스 로직에 대한 변경사항이 없음. 프록시를 통해 로그 추적기 작성 가능.
 
  + 기존 메서드 템플릿과 마찬가지로 로그 추적기에 대한 변경사항이 있을 경우 LogTrace 변경으로 한번에 처리 가능
 
  + 인터페이스만 같다면 모든곳에 적용 가능능
 
+ 단점

  + 인터페이스가 필수적이기 때문에 Controller, Service, Repository 모두 각자의 인터페이스 필수

### 클래스 기반 프록시

서버 클래스를 상속받아 프록시 객체 생성

![image](https://github.com/ManchanTime/study/assets/127479677/98c01549-18d0-470b-b8b9-4cc3f2bed195)

![image](https://github.com/ManchanTime/study/assets/127479677/092c7451-99ed-400a-9ad1-f46cc615f22f)

+ 클래스 기 Controller, Service, Repository 구체 작성

  + OrderControllerV2

          @Slf4j
          @RequestMapping
          @ResponseBody
          public class OrderControllerV2 {
        
            private final OrderServiceV2 orderService;
        
            public OrderControllerV2(OrderServiceV2 orderService) {
                this.orderService = orderService;
            }
        
            @GetMapping("/v2/request")
            public String request(String itemId) {
                orderService.orderItem(itemId);
                return "ok";
            }
        
            @GetMapping("/v2/no-log")
            public String noLog() {
                return "ok";
            }
          }

  + OrderServiceV2

        public class OrderServiceV2 {
      
          private final OrderRepositoryV2 orderRepository;
      
          public OrderServiceV2(OrderRepositoryV2 orderRepository) {
              this.orderRepository = orderRepository;
          }
      
          public void orderItem(String itemId) {
              orderRepository.save(itemId);
          }
        }

  + OrderRepositoryV2

        public class OrderRepositoryV2 {
      
          public void save(String itemId) {
              //저장 로직
              if (itemId.equals("ex")) {
                  throw new IllegalStateException("예외 발생!");
              }
              sleep(1000);
          }
      
          private void sleep(int mills) {
              try {
                  Thread.sleep(mills);
              } catch (InterruptedException e) {
                  throw new RuntimeException(e);
              }
          }
        }

  + 프록시x 스프링 컨테이너 등록

        @Configuration
        public class AppV2Config {
          @Bean
          public OrderControllerV2 orderControllerV2() {
              return new OrderControllerV2(orderServiceV2());
          }
      
          @Bean
          public OrderServiceV2 orderServiceV2() {
              return new OrderServiceV2(orderRepositoryV2());
          }
      
          @Bean
          public OrderRepositoryV2 orderRepositoryV2() {
              return new OrderRepositoryV2();
          }
        }

+ 프록시 적용

  + OrderControllerConcreteProxy

    이전 인터페이스와 다르게 클래스를 상속받아 만들기 때문에 부모 클래스의 생성자를 받아야 함 -> super 필수 하지만 final 객체는 받아올 수 없기 때문에 super(null)로 아무것도 받지 않음

        public class OrderControllerConcreteProxy extends OrderControllerV2 {
      
          private final OrderControllerV2 target;
          private final LogTrace logTrace;
      
          public OrderControllerConcreteProxy(OrderControllerV2 target, LogTrace logTrace) {
              super(null);
              this.target = target;
              this.logTrace = logTrace;
          }
      
          @Override
          public String request(String itemId) {
              TraceStatus status = null;
              try {
                  status = logTrace.begin("OrderController.request()");
                  //target 호출
                  String result = target.request(itemId);
                  logTrace.end(status);
                  return result;
              } catch (Exception e) {
                  logTrace.exception(status, e);
                  throw e;
              }
          }
      
          @Override
          public String noLog() {
              return target.noLog();
          }
        }

  + OrderServiceConcreteProxy
 
    위와 동일

        public class OrderServiceConcreteProxy extends OrderServiceV2 {
      
          private final OrderServiceV2 target;
          private final LogTrace logTrace;
      
          public OrderServiceConcreteProxy(OrderServiceV2 target, LogTrace logTrace) {
              super(null);
              this.target = target;
              this.logTrace = logTrace;
          }
      
          @Override
          public void orderItem(String itemId) {
              TraceStatus status = null;
              try {
                  status = logTrace.begin("OrderService.orderItem()");
                  //target 호출
                  target.orderItem(itemId);
                  logTrace.end(status);
              } catch (Exception e) {
                  logTrace.exception(status, e);
                  throw e;
              }
          }
        }

  + OrderRepositoryConcreteProxy
 
    위와 동일

        @RequiredArgsConstructor
        public class OrderRepositoryConcreteProxy extends OrderRepositoryV2 {
      
          private final OrderRepositoryV2 target;
          private final LogTrace logTrace;
      
          @Override
          public void save(String itemId) {
              TraceStatus status = null;
              try {
                  status = logTrace.begin("OrderRepository.save()");
                  //target 호출
                  target.save(itemId);
                  logTrace.end(status);
              } catch (Exception e) {
                  logTrace.exception(status, e);
                  throw e;
              }
          }
        }

  + ConcreteProxyConfig

    스프링 컨테이너에 프록시 기반으로 등록

        @Configuration
        public class ConcreteProxyConfig {
      
          @Bean
          public OrderControllerV2 orderController(LogTrace logTrace) {
              OrderControllerV2 controllerImpl = new OrderControllerV2(orderService(logTrace));
              return new OrderControllerConcreteProxy(controllerImpl, logTrace);
          }
      
          @Bean
          public OrderServiceV2 orderService(LogTrace logTrace) {
              OrderServiceV2 serviceImpl = new OrderServiceV2(orderRepository(logTrace));
              return new OrderServiceConcreteProxy(serviceImpl, logTrace);
          }
      
          @Bean
          public OrderRepositoryV2 orderRepository(LogTrace logTrace) {
              OrderRepositoryV2 repositoryImpl = new OrderRepositoryV2();
              return new OrderRepositoryConcreteProxy(repositoryImpl, logTrace);
          }
        }

+ 장점

  + 클래스를 스프링 컨테이너에 등록하기 때문에 따로 인터페이스를 생성하지 않아도 된다.
 
+ 단점

  + 클래스를 상속받기 때문에 상속 제약을 모두 적용받는다.
 
### 정리

+ 인터페이스 방식이나 클래스 상속 방식 모두 자주 사용하는 방식으로 알아둬야한다.

+ 장단점만 놓고 봤을 때 인터페이스 방식이 구현이나 작동방식 모두 좋아보인다. 인터페이스를 통해서 역할과 구현을 명확하게 나누는 것이 좋기 때문이다. 하지만
  인터페이스를 모두 구현해야 되는 것이 단점이다.

+ 인터페이스를 사용하는 것은 구현을 변경할 때 효과적인데 실용적으로 봤을 때 구현을 변경할 일이 거의 없는 코드에 이 방식을 적용하는 것은 비효율 적이다.

+ 두 방식 모두 해당 클래스나 인터페이스에 대한 구현체가 있어야 한다. 이 때문에 동적 프록시를 이용하여 많은 프록시 클래스를 줄일 수 있다.
