# 빈 후처리기

+ 이전 스프링이 지원하는 ProxyFactory를 이용하여 Cglib와 Dynamic 구분없이 프록시 생성이 가능해졌다.
+ Advisor(PointCut + Advice)를 사용하여 여러 프록시 생성 조건과 로직을 묶어서 프록시 생성이 가능해졌다.
+ But 스프링 빈 등록 시 프록시 생성을 위해서 모든 빈 등록 클래스에 프록시 생성 로직을 추가하고 빈에 등록해야 했다.

### 빈 후처리기(BeanPostProcessor)

+ 소개

  + 과정

    ![image](https://github.com/ManchanTime/study/assets/127479677/4285138d-dd6d-40af-93b3-04e6f31ad28e)

    + 생성: 스프링 빈 대상이 되는 객체를 생성한다.
    + 전달: 생성된 객체를 빈 저장소에 등록하기 전에 빈 후처리기에 전달한다.
    + 후 처리 작업: 전달된 스프링 객체를 조작하거나 다른 객체로 바꾼다.
    + 등록: 빈 후처리기는 작업이 끝난 빈을 반환한다. 전달된 빈은 작업 완료 상태로 빈 저장소에 등록된다.

  + 기능
    빈이 등록될 때 객체를 조작할 수 있고 다른 객체로 바꿔서 등록할 수도 있다.

    + 객체 바꿔치기

      ![image](https://github.com/ManchanTime/study/assets/127479677/bdf1e64f-a982-4560-8f75-827ee9c261d6)

+ 예제 코드 - 객체 바꿔치기

  + BeanPostProcessorConfig
    
    A라는 클래스를 스프링 빈에 등록한다.

        @Slf4j
        @Configuration
        static class BeanPostProcessorConfig {
            @Bean(name = "beanA")
            public A a() {
                return new A();
            }
    
            @Bean
            public AToBPostProcessor helloPostProcessor() {
                return new AToBPostProcessor();
            }
        }

  + AToBProcessor
 
    빈 후처리기 클래스로 들어온 Bean이 A 타입이라면 B로 교체한다.

        @Slf4j
        static class AToBPostProcessor implements BeanPostProcessor {
    
            @Override
            public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
                log.info("beanName={} bean={}", beanName, bean);
                if(bean instanceof A){
                    return new B();
                }
                return bean;
            }
        }

  + Result
 
        @Test
        void basicConfig(){
            ApplicationContext applicationContext = new AnnotationConfigApplicationContext(BeanPostProcessorConfig.class);
    
            //beanA 이름으로 B 객체가 빈으로 등록된다.
            B b = applicationContext.getBean("beanA", B.class);
            b.helloB();
    
            //B는 빈으로 등록되지 않는다.
            //B bean = applicationContext.getBean(B.class);
            assertThrows(NoSuchBeanDefinitionException.class, () -> applicationContext.getBean(A.class));
        }

    A클래스로 빈에 등록된 "beanA"라는 빈은 빈 후처리기를 통해 B로 교체되었기 때문에 호출 시 B타입의 클래스로 호출된다.

+ 실제 어플리케이션에 적용1 - 빈 후처리기 프로세서 작성

  + PackageLogTracePostProcessor

    basePackage 이름을 받아서 해당 패키지에만 프로세서 적용

        @Slf4j
        public class PackageLogTracePostProcessor implements BeanPostProcessor {
        
            private final String basePackage;
            private final Advisor advisor;
        
            public PackageLogTracePostProcessor(String basePackage, Advisor advisor) {
                this.basePackage = basePackage;
                this.advisor = advisor;
            }
        
            @Override
            public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
                log.info("param beanName={} bean={}", beanName, bean.getClass());
        
                //프록시 적용 대상 여부 체크
                //프록시 적용 대상이 아니면 원본을 그대로 진행
                String packageName = bean.getClass().getPackageName();
                if(!packageName.startsWith(basePackage)){
                    return bean;
                }
        
                //프록시 대상이면 프록시를 만들어서 반환
                ProxyFactory proxyFactory = new ProxyFactory(bean);
                proxyFactory.addAdvisor(advisor);
        
                Object proxy = proxyFactory.getProxy();
                log.info("create proxy: target={} proxy={}", bean.getClass(), proxy.getClass());
                return proxy;
            }
        }

  + BeanPostProcessorConfig
 
    빈 후처리기를 등록하는 것으로 지정 패키지에 해당되는 모든 빈 등록 객체에 대한 프록시 적용, AppV1Config, AppV2Config도 함께 적용하여 v1, v2도 빈에 등록

        @Slf4j
        @Configuration
        @Import({AppV1Config.class, AppV2Config.class})
        public class BeanPostProcessorConfig {
        
            @Bean
            public PackageLogTracePostProcessor logTracePostProcessor(LogTrace logTrace) {
                return new PackageLogTracePostProcessor("hello.proxy.app", getAdvisor(logTrace));
            }
        
            private Advisor getAdvisor(LogTrace logTrace) {
                //pointcut
                NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();
                pointcut.setMappedNames("request*", "order*", "save*");
                //advice
                LogTraceAdvice advice = new LogTraceAdvice(logTrace);
                return new DefaultPointcutAdvisor(pointcut, advice);
            }
        }

  + 특징
    + 프록시 적용 여부 판별에 PointCut을 통해서 더 깔끔하고 정밀하게 적용 가능

+ 실제 어플리케이션에 적용2 - Advisor를 빈으로 등록

  ![image](https://github.com/ManchanTime/study/assets/127479677/0d151eed-8d8a-4f12-81b1-b634facd5238)

  + 자동 프록시 생성기 - AutoProxyCreator
    + 스프링 부트 자동 설정으로 AnnotationAwareAspectJAutoProxyCreator라는 빈 후처리가 자동으로 스프링 빈에 등록
    + 이 후처리기는 등록된 Advisor들을 자동으로 찾아서 프록시가 필요한 곳에 자동으로 프록시 적용
    + Advisor에는 PointCut과 Advice가 모두 포함되있기 때문에 Advisor를 통해서 프록시 적용 여부를 알 수 있다.
   
  + AutoProxyConfig

        @Configuration
        @Import({AppV1Config.class, AppV2Config.class})
        public class AutoProxyConfig {
        
            //@Bean
            public Advisor advisor1(LogTrace logTrace) {
                //pointcut
                NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();
                pointcut.setMappedNames("request*", "order*", "save*");
                //advice
                LogTraceAdvice advice = new LogTraceAdvice(logTrace);
                return new DefaultPointcutAdvisor(pointcut, advice);
            }
        
            //@Bean
            public Advisor advisor2(LogTrace logTrace) {
                //pointcut
                AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
                pointcut.setExpression("execution(* hello.proxy.app..*(..))");
                //advice
                LogTraceAdvice advice = new LogTraceAdvice(logTrace);
                return new DefaultPointcutAdvisor(pointcut, advice);
            }
        
            @Bean
            public Advisor advisor3(LogTrace logTrace) {
                //pointcut
                AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
                pointcut.setExpression("execution(* hello.proxy.app..*(..)) && !execution(* hello.proxy.app..noLog(..))");
                //advice
                LogTraceAdvice advice = new LogTraceAdvice(logTrace);
                return new DefaultPointcutAdvisor(pointcut, advice);
            }
        }

      + Advisor1
        + 이전 getAdvice() 메서드 처럼 PointCut으로 단순히 이름만 넣었다. -> 빈 등록 시에 이 이름에 해당하는 모든 빈에 프록시가 적용되었다.
       
      + Advisor2
        + AspectJ PointCut으로 패키지 조건을 넣어 해당 패키지에 해당하는 객체 빈 등록 시에만 프록시를 적용하였다.
       
      + Advisor3
        + AspectJ PointCut에 !과 &&로 해당되지 않는 메서드는 제외하는 방식을 Advice적용 메서드까지 걸러낼 수 있었다.

  + 여러 Advisor 적용 시

    ![image](https://github.com/ManchanTime/study/assets/127479677/758003f7-fd70-4964-8538-a8ce970f8247)
 
    ![image](https://github.com/ManchanTime/study/assets/127479677/2a5c6c96-84cd-4d24-a382-2660320eb038)

    1. 여러 Advisor가 적용된다면 먼저 적용 Advisor 순서대로 PointCut에 해당되는지 빈 객체와 비교하고 해당된다면 프록시를 생성한다.
    2. 다음 Advisor PointCut 또한 마찬가지로 빈 객체와 비교하고 해당되지 않는다면 빈 객체 프록시에 해당 Advisor는 적용하지 않고 넘어간다.
    3. 둘다 적용되지 않는다면 프록시를 생성하지 않는다.
   
    + 결국 빈 등록 객체에 해당하는 프록시는 단 한개만 생성된다!! Advisor 하나 당 프록시가 계속 생성되는 것이 아니다!!
   
### 정리

+ 자동 프록시 생성기인 AnnotationAwareAspectJAutoProxyCreator 덕분에 매우 편리하게 프록시를 적용할 수 있다.
+ PointCut을 통해서 프록시 생성 여부와 어드바이스 적용 여부 모두 판별할 수 있었다. -> 둘 중 하나라도 적용된다면 프록시를 생성한다.
