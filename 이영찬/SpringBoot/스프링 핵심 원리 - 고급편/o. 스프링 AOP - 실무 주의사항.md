# 실무 사용 시 주의사항

### 프록시와 내부 호출

+ 문제
  + 스프링은 프록시 방식의 AOP를 사용한다.
  + AOP를 적용하기 위해 프록시를 통해 Target을 호출해야 한다.
  + 프록시를 거치지 않는다면 AOP가 적용되지 않고 어드바이스 또한 호출되지 않는다.
  + 대상 객체 내부에서 자신의 메서드가 호출될 경우 문제가 발생한다. -> 프록시가 호출되지않고 Target이 바로 호출되기 때문에 AOP가 적용되지 않는다.

  ![image](https://github.com/ManchanTime/study/assets/127479677/dea1ec9c-52ec-436f-8a0c-cd4153febb41)

+ 예시

  + CallLogAspect

        @Slf4j
        @Aspect
        public class CallLogAspect {
        
            @Before("execution(* hello.aop.internalcall..*.*(..))")
            public void doLog(JoinPoint joinPoint) {
                log.info("aop={}", joinPoint.getSignature());
            }
        }

  + CallServiceV0

        @Slf4j
        @Component
        public class CallServiceV0 {
        
            public void external() {
                log.info("call external");
                internal(); //내부 메서드 호출(this.internal())
            }
        
            public void internal() {
                log.info("call internal");
            }
        }

    + external 메서드에서 내부 메서드인 internal을 호출한다.

  + CallServiceV0Test

        @Slf4j
        @Import(CallLogAspect.class)
        @SpringBootTest
        class CallServiceV0Test {
        
            @Autowired
            CallServiceV0 callServiceV0;
        
            @Test
            void external() {
                callServiceV0.external();
            }
        
            @Test
            void internal() {
                callServiceV0.internal();
            }
        }

    + 결과

      ![image](https://github.com/ManchanTime/study/assets/127479677/96eb9872-da0a-446f-9856-83b643f1e59f)

      external을 호출했을 때 AOP가 잘 적용되지만 external 내부의 internal이 호출되면 AOP가 적용되지 않는다.
      물론 internal을 호출하면 AOP가 잘 적용된다.

+ 해결
  + 원초적인 방법은 AspectJ를 직접 이용하는 것이다.
    + AspectJ는 Target에 직접 AOP 적용 코드를 이식하기 때문에 프록시를 사용할 필요가 없고 내부 메서드 호출도 문제가 되지 않는다.
    + AsPectJ를 직접 이용하는 것은 이전에도 작성했던 것처럼 JVM에 옵션을 주거나 로드 타임 위빙(AspectJ 컴파일러 사용) 등 불편하기 때문에 다른 대안을 사용한다.
 
  + 대안1 - 자기 자신 주입
    + Target 클래스에서 자기 자신을 호출하는 것이다. 이 때 자기 자신을 스프링 빈에서 꺼내면 프록시로 호출되기 때문에 AOP가 적용된다.

      + CallServiceV1

            /**
             * 생성자 주입은 순환 사이클을 만들기 때문에 에러 발생 -> setter를 이용하여 해결
             * 이 때 스프링 2.6 버전부터 순환참고 금지 -> yaml이나 properties에 spring.main.allow-circular-references=true로 설정
             */
            @Slf4j
            @Component
            public class CallServiceV1 {
            
                private CallServiceV1 callServiceV1;
            
                @Autowired
                public void setCallServiceV1(CallServiceV1 callServiceV1) {
                    log.info("callServiceV1 setter={}", callServiceV1.getClass());
                    this.callServiceV1 = callServiceV1;
                }
                public void external() {
                    log.info("call external");
                    callServiceV1.internal(); //외부 메서드 호출
                }
            
                public void internal() {
                    log.info("call internal");
                }
            }

      + CallServiceV1Test

            @Slf4j
            @Import(CallLogAspect.class)
            @SpringBootTest
            class CallServiceV1Test {
                @Autowired
                CallServiceV1 callServiceV1;
            
                @Test
                void external() {
                    callServiceV1.external();
                }
            
                @Test
                void internal() {
                    callServiceV1.internal();
                }
            }

        + 결과

          ![image](https://github.com/ManchanTime/study/assets/127479677/b1e64a5d-c970-4e69-a65c-57c47b29035b)

          external을 호출해도 internal까지 AOP가 적용된다.

          ![image](https://github.com/ManchanTime/study/assets/127479677/ab3afc89-39f8-47b9-97ec-699ab3b11162)

  + 대안2 - 지연 조회
    + 이전 대안1은 자기 자신을 생성하여 주입받아 사용한다. 이 방식을 조금 더 간단하게 바꿔보자
    + 스프링 빈을 생성한 이후 자기 자신을 주입 받는 방식이다.

      + CallServiceV2
  
            @Slf4j
            @Component
            public class CallServiceV2 {
            
                //private final ApplicationContext applicationContext;
                public final ObjectProvider<CallServiceV2> callServiceV2ObjectProvider;
            
                public CallServiceV2(ObjectProvider<CallServiceV2> callServiceV2ObjectProvider) {
                    this.callServiceV2ObjectProvider = callServiceV2ObjectProvider;
                }
            
            
                public void external() {
                    log.info("call external");
                    CallServiceV2 bean = callServiceV2ObjectProvider.getObject();
                    bean.internal(); //외부 메서드 호출
                }
            
                public void internal() {
                    log.info("call internal");
                }
            }
   
        + ObjectProvider나 ApplicationContext를 사용하는데 ApplicationContext는 지나치게 많은 기능을 제공한다.
        + 때문에 스프링 빈 생성 시점이 아닌 실제 객체를 사용하는 시점으로 지연할 수 있는 ObjectProvider를 주로 사용한다.
        + callServiceV2Provider.getObject()를 호출하는 시점에서 자기 자신이 아닌 스프링 빈에 등록되어있는 프록시를 조회하기 때문에 순환 참조가 발생하지 않는다.

      + CallServiceV2Test
   
            @Slf4j
            @Import(CallLogAspect.class)
            @SpringBootTest
            class CallServiceV2Test {
                @Autowired
                CallServiceV2 callServiceV2;
            
                @Test
                void external() {
                    callServiceV2.external();
                }
            
                @Test
                void internal() {
                    callServiceV2.internal();
                }
            }

        + 결과

          ![image](https://github.com/ManchanTime/study/assets/127479677/e5c5aaa5-ff4c-4fb8-8561-49ab9e10a8d1)

          이전과 마찬가지로 external, internal 모두 잘 된다.

  + 대안3 - 구조 변경
    + 대안1은 자기 자신을 주입하고 대안2는 Provider 사용하는 것 처럼 조금 어색한 구조다.
    + 가장 권장하는 방법은 구조 자체를 AOP가 적용되는 내부 메서드를 호출하지 않도록 바꾸는 것이다.

      + CallServiceV3

            @Slf4j
            @Component
            @RequiredArgsConstructor
            public class CallServiceV3 {
            
                private final InternalService internalService;
            
                public void external() {
                    log.info("call external");
                    internalService.internal(); //외부 메서드 호출
                }
            }

      + InternalService

            @Slf4j
            @Component
            public class InternalService {
                public void internal() {
                    log.info("call internal");
                }
            }

        + internal 메서드를 InternalService라는 컴포넌트 스캔에 대상이되는 클래스로 분리하여 따로 생성하고 external에서 호출한다.

      + CallServiceV3Test

            @Slf4j
            @Import(CallLogAspect.class)
            @SpringBootTest
            class CallServiceV3Test {
            
                @Autowired
                CallServiceV3 callServiceV3;
                @Test
                void external() {
                    callServiceV3.external();
                }
            }

        + 결과

          ![image](https://github.com/ManchanTime/study/assets/127479677/76b2bb35-23a0-4a62-9c70-6e899b28884b)

          ![image](https://github.com/ManchanTime/study/assets/127479677/eb658845-00e7-43b9-976f-1d7aaeb36108)

          + 내부 호출 자체가 사라지고 callService에서 internalService를 호출하는 구조로 변경되어 자연스럽게 AOP가 적용된다.

+ 정리
  + AOP는 주로 트랜잭션 적용이나 주요 컴포넌트 로그 출력에 사용된다.
  + private 메서드 자체는 AOP를 적용할 수 없고 이 때문에 private을 public으로 바꾸는 작업은 비효율적이기 때문에 거의 없다.
  + 하지만 위 방식처럼 내부 메서드를 호출하는 문제는 발생할 수 있다.
  + 이를 해결하는 것은 주로 V3처럼 구조를 변경하는 것이 좋다.
 
### 프록시 기술과 한계

+ 타입 캐스팅

  + 프록시를 생성하는 기술
  
    + JDK 동적 프록시
      + 인터페이스가 필수
        
    + CGLIB
      + 구체 클래스를 상속받아 프록시 생성
     
  + JDK 동적 프록시 vs CGLIB
  
    + JDK 동적 프록시
  
          @Test
          void jdkProxy() {
              MemberServiceImpl target = new MemberServiceImpl();
              ProxyFactory proxyFactory = new ProxyFactory(target);
              proxyFactory.setProxyTargetClass(false); //JDK 동적 프록시
      
              //프록시를 인터페이스로 캐스팅 성공
              MemberService memberServiceProxy = (MemberService) proxyFactory.getProxy();
      
              //JDK 동적 프록시를 구현 클래스로 캐스팅 시도 실패 -> ClassCastException 발생
              assertThrows(ClassCastException.class, () ->  {
                  MemberServiceImpl castingMemberService = (MemberServiceImpl) memberServiceProxy;
              });
          }
  
      + JDK 동적 프록시 구조
  
        ![image](https://github.com/ManchanTime/study/assets/127479677/dee7b92e-efef-4c34-ba8f-4bf127fe8190)
  
        + 위 구조에서 볼 수 있는 것처럼 프록시를 MemberService라는 인터페이스를 구현체로 동적 생성한다.
        + 이 때 MemberServiceImpl 타입으로 스프링 빈에 등록된 프록시를 가져올 때 MemberService의 구현체인 프록시는 MemberServiceImpl을 알지 못하기 때문에 타입 캐스팅 예외가 발생한다.
  
          ![image](https://github.com/ManchanTime/study/assets/127479677/c712d3d5-17a3-4d03-837e-b65ed5e61392)
  
    + CGLIB
  
          @Test
          void cglibProxy() {
              MemberServiceImpl target = new MemberServiceImpl();
              ProxyFactory proxyFactory = new ProxyFactory(target);
              proxyFactory.setProxyTargetClass(true); //CGLIB 프록시
      
              //프록시를 인터페이스로 캐스팅 성공
              MemberService memberServiceProxy = (MemberService) proxyFactory.getProxy();
      
              log.info("proxy class={}", memberServiceProxy.getClass());
      
              //CGLIB 동적 프록시를 구현 클래스로 캐스팅 시도 성공
              MemberServiceImpl castingMemberService = (MemberServiceImpl) memberServiceProxy;
          }
  
      + CGLIB 구조
  
        ![image](https://github.com/ManchanTime/study/assets/127479677/5274e21f-2d8c-47f3-ac25-25ba35b63f90)
  
        + 타겟 구현체를 상속받는 형태로 Proxy를 생성한다.
        + MemberServiceImpl을 기반으로 Proxy를 생성하기 때문에 프록시를 MemberServiceImpl로 캐스팅하거나 MemberService로 캐스팅해도 문제가 발생하지 않는다.
  
          ![image](https://github.com/ManchanTime/study/assets/127479677/7209768e-e863-49e7-8297-592410e7c8ad)

+ 의존관계 주입

  + JDK 동적 프록시 사용 시 의존관계 주입 문제 발생

    + ProxyDIAspect

          @Slf4j
          @Aspect
          public class ProxyDIAspect {
          
              @Before("execution(* hello.aop..*.*(..))")
              public void doTrace(JoinPoint joinPoint) {
                  log.info("[proxyDIAdvice] {}", joinPoint.getSignature());
              }
          }

    + ProxyDITest

          @Slf4j
          @SpringBootTest(properties = {"spring.aop.proxy-target-class=false"})
          @Import(ProxyDIAspect.class)
          public class ProxyDITest {
          
              @Autowired
              MemberService memberService;
          
              @Autowired
              MemberServiceImpl memberServiceImpl;
          
              @Test
              void go() {
                  log.info("memberService class={}", memberService.getClass());
                  log.info("memberServiceImpl class={}", memberServiceImpl.getClass());
                  memberServiceImpl.hello("hello");
              }
          }

      + false로 설정하여 JDK 동적 프록시 사용 가능
      + 스프링 빈에 등록된 프록시 조회 시 MemberService 타입, MemberServiceImpl 타입으로 각각 주입
      + 결과
        + MemberServiceImpl로 캐스팅할 수 있는 타입이 없다는 에러 발생
        + 위 타입 캐스팅 문제와 마찬가지로 MemberServiceImpl로 가져올 때 MemberService 기반으로 만들어진 프록시는 MemberServiceImpl을 모르기 때문에 가져올 수 없다.
       
  + CGLIB

    + ProxyDITest

          @Slf4j
          @SpringBootTest(properties = {"spring.aop.proxy-target-class=true"})
          @Import(ProxyDIAspect.class)
          public class ProxyDITest {
          
              @Autowired
              MemberService memberService;
          
              @Autowired
              MemberServiceImpl memberServiceImpl;
          
              @Test
              void go() {
                  log.info("memberService class={}", memberService.getClass());
                  log.info("memberServiceImpl class={}", memberServiceImpl.getClass());
                  memberServiceImpl.hello("hello");
              }
          }

      + true로 설정하여 CGLIB만 사용하도록 변경
      + 결과
        + 정상적으로 잘 동작한다.
        + 이 또한 위 타입 캐스팅 문제와 같이 MemberServiceImpl을 기반으로 생성된 프록시이기 때문에 MemberService나 MemberServiceImpl로 의존 관계를 주입 받을 때 문제가 발생하지 않는다.
        + 

    + CGLIB의 한계

      + 위 문제만 놓고 보면 JDK 동적 프록시보다 CGLIB가 무조건 좋아보인다. 하지만 문제가 있다.
        + 대상 클래스에 기본 생성자 필수
        + 생성자 2번 호출 문제
        + final 키워드 클래스, 메서드 사용 불가
     
      + 대상 클래스에 기본 생성자 필수
        + CGLIB는 구체 클래스를 상속받기 때문에 자바의 상속 조건을 따른다.
          + 자식 클래스 생성 시 부모 클래스의 생성자 또한 호출해야 한다.
          + CGLIB가 생성하는 프록시의 생성자는 우리가 호출하는 것이 아니다. 따라서 대상 클래스에 기본 생성자가 반드시 있어야 한다.
         
      + 생성자 2번 호출 문제
        + 실제 Target을 호출할 때 생성자를 호출한다.
        + 프록시 객체를 생성할 때 부모 클래스의 생성자를 호출해야하고 이 부모 클래스의 생성자가 실제 Target의 생성자이다.

          ![image](https://github.com/ManchanTime/study/assets/127479677/095f93d3-8210-4a4f-85b1-3d3af6975433)

      + final 키워드 클래스, 메서드 사용 불가
        + final 키워드는 상속이 불가능하고 메서드 오버라이딩 또한 불가능하다. CGLIB가 상속을 기반으로 하기 때문에 앞선 경우 정상 동작하지 않는다.
        + 이 경우는 사실 final 키워드 자체를 잘 사용하지 않기 때문에 문제가 될 일은 거의 없다.

    + CGLIB 한계 극복
      
      + 스프링 3.2, 스프링 내부에 CGLIB 함께 패키징
        + CGLIB 별도 라이브러리를 스프링 내부에 넣는 것으로 라이브러리 추가없이 사용 가능

      + CGLIB 기본 생성자 필수 문제 해결
        + 스프링 4.0부터 objenesis라는 특별한 라이브러리를 사용해서 기본 생성자 없이 객체 생성 가능
        + 생성자 호출 없이 객체를 생성할 수 있게 해줌
       
      + 생성자 2번 호출 문제
        + 스프링 4.0부터 objenesis 라이브러리를 이용하여 해결
       
      + 스프링부트 2.0부터 CGLIB를 기본으로 사용
        + 앞선 2가지 문제를 해결했기 때문에 인터페이스가 있더라도 CGLIB를 이용하여 구체클래스 기반 프록시를 생성한다.
        + final 문제는 자주 사용하지 않기 때문에 문제가 되지 않는다.
