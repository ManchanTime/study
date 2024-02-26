# 스프링 AOP 구현

### 예시 프로젝트

![image](https://github.com/ManchanTime/study/assets/127479677/c0401b1d-dbc7-4823-96bf-68f6b3f70e3b)

### 구현 시작

+ 테스트 코드

      @Slf4j
      @SpringBootTest
      @Import(적용할 Aspect 클래스.class)
      public class AopTest {
      
          @Autowired
          OrderService orderService;
      
          @Autowired
          OrderRepository orderRepository;
      
          @Test
          void aopFind() {
              log.info("isAopProxy, orderService={}", AopUtils.isAopProxy(orderService));
              log.info("isAopProxy, orderRepository={}", AopUtils.isAopProxy(orderRepository));
          }
      
          @Test
          void success() {
              orderService.orderItem("itemA");
          }
      
          @Test
          void exception() {
              Assertions.assertThatThrownBy(() -> orderService.orderItem("ex"))
                      .isInstanceOf(IllegalStateException.class);
          }
      }

+ 스프링 AOP v1

      @Slf4j
      @Aspect
      public class AspectV1 {
      
          @Around("execution(* hello.aop.order..*(..))")
          public Object doLog(ProceedingJoinPoint joinPoint) throws Throwable {
              log.info("[log] {}", joinPoint.getSignature()); //join point 시그니처
              return joinPoint.proceed();
          }
      }

  + @Around 애노테이션에 "execution..." -> 포인트컷
  + doLog 메서드 -> advice
  + hello.aop.order 하위 패키지는 모두 포인트컷 대상이 되기 때문에 OrderService, OrderRepository 모두 AOP 적용 대상이 됨
  + 테스트 결과(access() 기준)

    ![image](https://github.com/ManchanTime/study/assets/127479677/83720220-4898-422c-a0b7-3e2992442d31)

    ![image](https://github.com/ManchanTime/study/assets/127479677/c0c3b379-82d2-45ec-aceb-7b9d1d9dc98c)

+ 스프링 AOP v2

      @Slf4j
      @Aspect
      public class AspectV2 {
      
          @Pointcut("execution(* hello.aop.order..*(..))")
          private void allOrder() {} //pointcut signature
      
          @Around("allOrder()")
          public Object doLog(ProceedingJoinPoint joinPoint) throws Throwable {
              log.info("[log] {}", joinPoint.getSignature()); //join point 시그니처
              return joinPoint.proceed();
          }
      }

  + @Pointcut
    + @Pointcut에 포인트컷 표현식을 사용한다.
    + 메서드 이름과 파라미터를 합쳐서 포인트컷 시그니처라고 한다.
    + 반환 타입은 void여야 한다.
    + 코드는 비워둔다.
    + @Around에 직접 지정해도 되지만 여러 어드바이스 메서드에서 사용할 수 있기 때문에 따로 분리하는 것도 좋다.
    + 외부 클래스로 빼서 사용한다면 public으로 아니면 private으로 지정한다.
  + 테스트 결과

    ![image](https://github.com/ManchanTime/study/assets/127479677/2a3bdb38-0802-46a1-a1dd-854c762433cc)

    결과적으로는 v1과 같다. 단순히 Pointcut만 분리한 것이다.

+ 스프링 AOP v3

      @Slf4j
      @Aspect
      public class AspectV3 {
      
          @Pointcut("execution(* hello.aop.order..*(..))")
          private void allOrder() {} //pointcut signature
      
          //클래스 이름 패턴이 *Service
          @Pointcut("execution(* *..*Service.*(..))")
          private void allService(){};
      
          @Around("allOrder()")
          public Object doLog(ProceedingJoinPoint joinPoint) throws Throwable {
              log.info("[log] {}", joinPoint.getSignature()); //join point 시그니처
              return joinPoint.proceed();
          }
      
          //hello.aop.order 패키지와 하위 패키지 이면서 클래스 이름 패턴이 *Service
          @Around("allOrder() && allService()")
          public Object doTransaction(ProceedingJoinPoint joinPoint) throws Throwable {
              try {
                  log.info("[트랜잭션 시작] {}", joinPoint.getSignature());
                  Object result = joinPoint.proceed();
                  log.info("[트랜잭션 종료] {}", joinPoint.getSignature());
                  return result;
              } catch (Exception e) {
                  log.info("[트랜잭션 롤백] {}", joinPoint.getSignature());
                  throw e;
              }finally {
                  log.info("[리소스 릴리즈] {}", joinPoint.getSignature());
              }
          }
      }

  + allOrder() 포인트컷으로 hello.aop.order 하위 패키지에 존재하는 OrderService, OrderRepository 모두에 적용한다.
  + allService() 포인트컷으로 *Service에 해당하는 클래스만 대상으로 한다.
  + @Around("allOrder() && allService())로 조합이 가능하다. 즉, order 하위 패키지 이면서 타입 이름 패턴이 *Service인 OrderService만 대상으로 한다.
  + 테스트 결과

    ![image](https://github.com/ManchanTime/study/assets/127479677/962b2cf2-b13c-4d51-85f3-fd95a860d528)

    트랜잭션이라는 Advice 또한 적용되었다.

    ![image](https://github.com/ManchanTime/study/assets/127479677/9a0a8535-0f4d-46bf-94fe-d30148e4fa5e)

+ 스프링 AOP v4

      @Slf4j
      @Aspect
      public class AspectV4Pointcut {
      
          @Around("hello.aop.order.aop.Pointcuts.allOrder()")
          public Object doLog(ProceedingJoinPoint joinPoint) throws Throwable {
              log.info("[log] {}", joinPoint.getSignature()); //join point 시그니처
              return joinPoint.proceed();
          }
      
          //hello.aop.order 패키지와 하위 패키지 이면서 클래스 이름 패턴이 *Service
          @Around("hello.aop.order.aop.Pointcuts.orderAndService()")
          public Object doTransaction(ProceedingJoinPoint joinPoint) throws Throwable {
              try {
                  log.info("[트랜잭션 시작] {}", joinPoint.getSignature());
                  Object result = joinPoint.proceed();
                  log.info("[트랜잭션 종료] {}", joinPoint.getSignature());
                  return result;
              } catch (Exception e) {
                  log.info("[트랜잭션 롤백] {}", joinPoint.getSignature());
                  throw e;
              }finally {
                  log.info("[리소스 릴리즈] {}", joinPoint.getSignature());
              }
          }
      }

  + Pointcuts

        public class Pointcuts {
        
            //hello.aop.order 패키지와 하위 패키지
            @Pointcut("execution(* hello.aop.order..*(..))")
            public void allOrder() {} //pointcut signature
        
            //클래스 이름 패턴이 *Service
            @Pointcut("execution(* *..*Service.*(..))")
            public void allService() {}
        
            //allOrder && allService
            @Pointcut("allOrder() && allService()")
            public void orderAndService() {}
        }

  + 포인트컷을 Pointcuts라는 클래스에 따로 분리하여 작성할 수 있다.
  + 여러 Aspect 클래스에서 포인트컷을 중복 작성없이 사용할 수 있다.
  + 테스트 결과

    ![image](https://github.com/ManchanTime/study/assets/127479677/e41185a6-5f95-4e15-a52f-b4a100e29112)

    v3와 결과는 같다.

+ 스프링 AOP v5

      @Slf4j
      public class AspectV5Order {
      
          @Aspect
          @Order(2)
          public static class LogAspect {
              @Around("hello.aop.order.aop.Pointcuts.allOrder()")
              public Object doLog(ProceedingJoinPoint joinPoint) throws Throwable {
                  log.info("[log] {}", joinPoint.getSignature()); //join point 시그니처
                  return joinPoint.proceed();
              }
          }
      
          @Aspect
          @Order(1)
          public static class TxAspect {
              //hello.aop.order 패키지와 하위 패키지 이면서 클래스 이름 패턴이 *Service
              @Around("hello.aop.order.aop.Pointcuts.orderAndService()")
              public Object doTransaction(ProceedingJoinPoint joinPoint) throws Throwable {
                  try {
                      log.info("[트랜잭션 시작] {}", joinPoint.getSignature());
                      Object result = joinPoint.proceed();
                      log.info("[트랜잭션 종료] {}", joinPoint.getSignature());
                      return result;
                  } catch (Exception e) {
                      log.info("[트랜잭션 롤백] {}", joinPoint.getSignature());
                      throw e;
                  }finally {
                      log.info("[리소스 릴리즈] {}", joinPoint.getSignature());
                  }
              }
          }
      }

  + Transaction 후 Log가 작동되게 하고 싶다.
  + Aspect 클래스에서 메서드의 순서를 보장하지 않는다.
  + @Order를 통해 순서를 지정할 수 있지만 클래스에 한정되기 때문에 하나의 Aspect 클래스에서 메서드에 @Order를 지정해도 작동하지 않는다.
  + static class로 메서드를 하나하나 Aspect 클래스로 만들고 @Order를 통해 순서를 지정해야 한다.
  + 테스트 결과

    ![image](https://github.com/ManchanTime/study/assets/127479677/f7aa973b-b986-4b1d-a5ff-19d7998c72e3)
 
    ![image](https://github.com/ManchanTime/study/assets/127479677/b498b08b-e870-44c4-95ff-2a9bc76fca75)

    트랜잭션이 실행되고 로그가 실행된다. 이 때 메서드가 모두 클래스로 변경되었기 때문에
    @Import({AspectV5Order.TxAspect.class, AspectV5Order.LogAspect.class})
    모든 클래스를 빈에 등록해야 한다.

+ 스프링 AOP v6

      @Slf4j
      @Aspect
      public class AspectV6Advice {
      
          //hello.aop.order 패키지와 하위 패키지 이면서 클래스 이름 패턴이 *Service
          @Around("hello.aop.order.aop.Pointcuts.orderAndService()")
          public Object doTransaction(ProceedingJoinPoint joinPoint) throws Throwable {
              try {
                  //@Before
                  log.info("[트랜잭션 시작] {}", joinPoint.getSignature());
                  Object result = joinPoint.proceed();
                  //@AfterReturning
                  log.info("[트랜잭션 종료] {}", joinPoint.getSignature());
                  return result;
              } catch (Exception e) {
                  //@AfterThrowing
                  log.info("[트랜잭션 롤백] {}", joinPoint.getSignature());
                  throw e;
              }finally {
                  //@After
                  log.info("[리소스 릴리즈] {}", joinPoint.getSignature());
              }
          }
      
          @Before("hello.aop.order.aop.Pointcuts.orderAndService()")
          public void doBefore(JoinPoint joinPoint) {
              log.info("[before] {}", joinPoint.getSignature());
          }
      
          @AfterReturning(value = "hello.aop.order.aop.Pointcuts.orderAndService()", returning = "result")
          public void doReturn(JoinPoint joinPoint, Object result) {
              log.info("[return] {} return={}", joinPoint.getSignature(), result);
          }
      
          @AfterThrowing(value = "hello.aop.order.aop.Pointcuts.orderAndService()", throwing = "ex")
          public void doThrowing(JoinPoint joinPoint, Exception ex) {
              log.info("[ex] {} message={}", ex);
          }
      
          @After(value = "hello.aop.order.aop.Pointcuts.orderAndService()")
          public void doAfter(JoinPoint joinPoint) {
              log.info("[after] {}", joinPoint.getSignature());
          }
      }

  + 어드바이스 종류
    + @Around: 메서드 호출 전후에 수행, 가장 강력한 어드바이스로 조인 포인트 실행 여부, 반환 값 변환, 예외 값 변환 등 가장 확장적인 기능이다.
    + @Before: 조인 포인트 실행 이전에 실행
    + @AfterReturning: 조인 포인트가 정상 완료 후 실행
    + @AfterThrowing: 메서드가 예외를 던지는 경우 실행
    + @After: 조인 포인트가 정상 또는 예외에 관계없이 실행(finally)

  + @Around를 제외한 나머지 어드바이스들은 @Around의 기능 중 일부 기능을 사용할 뿐이다. 즉, @Around가 모든 기능을 커버칠 수 있다.
  + 모든 어드바이스는 JoinPoint를 파라미터로 사용할 수 있다. 단, @Around는 ProceedJoinPoint를 사용해야 한다.
  + 
  + JoinPoint 인터페이스 주요 기능
    + getArgs(): 메서드 인수 반환
    + getThis(): 프록시 객체 반환
    + getTarget(): 대상 객체 반환
    + getSignature(): 조언되는 메서드에 대한 설면 반환
    + toString(): 조언되는 방법에 대한 유용한 설명 인쇄
  
  + ProceedJoinPoint 인터페이스 주요 기능
    + proceed(): 다음 어드바이스나 타켓 호출
   
  + 테스트 결과

    ![image](https://github.com/ManchanTime/study/assets/127479677/54f8874f-6030-4331-bca7-40a2ee46d5e3)

    ![image](https://github.com/ManchanTime/study/assets/127479677/7ce45fce-ad61-45e0-b7de-732752a9e264)

    각 어드바이스들이 위치에 맞게 실행된다.

  + 여러 어드바이스 존재 이유
    + @Around는 proceed()를 필수적으로 호출해야 한다. 호출하지 않는다면 치명적인 버그로 이어진다.
    + @Around의 기능을 세분화하여 쪼개 이용하면서 제약을 둘 수 있다. 예를 들어 @Before를 통해서 proceed()의 호출에 고민없이 호출 전 로그만 찍을 수 있다.
    + 제약 조건은 일종의 가이드 역할을 한다. 이를 통해 다른 개발자의 코드 분석에 이점을 주고 문제 발생 자체를 줄일 수 있다.
