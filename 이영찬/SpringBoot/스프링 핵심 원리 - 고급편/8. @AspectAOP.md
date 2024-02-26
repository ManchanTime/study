# @Aspect AOP 적용

+ 이전 AOP 라이브러리를 받으면서 Advisor를 스프링 빈에 등록하면 자동 프록시 생성기가 프록시 생성 처리를 알아서 해줬다.
+ @Aspect 애노테이션을 이용하면 Advisor 클래스를 따로 만들 필요없이 매우 편리하게 포인트컷과 Advice로 구성되어있는 Advisor 생성 기능을 제공해준다.

### 설명

+ 자동 프록시 생성기는 Advisor를 자동으로 찾아 프록시 생성 유무를 판별한다.
+ 추가로 @Aspect를 찾아 Advisor로 변환하는 작업 또한 진행한다.

![image](https://github.com/ManchanTime/study/assets/127479677/1b58104b-005b-42c1-9fd8-77a9d5cf4917)

+ @Aspect To Advisor

  ![image](https://github.com/ManchanTime/study/assets/127479677/5a5bd6ad-44c0-4b9c-ab5a-da99817be307)

  1. 실행: 스프링 어플리케이션 로딩 시점에 자동 프록시 생성기 호출
  2. 모든 @Aspect 빈 조회: 자동 프록시 생성기가 스프링 컨테이너에서 @Aspect 어노테이션이 존재하는 스프링 빈 모두 조회
  3. Advisor 생성: @Aspect에 존재하는 Advisor 빌더를 통해 @Aspect 어노테이션 정보를 기반으로 Advisor 생성
  4. @Aspect 기반 Advisor 저장: 생성한 Advisor를 @Aspect Advisor 빌드 내부에 저장

  + @Aspect Advisor 빌더

    BeanFactoryAspectJAdvisorBuilder 클래스로 @Aspect 정보를 기반으로 포인트컷, Advice, Advisor를 생성하고 보관한다.

+ Advisor 기반 프록시 생성

  ![image](https://github.com/ManchanTime/study/assets/127479677/a967ef02-9654-4391-a3d6-a0afe46b4b5f)

  + 이전 Adsior를 생성했던 것처럼 스프링 컨테이너 자체 Advisor도 조회하지만 @Aspect Advisor 또한 조회하여 빈 후처리기에서 프록시 적용 대상 체크에 반영한다.
 
  1. 생성: 스프링 빈 대상이 되는 객체 생성
  2. 전달: 생성된 객체를 빈에 등록하기 전에 빈 후처리기에 전달한다.
  3. Advisor 조회 + @Aspect Advisor 조회: 스프링 컨테이너에서 Advisor 빈 모두 조회 + @Aspect Advisor 빌드 내부 저장 Advisor 모두 조회
  4. 프록시 적용 대상 체크: Adsivor에 포함되어 있는 포인트컷을 사용해서 해당 객체 프록시 적용 여부 판별, 이 때 객체 클래스 정보 + 객체 모든 메서드를 포인트컷에 매칭하여 하나라도 해당되면 프록시 생성

### 적용

+ LogTraceAspect

      @Slf4j
      @Aspect
      public class LogTraceAspect {
      
          private final LogTrace logTrace;
      
          public LogTraceAspect(LogTrace logTrace) {
              this.logTrace = logTrace;
          }
      
          @Around("execution(* hello.proxy.app..*(..))")
          public Object execute(ProceedingJoinPoint joinPoint) throws Throwable {
              TraceStatus status = null;
              try {
                  String message = joinPoint.getSignature().toShortString();
                  status = logTrace.begin(message);
                  //로직 호출
                  Object result = joinPoint.proceed();
                  logTrace.end(status);
                  return result;
              } catch (Exception e) {
                  logTrace.exception(status, e);
                  throw e;
              }
          }
      }

    + @Aspect: 애노테이션 기반 프록시 적용 시 필요
    + @Around(~):
      + 포인트컷 표현식(AspectJ 형식) -> 포인트컷
      + 해당 어노테이션이 있는 메서드는 Advice로 인식 -> Advice
    + ProceedingJoinPoint: 어드바이스에서 MethodInvocation과 유사한 기능으로 내부에 실제 호출 대상, 전달 인자, 호출 객체-메서드 정보 포함
      + proceed(): 실제 호출 대상 호출
     
+ AopConfig

      @Configuration
      @Import({AppV1Config.class, AppV2Config.class})
      public class AopConfig {
      
          @Bean
          public LogTraceAspect logTraceAspect(LogTrace logTrace){
              return new LogTraceAspect(logTrace);
          }
      }

    + @Aspect가 존재해도 @Bean으로 등록해야한다.
    + @Component로 바로 빈으로 등록해도 상관없다.

### 정리

+ 실무에서 대부분 이 방식 사용
+ 특정 기능에 한정되는 것이 아닌 어플리케이션 여러 기능들 사이에 걸쳐 들어가는 관심사로 횡단 관심사이다.

![image](https://github.com/ManchanTime/study/assets/127479677/88dc874d-48c4-416f-894d-31f1c4bc77c4)
