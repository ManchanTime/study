# 동적 프록시 기술

+ why?

  이전까지 로그 추적기 동시성 문제, 코드 중복 문제를 해결해왔다. 남은 문제는 클래스 수를 줄이는 것이다.
  대상 클래스마다 프록시 클래스를 생성하여 적용하는 것이 비효율적이기 때문이다.

### 리플렉션

+ ReflectionTest

  Hello 클래스의 공통 로직을 실행하는데 코드 상 중복되는 부분이 많다. 이를 해결하기 위해서 Method 객체를 이용한다.
  Method 객체는 적용한 클래스의 메서드 이름을 바탕으로 메서드를 동적으로 실행할 수 있는 기능을 제공한다. 이를 바탕으로 dynamicCall이라는 메서드를 생성하여
  Method 객체와 target 클래스를 기반으로 해당 클래스의 method를 동적으로 실행하여 중복 코드를 제거할 수 있다.

      @Slf4j
      public class ReflectionTest {
      
          @Test
          void reflection0(){
              Hello target = new Hello();
      
              //공통 로직1 시작
              log.info("start");
              String result1 = target.callA();
              log.info("result={}", result1);
              //공통 로직1 종료
      
              //공통 로직2 시작
              log.info("start");
              String result2 = target.callB();
              log.info("result={}", result2);
              //공통 로직2 종료
          }
      
          @Test
          void reflection1() throws Exception {
              //클래스 정보
              Class classHello = Class.forName("hello.proxy.jdkdynamic.ReflectionTest$Hello");
      
              Hello target = new Hello();
              //callA 메서드 정보
              Method methodCallA = classHello.getMethod("callA");
              Object result1 = methodCallA.invoke(target);
              log.info("result1={}", result1);
      
              //callB 메서드 정보
              Method methodCallB = classHello.getMethod("callB");
              Object result2 = methodCallB.invoke(target);
              log.info("result2={}", result2);
          }
      
          @Test
          void reflection2() throws Exception {
              //클래스 정보
              Class classHello = Class.forName("hello.proxy.jdkdynamic.ReflectionTest$Hello");
      
              Hello target = new Hello();
              //callA 메서드 정보
              Method methodCallA = classHello.getMethod("callA");
              dynamicCall(methodCallA, target);
      
              //callB 메서드 정보
              Method methodCallB = classHello.getMethod("callB");
              dynamicCall(methodCallB, target);
          }
      
          private void dynamicCall(Method method, Object target) throws Exception {
              log.info("start");
              Object result = method.invoke(target);
              log.info("result={}", result);
          }
      
          @Slf4j
          static class Hello {
              public String callA() {
                  log.info("callA");
                  return "A";
              }
      
              public String callB() {
                  log.info("callB");
                  return "B";
              }
          }
      }

+ 문제점

  보기에는 해당 방법으로 중복되는 로직 코드를 Method로 묶어서 처리할 수 있지만 Method 객체 생성에 메서드 이름을 문자로 주어 런타임 단계에서 예외처리 방식으로 오류를 잡는다.
  다시말해 컴파일 단계에서 오류를 잡을 수 없다.

### JDK 동적 프록시

+ 소개

  + 프록시 객체를 동적으로 런타임 단계에서 생성하여 개발자가 직접 프록시 클래스를 작성할 필요가 없다.
  + 인터페이스를 기반으로 프록시를 생성하기 때문에 인터페이스가 필수다. 해당 코드에서 v1같은 형태여야 한다.
  + InvocationHandler 인터페이스 구현체를 생성하여 동적 생성될 프록시 객체에서 실행할 로직을 구현한다.

+ 동적 프록시 적용 전 후 비교

  + 적용 전

    ![image](https://github.com/ManchanTime/study/assets/127479677/688dacbc-591f-4161-a87c-48e5ad3f265f)

  + 적용 후

    ![image](https://github.com/ManchanTime/study/assets/127479677/b82062c9-b7ef-4ac2-b643-5d4736b7eb1b)

+ 동적 프록시 테스트 코드

  + 초기 설정

    AInterface, AImpl, BInterface, BImpl 두 인터페이스-구현체 세트가 있고 해당 세트에 프록시 패턴을 적용할 때 JDK 동적 프록시를 사용하겠다.

  + TimeInvocationHandler
 
    target 객체(실제 로직 실행 객체)를 Object로 받아 타입 문제를 해결하고 invoke 메서드에 로직을 구현한다. 시간 측정 로그를 적용하고 method.invoke를 통해서 target의 메서드를 실행한다.

        @Slf4j
        public class TimeInvocationHandler implements InvocationHandler {
        
            private final Object target;
        
            public TimeInvocationHandler(Object target) {
                this.target = target;
            }
        
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                log.info("TimeProxy 실행");
                long startTime = System.currentTimeMillis();
        
                Object result = method.invoke(target, args);
        
                long endTime = System.currentTimeMillis();
                long resultTime = endTime - startTime;
                log.info("TimeProxy 종료 resultTime={}", resultTime);
                return result;
            }
        }

  + JdkDynamicProxyTest
 
    A 세트나 B 세트나 모두 JDK 동적 프록시를 통해 프록시를 생성하고 프록시를 통해 로직을 실행한다.
    Proxy.newProxyInstance를 통해 프록시를 동적 생성한다.

        @Slf4j
        public class JdkDynamicProxyTest {
        
            @Test
            void dynamicA() {
                AInterface target = new AImpl();
                TimeInvocationHandler handler = new TimeInvocationHandler(target);
        
                AInterface proxy = (AInterface) Proxy.newProxyInstance(AInterface.class.getClassLoader(), new Class[]{AInterface.class}, handler);
        
                proxy.call();
                log.info("targetClass={}", target.getClass());
                log.info("proxyClass={}", proxy.getClass());
            }
        
            @Test
            void dynamicB() {
                BInterface target = new BImpl();
                TimeInvocationHandler handler = new TimeInvocationHandler(target);
        
                BInterface proxy = (BInterface) Proxy.newProxyInstance(BInterface.class.getClassLoader(), new Class[]{BInterface.class}, handler);
        
                proxy.call();
                log.info("targetClass={}", target.getClass());
                log.info("proxyClass={}", proxy.getClass());
            }
        }

  + 결과

    ![image](https://github.com/ManchanTime/study/assets/127479677/fd6df49f-86c1-45db-a8f6-2b79bb5418f3)

+ 실제 런타임 적용

  v1을 기반으로 적용

  + LogTraceBasicHandler
 
    Order 로직에 사용될 로그 추적기를 적용하고 method.invoke를 통해 다음 단계로 진행한다.

        public class LogTraceBasicHandler implements InvocationHandler {
        
            private final Object target;
            private final LogTrace logTrace;
        
            public LogTraceBasicHandler(Object target, LogTrace logTrace) {
                this.target = target;
                this.logTrace = logTrace;
            }
        
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                TraceStatus status = null;
                try {
                    String message = method.getDeclaringClass().getSimpleName() + "." + method.getName() + "()";
                    status = logTrace.begin(message);
                    //target 호출
                    Object result = method.invoke(target, args);
                    logTrace.end(status);
                    return result;
                } catch (Exception e) {
                    logTrace.exception(status, e);
                    throw e;
                }
            }
        }

  + DynamicProxyConfig
 
    스프링 컨테이너에 등록할 때 Proxy.newProxyInstance를 통해서 프록시를 동적으로 생성하고 해당 프록시를 스프링 컨테이너에 등록한다.

        @Configuration
        public class DynamicProxyConfig {
        
            @Bean
            public OrderRepositoryV1 orderRepositoryV1(LogTrace logTrace) {
                OrderRepositoryV1 orderRepository = new OrderRepositoryV1Impl();
        
                OrderRepositoryV1 proxy = (OrderRepositoryV1) Proxy.newProxyInstance(
                        OrderRepositoryV1.class.getClassLoader(),
                        new Class[]{OrderRepositoryV1.class},
                        new LogTraceBasicHandler(orderRepository, logTrace));
        
                return proxy;
            }
        
            @Bean
            public OrderServiceV1 orderServiceV1(LogTrace logTrace) {
                OrderServiceV1 orderService = new OrderServiceV1Impl(orderRepositoryV1(logTrace));
        
                OrderServiceV1 proxy = (OrderServiceV1) Proxy.newProxyInstance(
                        OrderServiceV1.class.getClassLoader(),
                        new Class[]{OrderServiceV1.class},
                        new LogTraceBasicHandler(orderService, logTrace));
                return proxy;
            }
        
            @Bean
            public OrderControllerV1 orderController(LogTrace logTrace) {
                OrderControllerV1 orderController = new OrderControllerV1Impl(orderServiceV1(logTrace));
        
                OrderControllerV1 proxy = (OrderControllerV1) Proxy.newProxyInstance(
                        OrderControllerV1.class.getClassLoader(),
                        new Class[]{OrderControllerV1.class},
                        new LogTraceBasicHandler(orderController, logTrace)
                );
                return proxy;
            }
        }

+ 문제

  no-log api를 실행시켜도 로그가 남는다. 동적 프록시를 생성할 때 조건이 없어 모든 api에 대한 동적 프록시가 생성되기 때문이다.

  + LogTraceFilterHandler

    로그 추적기 사용 로직 이름 패턴을 저장할 patterns를 생성하여 받아오고 이 패턴을 PatternMatchUtils의 simpleMatch를 사용하여 비교 후 있다면 추적기를 적용하고
    없다면 적용하지 않는다. 물론 config 파일도 LogTraceFilterHandler로 변경한다.
  
          public class LogTraceFilterHandler implements InvocationHandler {
        
            private final Object target;
            private final LogTrace logTrace;
            private final String[] patterns;
        
            public LogTraceFilterHandler(Object target, LogTrace logTrace, String[] patterns) {
                this.target = target;
                this.logTrace = logTrace;
                this.patterns = patterns;
            }
        
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        
                //메서드 이름 필터
                String methodName = method.getName();
                //save, request, reque*, *est
                if (!PatternMatchUtils.simpleMatch(patterns, methodName)) {
                    return method.invoke(target, args);
                }
        
                TraceStatus status = null;
                try {
                    String message = method.getDeclaringClass().getSimpleName() + "." + method.getName() + "()";
                    status = logTrace.begin(message);
                    //target 호출
                    Object result = method.invoke(target, args);
                    logTrace.end(status);
                    return result;
                } catch (Exception e) {
                    logTrace.exception(status, e);
                    throw e;
                }
            }
        }

### CGLIB

+ 소개

  + CGLIB는 인터페이스가 없어도 구체 클래스만 가지고 동적 클래스를 생성할 수 있다.
  + 스프링 사용 시 외부 라이브러리없이 사용할 수 있다.
  + 사실 거의 사용하지 않는다. -> ProxyFactory를 주로 이용한다.
 
+ 의존 관계

  ![image](https://github.com/ManchanTime/study/assets/127479677/2e286518-3f39-4c97-8da4-b9ab39162f1d)

  ![image](https://github.com/ManchanTime/study/assets/127479677/ef7f9d41-4812-4dac-8ff4-c5d5368ae6ec)

+ 테스트 코드

  + TimeMethodInterceptor

    MethodInterceptor 구현체로 JDK 동적 프록시처럼 target 객체를 받고 로그 추적기 로직 사이에 CGLIB의 methodProxy.invoke를 통해 메인 로직을 실행한다.

        @Slf4j
        public class TimeMethodInterceptor implements MethodInterceptor {
        
            private final Object target;
        
            public TimeMethodInterceptor(Object target) {
                this.target = target;
            }
        
            @Override
            public Object intercept(Object obj, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
                log.info("TimeProxy 실행");
                long startTime = System.currentTimeMillis();
        
                Object result = methodProxy.invoke(target, args);
        
                long endTime = System.currentTimeMillis();
                long resultTime = endTime - startTime;
                log.info("TimeProxy 종료 resultTime={}", resultTime);
                return result;
            }
        }

  + CglibTest

        @Slf4j
        public class CglibTest {
        
            @Test
            void cglib() {
                ConcreteService target = new ConcreteService();
        
                Enhancer enhancer = new Enhancer();
                enhancer.setSuperclass(ConcreteService.class);
                enhancer.setCallback(new TimeMethodInterceptor(target));
                ConcreteService proxy = (ConcreteService) enhancer.create();
                log.info("targetClass={}", target.getClass());
                log.info("proxyClass={}", proxy.getClass());
        
                proxy.call();
            }
        }

    + 결과

      ![image](https://github.com/ManchanTime/study/assets/127479677/eaa9ba80-fc5c-4556-8918-d41559114316)

      인터페이스 없이 프록시를 생성하여 로그 추적기 적용이 잘 된다.

+ 제약

  + 클래스 기반 프록시는 상속이 기본이기 때문에 상속 문제가 모두 발생
    + 부모 클래스의 생성자 체크 -> 기본 생성자 필요 -> setter를 이용하여 생성자 없이 값을 주입해야 함.
    + final 키워드 시 상속, 오버라이딩 불가
   
### 정리

+ 남은 문제
  + 인터페이스 있는 경우 + 클래스 기반인 경우에 어떻게 동적 프록시를 적용할 것인가?
  + 특정 조건에 맞는 프록시 적용 로직도 중복 코드가 발생하기 때문에 공통으로 바꾸고 싶다면?
