# 포인트컷

### 포인트컷 지시자

+ 포인트컷 표현식은 execution과 같은 포인트컷 지시자(Pointcut Designator) 즉 PCD로 시작한다.

+ 포인트컷 지시자 종류
  + execution: 메서드 실행 조인 포인트를 매칭한다. 가장 많이 사용하고 기능도 복잡하다.
  + within: 특정 타입 내의 조인 포인트를 매칭한다.
  + args: 인자가 주어진 타입의 인스터인인 조인 포인트
  + this: 스프링 빈 객체(프록시)를 대상으로 하는 조인 포인트
  + target: Target 객체(실제 대상)를 대상으로 하는 조인 포인트
  + @target: 실행 객체의 클래스에 주어진 타입의 어노테이션이 있는 조인 포인트
  + @within: 주어진 어노테이션이 있는 타입 내 조인 포인트
  + @annotation: 메서드가 주어진 어노테이션을 가지고 있는 조인 포인트
  + @args: 전달된 실제 인수의 런타임 타입이 주어진 타입의 어노테이션을 갖는 조인 포인트
  + bean: 스프링 전용 포인트컷 지시자로 빈의 이름을 포인트컷으로 지정
 
+ 테스트에 사용할 기본 클래스
  + ClassAop
    
        @Target(ElementType.TYPE)
        @Retention(RetentionPolicy.RUNTIME)
        public @interface ClassAop {
        }

  + MethodAop

        @Target(ElementType.METHOD)
        @Retention(RetentionPolicy.RUNTIME)
        public @interface MethodAop {
            String value();
        }

  + MemberService

        public interface MemberService {
            String hello(String param);
        }

  + MemberServiceImpl

        @ClassAop
        @Component
        public class MemberServiceImpl implements MemberService {
            @Override
            @MethodAop("test value")
            public String hello(String param) {
                return "ok";
            }
        
            public String internal(String param) {
                return "ok";
            }
        }

+ execution
  + 가장 많이 사용하고 복잡한 기능이 많은 지시자자

  + 매칭 조건
    + 접근제어자?: public
    + 반환타입?: String
    + 선언타입?: hello.aop.member.MemberServiceImpl
    + 메서드이름?: hello
    + 파라미터?: (String)
    + 예외?: 생략
    + ? 생략 가능, 패턴을 *로 치환할 수 있고 ..을 통해서 여러 타입의 값을 받을 수 있다.
      
  + ExecutionTest 기본 설정

        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        Method helloMethod;
    
        @BeforeEach
        public void init() throws NoSuchMethodException {
            helloMethod = MemberServiceImpl.class.getMethod("hello", String.class);
        }

    + MemberService에 있는 hello 메서드를 가져옴

    + 생략없는 execution
  
          @Test
          void exactMatch() {
              //public java.lang.String hello.aop.member.MemberServiceImpl.hello(java.lang.String)
              pointcut.setExpression("execution(public String hello.aop.member.MemberServiceImpl.hello(String))");
              assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
          }

    + 모두 생략

          @Test
          void allMatch() {
              pointcut.setExpression("execution(* *(..))");
              assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
          }

    + 함수 이름만 매칭

          @Test
          void nameMatch() {
              pointcut.setExpression("execution(* hello(..))");
              assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
          }

    + 함수 이름 패턴으로 매칭

          @Test
          void nameMatchStar1() {
              pointcut.setExpression("execution(* hel*(..))");
              assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
          }
      
          @Test
          void nameMatchStar2() {
              pointcut.setExpression("execution(* *el*(..))");
              assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
          }

    + 이름이 존재하지 않을 경우

          @Test
          void nameMatchFalse() {
              pointcut.setExpression("execution(* nono(..))");
              assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isFalse();
          }

    + 패키지 정확하게 매칭

          @Test
          void packageExactMatch1() {
              pointcut.setExpression("execution(* hello.aop.member.MemberServiceImpl.hello(..))");
              assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
          }

    + 패키지 클래스 이름 생략

          @Test
          void packageExactMatch2() {
              pointcut.setExpression("execution(* hello.aop.member.*.*(..))");
              assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
          }

    + 패키지 이름 틀렸을 경우

          @Test
          void packageExactFalse() {
              pointcut.setExpression("execution(* hello.aop.*.*(..))");
              assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isFalse();
          }

        aop 패키지 밑에 참조할 패키지가 없다.

    + 하위 패키지까지 설정

            @Test
            void packageMatchSubPackage1() {
                pointcut.setExpression("execution(* hello.aop.member..*.*(..))");
                assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
            }
        
            @Test
            void packageMatchSubPackage2() {
                pointcut.setExpression("execution(* hello.aop..*.*(..))");
                assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
            }

    + 참조 클래스 타입 매칭

            @Test
            void typeExactMatch() {
                pointcut.setExpression("execution(* hello.aop.member.MemberServiceImpl.*(..))");
                assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
            }
        
            @Test
            void typeMatchSuperType() {
                pointcut.setExpression("execution(* hello.aop.member.MemberService.*(..))");
                assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
            }
        
            @Test
            void typeMatchInternal() throws NoSuchMethodException {
                pointcut.setExpression("execution(* hello.aop.member.MemberServiceImpl.*(..))");
                Method internalMethod = MemberServiceImpl.class.getMethod("internal", String.class);
                assertThat(pointcut.matches(internalMethod, MemberServiceImpl.class)).isTrue();
            }
        
            @Test
            void typeMatchNoSuperTypeMethodFalse() throws NoSuchMethodException {
                pointcut.setExpression("execution(* hello.aop.member.MemberService.*(..))");
                Method internalMethod = MemberServiceImpl.class.getMethod("internal", String.class);
                assertThat(pointcut.matches(internalMethod, MemberServiceImpl.class)).isFalse();
            }
      
      이 때 MemberService 존재하는 메서드만 가져올 수 있음. MemberServiceImpl의 internal 메서드는 포인트컷 대상 적용 불가

    + 파라미터

            @Test
            void argsMatch() {
                pointcut.setExpression("execution(* *(String))");
                assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
            }
        
            //파라미터가 없어야 함
            //()
            @Test
            void argsMatchNoArgs() {
                pointcut.setExpression("execution(* *())");
                assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isFalse();
            }
        
            //정확히 하나의 파라미터 허용, 모든 타입 허용
            //(Xxx)
            @Test
            void argsMatchStar() {
                pointcut.setExpression("execution(* *(*))");
                assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
            }
        
            //숫자와 무관하게 모든 파라미터, 모든 타입 허용
            //(), (Xxx), (Xxx, Xxx)
            @Test
            void argsMatchAll() {
                pointcut.setExpression("execution(* *(..))");
                assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
            }
        
            //String 타입으로 시작하고 숫자와 무관하게 모든 파라미터, 모든 타입 허용
            //(String), (String, Xxx), (String, Xxx, Xxx)
            @Test
            void argsMatchComplex() {
                pointcut.setExpression("execution(* *(String, ..))");
                assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
            }

+ within
  + 특정 타입 내의 조인포인트들로 매칭을 제한함 -> 해당 타입이 매칭되면 그 안의 메서드들이 자동으로 매칭됨
  + 단순한 문법으로 execution의 type 부분만 사용한다고 보면 됨
 
  + WithinTest 기본 설정

        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        Method helloMethod;
    
        @BeforeEach
        public void init() throws NoSuchMethodException {
            helloMethod = MemberServiceImpl.class.getMethod("hello", String.class);
        }

    + 정확하게 매칭

          @Test
          void withinExact() {
              pointcut.setExpression("within(hello.aop.member.MemberServiceImpl)");
              assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
          }

    + 타입 이름 패턴 적용

          @Test
          void withinStar() {
              pointcut.setExpression("within(hello.aop.member.*Service*)");
              assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
          }

    + SubPackage 생략

          @Test
          void withinSubPackage() {
              pointcut.setExpression("within(hello.aop..*)");
              assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
          }

    + 상속받은 인터페이스 타입 적용

          @Test
          @DisplayName("타겟의 타입에만 직접 적용, 인터페이스를 선정하면 안된다.")
          void withinSuperTypeFalse() {
              pointcut.setExpression("within(hello.aop.member.MemberService)");
              assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isFalse();
          }

      타겟을 확실하게 넣어야 함 -> 인터페이스 선정 x

+ args
  + 인자가 주어진 타입의 인스턴스인 조인 포인트로 매칭
  + 기본 문법은 execution의 args와 같음
  + execution과 다르게 클래스의 선언된 정보를 기반으로 판단하기 때문에 부모 타입까지 허용 -> 실제 넘어온 파라미터 객체 인스턴스를 보고 판단함
 
  + ArgsTest 기본 설정

        Method helloMethod;
    
        @BeforeEach
        public void init() throws NoSuchMethodException {
            helloMethod = MemberServiceImpl.class.getMethod("hello", String.class);
        }
    
        private AspectJExpressionPointcut pointcut(String expression) {
            AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
            pointcut.setExpression(expression);
            return pointcut;
        }

      + args 여러 타입 매칭 테스트

            @Test
            void args() {
                //hello(String)과 매칭
                assertThat(pointcut("args(String)").matches(helloMethod, MemberServiceImpl.class)).isTrue();
                assertThat(pointcut("args(Object)").matches(helloMethod, MemberServiceImpl.class)).isTrue();
                assertThat(pointcut("args()").matches(helloMethod, MemberServiceImpl.class)).isFalse();
                assertThat(pointcut("args(..)").matches(helloMethod, MemberServiceImpl.class)).isTrue();
                assertThat(pointcut("args(*)").matches(helloMethod, MemberServiceImpl.class)).isTrue();
                assertThat(pointcut("args(String, ..)").matches(helloMethod, MemberServiceImpl.class)).isTrue();
            }

      + execution과 비교

            @Test
            void argsVsExecution() {
                //Args
                assertThat(pointcut("args(String)").matches(helloMethod, MemberServiceImpl.class)).isTrue();
                assertThat(pointcut("args(java.io.Serializable)").matches(helloMethod, MemberServiceImpl.class)).isTrue();
                assertThat(pointcut("args(Object)").matches(helloMethod, MemberServiceImpl.class)).isTrue();
        
                //Execution
                assertThat(pointcut("execution(* *(String))").matches(helloMethod, MemberServiceImpl.class)).isTrue();
                assertThat(pointcut("execution(* *(java.io.Serializable))").matches(helloMethod, MemberServiceImpl.class)).isFalse();
                assertThat(pointcut("execution(* *(Object))").matches(helloMethod, MemberServiceImpl.class)).isFalse();
            }

          정확한 타입을 넣어주지 않아도 args는 해당 타입이 파라미터 타입의 부모클래스가 성립한다면 포인트컷 매칭으로 인정

+ @target, @within
  + @target: 실행 객체의 클래스에 주어진 타입의 어노테이션이 있는 조인 포인트
  + @within: 주어진 어노테이션이 있는 타입 내 조인 포인트
  + 둘다 타입에 있는 어노테이션으로 AOP 적용 여부를 판단한다.
  + 이 두 포인트컷 지시자는 단독으로 사용할 수 없다.
    + 프록시가 있어야 판별가능한 지시자이지만 해당 조건만으로는 모든 스프링 빈에 AOP 적용을 시도하게되고 이 때문에 프록시를 생성할 수 없는 클래스에 대해서 에러가 발생한다.

  + @target vs @within
    + @target은 인스턴스의 모든 메서드를 조인 포인트로 적용한다. -> 부모 클래스까지 모두 적용
    + @within은 해당 타입 내에 있는 메서드만 조인 포인트로 적용한다. -> 당사자만 적용
   
      ![image](https://github.com/ManchanTime/study/assets/127479677/ddfc8a40-b9a0-486e-bcfc-105088b342ea)

  + AtTargetWithinTest

    + 기본 설정

          @Autowired
          Child child;
      
          @Test
          void success() {
              log.info("child Proxy={}", child.getClass());
              child.childMethod(); //부모, 자식 모두 있는 메서드
              child.parentMethod(); //부모 클래스만 있는 메서드
          }
      
          static class Config {
      
              @Bean
              public Parent parent() {
                  return new Parent();
              }
      
              @Bean
              public Child child() {
                  return new Child();
              }
      
              @Bean
              public AtTargetWithinAspect atTargetWithinAspect() {
                  return new AtTargetWithinAspect();
              }
          }
      
          static class Parent {
              public void parentMethod() {} //부모에만 있는 메서드
          }
      
          @ClassAop
          static class Child extends Parent {
              public void childMethod(){}
          }

      @within과 @target의 차이점을 확인하기 위해서 Parent 클래스를 생성하여 Child 클래스가 상속받도록하고 모두 스프링 빈에 등록한다.

      + 차이 비교 테스트

            @Slf4j
            @Aspect
            static class AtTargetWithinAspect {
        
                //@target: 인스턴스 기준으로 모든 메서드의 조인 포인트를 선정, 부모 타입 메서드도 적용
                @Around("execution(* hello.aop..*(..)) && @target(hello.aop.member.annotation.ClassAop)")
                public Object atTarget(ProceedingJoinPoint joinPoint) throws Throwable {
                    log.info("[@target] {}", joinPoint.getSignature());
                    return joinPoint.proceed();
                }
        
                //@within: 선택된 클래스 내부에 있는 메서드만 조인 포인트로 선정, 부모 타입의 메서드는 적용되지 않음
                @Around("execution(* hello.aop..*(..)) && @within(hello.aop.member.annotation.ClassAop)")
                public Object atWithin(ProceedingJoinPoint joinPoint) throws Throwable {
                    log.info("[@within] {}", joinPoint.getSignature());
                    return joinPoint.proceed();
                }
            }

          ![image](https://github.com/ManchanTime/study/assets/127479677/5af0c3d6-4f2d-443a-9b14-9614b37599f6)

          @target은 Parent 클래스에는 적용되지 않는다.

 + @annotation
   + 메서드가 주어진 어노테이션을 가지고 있는 조인 포인트를 매칭
     + 예시를 위해 생성해둔 @MethodAop가 있는 메서드에만 조인 포인트를 매칭 -> MemberServiceImpl의 hello 메서드만 매칭
    
    + AtAnnotationTest

          @Slf4j
          @Import(AtAnnotationTest.AtAnnotationAspect.class)
          @SpringBootTest
          public class AtAnnotationTest {
          
              @Autowired
              MemberService memberService;
          
              @Test
              void success() {
                  log.info("memberService Proxy={}", memberService.getClass());
                  memberService.hello("helloA");
              }
          
              @Slf4j
              @Aspect
              static class AtAnnotationAspect {
          
                  @Around("@annotation(hello.aop.member.annotation.MethodAop)")
                  public Object doAtAnnotation(ProceedingJoinPoint joinPoint) throws Throwable {
                      log.info("[@annotation] {}", joinPoint.getSignature());
                      return joinPoint.proceed();
                  }
              }
          }

      ![image](https://github.com/ManchanTime/study/assets/127479677/f8fa91e1-e2da-4c9a-b8a1-1214942cf567)

      hello에만 적용된다.

+ @args
  + 전달된 실제 인수의 런타임 타입이 주어진 타입의 어노테이션을 갖는 조인 포인트
  + 전달된 인수의 런타임 타입에 @Check 어노테이션이 있는 경우 매칭한다.
  + @args(test.Check)
 
+ bean
  + 스프링 전용 포인트컷 지시자로 빈의 이름으로 지정한다.
  + bean(orderService) || bean(*Repository) 처럼 * 패턴을 사용할 수 있고 || &&와 같은 연산자도 사용가능하다.
 
  + BeanTest

        @Slf4j
        @Import(BeanTest.BeanAspect.class)
        @SpringBootTest
        public class BeanTest {
        
            @Autowired
            OrderService orderService;
        
            @Test
            void success() {
                orderService.orderItem("itemA");
            }
        
            @Aspect
            static class BeanAspect {
                @Around("bean(orderService) || bean(*Repository)")
                public Object doLog(ProceedingJoinPoint joinPoint) throws Throwable {
                    log.info("[bean] {}", joinPoint.getSignature());
                    return joinPoint.proceed();
                }
            }
        }

    ![image](https://github.com/ManchanTime/study/assets/127479677/33598be9-746e-4b3a-a283-6822f62662d8)

+ 매개변수 전달
  + this, target, args, @target, @within, @annotation, @args와 같은 지시자를 이용하여 어드바이스에 매개변수 전달 가능
  + 포인트컷의 이름과 매개변수의 이름을 맞추어야 한다.
  + 타입이 메서드에 지정한 타입으로 제한된다.
 
  + ParameterTest

        @Slf4j
        @Import(ParameterTest.ParameterAspect.class)
        @SpringBootTest
        public class ParameterTest {
        
            @Autowired
            MemberService memberService;
        
            @Test
            void success() {
                log.info("memberService Proxy={}", memberService.getClass());
                memberService.hello("helloA");
            }
        
            @Slf4j
            @Aspect
            static class ParameterAspect {
                @Pointcut("execution(* hello.aop.member..*(..))")
                private void allMember() {}
        
                @Around("allMember()")
                public Object logArgs1(ProceedingJoinPoint joinPoint) throws Throwable {
                    Object arg1 = joinPoint.getArgs()[0];
                    log.info("[logArgs1]{}, arg={}", joinPoint.getSignature(), arg1);
                    return joinPoint.proceed();
                }
        
                @Around("allMember() && args(arg,..)")
                public Object logArgs2(ProceedingJoinPoint joinPoint, String arg) throws Throwable {
                    log.info("[logArgs2]{}, arg={}", joinPoint.getSignature(), arg);
                    return joinPoint.proceed();
                }
        
                @Before("allMember() && args(arg,..)")
                public void logArgs3(String arg) {
                    log.info("[logArgs3] arg={}", arg);
                }
        
                @Before("allMember() && this(obj)")
                public void thisArgs(JoinPoint joinPoint, MemberService obj) {
                    log.info("[this]{}, obj={}", joinPoint.getSignature(), obj.getClass());
                }
        
                @Before("allMember() && target(obj)")
                public void targetArgs(JoinPoint joinPoint, MemberService obj) {
                    log.info("[target]{}, obj={}", joinPoint.getSignature(), obj.getClass());
                }
        
                @Before("allMember() && @target(annotation)")
                public void atTarget(JoinPoint joinPoint, ClassAop annotation) {
                    log.info("[@target]{}, obj={}", joinPoint.getSignature(), annotation);
                }
        
                @Before("allMember() && @within(annotation)")
                public void atWithin(JoinPoint joinPoint, ClassAop annotation) {
                    log.info("[@within]{}, obj={}", joinPoint.getSignature(), annotation);
                }
        
                @Before("allMember() && @annotation(annotation)")
                public void atAnnotation(JoinPoint joinPoint, MethodAop annotation) {
                    log.info("[@annotation]{}, annotationValue={}", joinPoint.getSignature(), annotation.value());
                }
            }
        }

    + logArgs1: joinPoint.getArgs[0]와 같이 매개변수를 배열로 직접 받는다.
    + logArgs2: args(arg,..)와 같이 매개변수를 받는다.
    + logArgs3: @Before를 사용하여 축약버전으로 타입을 String으로 제한하여 받는다.
    + this: 프록시 객체를 전달받는다.
    + target: 실제 대상 객체를 전달 받는다.
    + @target, @within: 타입의 어노테이션을 받는다.(ClassAop)
    + @annotation: 메서드의 어노테이션을 전달 받는다. 이 떄 Method 어노테이션의 value와 같은 메서드를 이용하여 값을 출력할 수 있다.(MethAop)
   
+ this, target
  + this: 스프링 빈 객체(AOP 프록시)를 대상으로 하는 조인 포인트
  + target: Target 객체(AOP 프록시가 가리키는 실제 대상)를 대상으로 하는 조인 포인트
  + 적용 타입 하나를 정확하게 지정해야 한다. -> 생략 불가, 부모 타입은 허용
 
  + 프록시 생성 방식에 따른 this와 target의 차이
    + 스프링은 프록시 생성 시 조건에 따라 JDK와 CGLIB 방식으로 나뉨

    + JDK 방식 기준
      
      ![image](https://github.com/ManchanTime/study/assets/127479677/978285b8-c15d-41bb-82e3-b3bdcade8050)
      
      + MemberService 인터페이스 지정
        + this: proxy 객체를 보고 판단 -> 부모 타입이 허용되기 때문에 AOP 적용
        + target: 마찬가지로 aOP 적용
      + MemberServiceImpl 구체 클래스 지정
        + this: JDK 동적으로 만들어진 객체는 MemberService를 기반으로 구현되기 때문에 MemberServiceImpl에 대해 알지 못해 AOP 적용 대상이 아니다.
        + target: 실제 대상 객체가 MemberServiceImpl이기 때문에 AOP 적용 대상이다.
         
    + CGLIB 기준

      ![image](https://github.com/ManchanTime/study/assets/127479677/8d29e760-e7f1-4741-a436-5baceb661a61)

      + MeberService 인터페이스 지정
        + this: JDK와 마찬가지로 AOP 적용
        + target: 마찬가지로 AOP 적용
      + MemberServiceImpl 구체 클래스 지정
        + this: CGLIB는 MemberServiceImpl을 상속받아 프록시를 구현하기 때문에 AOP 적용 대상
        + target: JDK와 같은 이유로 AOP 적용 대상
       
    + 정리
      + 프록시를 대상으로 하는 this의 경우 JDK와 CGLIB 지정 타입에 따라 AOP 적용 여부가 다름

  + ThisTargetTest

        /**
         * application.properties
         * spring.aop.proxy-target-class=true CGLIB
         * spring.aop.proxy=target-class=false JDK 동적 프록시
         */
        @Slf4j
        @Import(ThisTargetTest.ThisTargetAspect.class)
        @SpringBootTest(properties = "spring.aop.proxy-target-class=true")
        public class ThisTargetTest {
        
            @Autowired
            MemberService memberService;
        
            @Test
            void success() {
                log.info("memberService Proxy={}", memberService.getClass());
                memberService.hello("helloA");
            }
        
            @Slf4j
            @Aspect
            static class ThisTargetAspect {
        
                //부모 타입 허용
                @Around("this(hello.aop.member.MemberService)")
                public Object doThisInterface(ProceedingJoinPoint joinPoint) throws Throwable {
                    log.info("[this-interface] {}", joinPoint.getSignature());
                    return joinPoint.proceed();
                }
        
                //부모 타입 허용
                @Around("target(hello.aop.member.MemberService)")
                public Object doTargetInterface(ProceedingJoinPoint joinPoint) throws Throwable {
                    log.info("[target-interface] {}", joinPoint.getSignature());
                    return joinPoint.proceed();
                }
        
                //부모 타입 허용
                @Around("this(hello.aop.member.MemberServiceImpl)")
                public Object doThis(ProceedingJoinPoint joinPoint) throws Throwable {
                    log.info("[this-impl] {}", joinPoint.getSignature());
                    return joinPoint.proceed();
                }
        
                //부모 타입 허용
                @Around("target(hello.aop.member.MemberServiceImpl)")
                public Object doTarget(ProceedingJoinPoint joinPoint) throws Throwable {
                    log.info("[target-impl] {}", joinPoint.getSignature());
                    return joinPoint.proceed();
                }
            }
        }

    + CGLIB로 강제했을 때

      ![image](https://github.com/ManchanTime/study/assets/127479677/c684e2d0-755d-49fe-a20e-97dba8c9ced4)
 
      타입을 MemberService나 MeberServiceImpl 아무거나 설정해도 모두 AOP 적용대상이 된다.

    + JDK CGLIB 자동 선택으로 설정했을 때

      ![image](https://github.com/ManchanTime/study/assets/127479677/92434c5b-2804-4ec0-975a-6abe28d1f2db)

      타입을 MemberServiceImpl로 설정했을 때 JDK로 생성되는 [this-impl]은 AOP 적용대상이 아니게 된다.
