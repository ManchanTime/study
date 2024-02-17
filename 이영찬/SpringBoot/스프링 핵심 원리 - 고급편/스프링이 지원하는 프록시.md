# 스프링 자체에서 제공하는 프록시 생성

+ 이전 문제점
  + 인터페이스 -> JDK, 클래스구체 -> CGLIB / 두 기술 함께 병행에 어려움 발생 -> 공통 기능 필요
  + 특정 조건에 맞는 프록시 로직(접근 제한 등) 공통 제공 필요

### ProxyFactory

+ why?

  이전 핸들러를 통해서 필요한 인터페이스를 찾아 실행하는 것처럼 ProxyFactory를 거치면서 JDK인지 CGLIB인지 결정하여 실행할 수 있음

+ 의존 관계

  ![image](https://github.com/ManchanTime/study/assets/127479677/ba0ca1d5-2bc4-428b-a8e4-1bace7876665)

+ 사용 흐름

  ![image](https://github.com/ManchanTime/study/assets/127479677/29d591dc-0da7-4860-bbe3-dbfd9325f960)

+ Advice

  + 필요한 부가 기능을 공통으로 추가하기 위해서 JDK, CGLIB에 관여하지않고 Advice를 생성하여 적용하면 끝
 
  + Advice를 거치면서 부가 기능을 거치고 target으로 넘어

  ![image](https://github.com/ManchanTime/study/assets/127479677/ea6ce9bb-affe-4bb8-9f0c-8fa83ce79fe1)

  ![image](https://github.com/ManchanTime/study/assets/127479677/2e092fd9-c36b-4944-aa58-831c88657b08)

+ Pointcut

  + 특정 메서드 이름의 조건에 맞을 때만 프록시 부가 기능이 적용되는 코드에서 Pointcut을 통해 필터링
 
+ 예제 코드

  + TimeAdvice

    시간 측정 부가 기능 추가 Advice -> MethodInterceptor(Aop 패키지) 구현

        @Slf4j
        public class TimeAdvice implements MethodInterceptor {
        
            @Override
            public Object invoke(MethodInvocation invocation) throws Throwable {
                log.info("TimeProxy 실행");
                long startTime = System.currentTimeMillis();
        
                //Object result = method.invoke(target, args);
                Object result = invocation.proceed();
        
                long endTime = System.currentTimeMillis();
                long resultTime = endTime - startTime;
                log.info("TimeProxy 종료 resultTime={}", resultTime);
                return result;
            }
        }

  + ProxyFactoryTest
 
    + CGLIB든 JDK든 조건에 맞을 때 실행 -> setProxyTargetClass를 true로 설정하면 무조건 CGLIB 사용
   
    + AopUtils.isCglibProxy나 isJdkDynamicProxy로 사용한 방식 체크 가능
   
    + isAopProxy로 ProxyFactory 사용여부 판단 가능
    
        @Slf4j
        public class ProxyFactoryTest {
        
            @Test
            @DisplayName("인터페이스가 있으면 JDK 동적 프록시 사용")
            void interfaceProxy() {
                ServiceInterface target = new ServiceImpl();
                ProxyFactory proxyFactory = new ProxyFactory(target);
                proxyFactory.addAdvice(new TimeAdvice());
                ServiceInterface proxy = (ServiceInterface) proxyFactory.getProxy();
                log.info("targetClass={}", target.getClass());
                log.info("proxyClass={}", proxy.getClass());
        
                proxy.save();
        
                assertThat(AopUtils.isAopProxy(proxy)).isTrue();
                assertThat(AopUtils.isJdkDynamicProxy(proxy)).isTrue();
                assertThat(AopUtils.isCglibProxy(proxy)).isFalse();
            }
        
            @Test
            @DisplayName("구체 클래스만 있으면 JDK 동적 프록시 사용")
            void concreteProxy() {
                ConcreteService target = new ConcreteService();
                ProxyFactory proxyFactory = new ProxyFactory(target);
                proxyFactory.addAdvice(new TimeAdvice());
                ConcreteService proxy = (ConcreteService) proxyFactory.getProxy();
                log.info("targetClass={}", target.getClass());
                log.info("proxyClass={}", proxy.getClass());
        
                proxy.call();
        
                assertThat(AopUtils.isAopProxy(proxy)).isTrue();
                assertThat(AopUtils.isCglibProxy(proxy)).isTrue();
                assertThat(AopUtils.isJdkDynamicProxy(proxy)).isFalse();
            }
        
            @Test
            @DisplayName("ProxyTargetClass 옵션을 사용하면 인터페이스가 있어도 CGLIB를 사용하고, 클래스 기반 프록시 사용")
            void proxyTargetClass() {
                ServiceInterface target = new ServiceImpl();
                ProxyFactory proxyFactory = new ProxyFactory(target);
                proxyFactory.setProxyTargetClass(true);
                proxyFactory.addAdvice(new TimeAdvice());
                ServiceInterface proxy = (ServiceInterface) proxyFactory.getProxy();
                log.info("targetClass={}", target.getClass());
                log.info("proxyClass={}", proxy.getClass());
        
                proxy.save();
        
                assertThat(AopUtils.isAopProxy(proxy)).isTrue();
                assertThat(AopUtils.isJdkDynamicProxy(proxy)).isFalse();
                assertThat(AopUtils.isCglibProxy(proxy)).isTrue();
            }
        }

### Advice & Pointcut & Advisor

+ Pointcut

  + 어디에 부가 기능을 적용할지, 어디에 적용하지 않을지 판단하는 필터링 로직으로 주로 클래스, 메서드 이름으로 필터링

+ Advice

  + 프록시가 호출하는 부가 기능 -> 프록시 로직

+ Advisor

  + 하나의 포인컷 + 하나의 어드바이스
  + 포인트컷으로 어디에 적용할지 선택하고 어드바이스로 어떤 로직을 적용할지 선택하는 것
 
  + 포인트컷은 필터만 어드바이스는 로직만 담당하여 역할을 확실하게 구분

    ![image](https://github.com/ManchanTime/study/assets/127479677/5860a724-ae81-49a1-a51a-ab4de1ea3f82)

+ 예제 코드

  Pointcut과 Advice를 적용하여 메서드 이름이 "save"일 때만 Advice가 작동해야함.

  ![image](https://github.com/ManchanTime/study/assets/127479677/96c9c4d9-32cb-4bef-a4ef-dd4cef6e178a)


  + AdvisorTest
 
    Pointcut을 생성하여 필터조건을 넣고 Pointcut에 필요한 MethodMatcher 인터페이스 구현체 생성, advice는 이전 생성한 TimeAdvice 사
 
        @Slf4j
        public class AdvisorTest {
        
            @Test
            void advisorTest1() {
                ServiceImpl target = new ServiceImpl();
                ProxyFactory proxyFactory = new ProxyFactory(target);
                DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor(Pointcut.TRUE, new TimeAdvice());
                proxyFactory.addAdvisor(advisor);
                ServiceInterface proxy = (ServiceInterface) proxyFactory.getProxy();
        
                proxy.save();
                proxy.find();
            }
        
            @Test
            @DisplayName("직접 만든 포인트컷")
            void advisorTest2() {
                ServiceImpl target = new ServiceImpl();
                ProxyFactory proxyFactory = new ProxyFactory(target);
                DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor(new MyPointcut(), new TimeAdvice());
                proxyFactory.addAdvisor(advisor);
                ServiceInterface proxy = (ServiceInterface) proxyFactory.getProxy();
        
                proxy.save();
                proxy.find();
            }
        
            @Test
            @DisplayName("스프링이 제공하는 포인트컷")
            void advisorTest3() {
                ServiceImpl target = new ServiceImpl();
                ProxyFactory proxyFactory = new ProxyFactory(target);
                NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();
                pointcut.setMappedName("save");
                DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor(pointcut, new TimeAdvice());
                proxyFactory.addAdvisor(advisor);
                ServiceInterface proxy = (ServiceInterface) proxyFactory.getProxy();
        
                proxy.save();
                proxy.find();
            }
        
            static class MyPointcut implements Pointcut {
        
                @Override
                public ClassFilter getClassFilter() {
                    return ClassFilter.TRUE;
                }
        
                @Override
                public MethodMatcher getMethodMatcher() {
                    return new MyMethodMatcher();
                }
            }
        
            static class MyMethodMatcher implements MethodMatcher {
        
                private String matchName = "save";
        
                @Override
                public boolean matches(Method method, Class<?> targetClass) {
                    boolean result = method.getName().equals(matchName);
                    log.info("포인트컷 호출 method={} targetClass={}", method.getName(), targetClass);
                    log.info("포인트컷 결과 result={}", result);
                    return result;
                }
        
                @Override
                public boolean isRuntime() {
                    return false;
                }
        
                @Override
                public boolean matches(Method method, Class<?> targetClass, Object... args) {
                    return false;
                }
            }
        }

  + MultiAdvisorTest
 
    여러 어드바이저가 필요하다면?
 
    ![image](https://github.com/ManchanTime/study/assets/127479677/70259755-f4d4-46b6-817b-ff8ff4f5917e)
 
    + 첫번째 방법으로 어드바이저 하나씩 적용하기 위해 해당 어드바이저가 적용될 프록시를 하나씩 만든다.
    + 두번째 방법으로 어드바이저를 생성하고 하나의 프록시에 Chaining Filter처럼 순서에 맞게 addAdvisor로 넣어준다. -> 어드바이저마다 프록시 생성x 하나의 프록시만 사용용

        @Slf4j
        public class MultiAdvisorTest {
        
            @Test
            @DisplayName("여러 프록시")
            void multiAdvisorTest() {
                //client -> proxy2(advisor2) -> proxy1(advisor1) -> target
        
                //프록시1 생성
                ServiceImpl target = new ServiceImpl();
                ProxyFactory proxyFactory1 = new ProxyFactory(target);
                DefaultPointcutAdvisor advisor1 = new DefaultPointcutAdvisor(Pointcut.TRUE, new Advice1());
                proxyFactory1.addAdvisor(advisor1);
                ServiceInterface proxy1 = (ServiceInterface) proxyFactory1.getProxy();
        
                //프록시2 생성 target -> proxy1 입력
                ProxyFactory proxyFactory2 = new ProxyFactory(proxy1);
                DefaultPointcutAdvisor advisor2 = new DefaultPointcutAdvisor(Pointcut.TRUE, new Advice2());
                proxyFactory2.addAdvisor(advisor2);
                ServiceInterface proxy2 = (ServiceInterface) proxyFactory2.getProxy();
        
                proxy2.save();
            }
        
            @Test
            @DisplayName("하나의 프록시, 여러 어드바이저")
            void multiAdvisorTest2() {
                //client -> proxy -> advisor2 -> advisor1 -> target
        
                DefaultPointcutAdvisor advisor1 = new DefaultPointcutAdvisor(Pointcut.TRUE, new Advice1());
                DefaultPointcutAdvisor advisor2 = new DefaultPointcutAdvisor(Pointcut.TRUE, new Advice2());
        
                //프록시1 생성
                ServiceImpl target = new ServiceImpl();
                ProxyFactory proxyFactory1 = new ProxyFactory(target);
        
                proxyFactory1.addAdvisor(advisor2);
                proxyFactory1.addAdvisor(advisor1);
                ServiceInterface proxy1 = (ServiceInterface) proxyFactory1.getProxy();
        
                //실행
                proxy1.save();
            }
        
            static class Advice1 implements MethodInterceptor {
        
                @Override
                public Object invoke(MethodInvocation invocation) throws Throwable {
                    log.info("advice1 호출");
                    return invocation.proceed();
                }
            }
        
            static class Advice2 implements MethodInterceptor {
        
                @Override
                public Object invoke(MethodInvocation invocation) throws Throwable {
                    log.info("advice2 호출");
                    return invocation.proceed();
                }
            }
        }

### 실제 어플리케이션에 적용

+ v1 버전

  + ProxyFactoryConfigV1

      이전 config와 비슷하지만 Proxy를 위한 인터페이스 클래스 생성x + no-log api 필터링 시 Proxy에 직접 로직 적용x advice사

        @Slf4j
        @Configuration
        public class ProxyFactoryConfigV1 {
        
            @Bean
            public OrderControllerV1 orderControllerV1(LogTrace logTrace) {
                OrderControllerV1 orderController = new OrderControllerV1Impl(orderServiceV1(logTrace));
                ProxyFactory factory = new ProxyFactory(orderController);
                factory.addAdvisor(getAdvisor(logTrace));
                OrderControllerV1 proxy = (OrderControllerV1) factory.getProxy();
                log.info("ProxyFactory proxy={}, target={}", proxy.getClass(), orderController.getClass());
                return proxy;
            }
        
            @Bean
            public OrderServiceV1 orderServiceV1(LogTrace logTrace) {
                OrderServiceV1Impl orderService = new OrderServiceV1Impl(orderRepositoryV1(logTrace));
                ProxyFactory factory = new ProxyFactory(orderService);
                factory.addAdvisor(getAdvisor(logTrace));
                OrderServiceV1 proxy = (OrderServiceV1) factory.getProxy();
                log.info("ProxyFactory proxy={}, target={}", proxy.getClass(), orderService.getClass());
                return proxy;
            }
        
            @Bean
            public OrderRepositoryV1 orderRepositoryV1(LogTrace logTrace) {
                OrderRepositoryV1Impl orderRepository = new OrderRepositoryV1Impl();
                ProxyFactory factory = new ProxyFactory(orderRepository);
                factory.addAdvisor(getAdvisor(logTrace));
                OrderRepositoryV1 proxy = (OrderRepositoryV1) factory.getProxy();
                log.info("ProxyFactory proxy={}, target={}", proxy.getClass(), orderRepository.getClass());
                return proxy;
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

+ v2 버전

  + ProxyConfigV2

      v1버전과 동일 but CGLIB를 통해 프록시 생
  
        @Slf4j
        @Configuration
        public class ProxyFactoryConfigV2 {
            @Bean
            public OrderControllerV2 orderControllerV2(LogTrace logTrace) {
                OrderControllerV2 orderController = new OrderControllerV2(orderServiceV2(logTrace));
                ProxyFactory factory = new ProxyFactory(orderController);
                factory.addAdvisor(getAdvisor(logTrace));
                OrderControllerV2 proxy = (OrderControllerV2) factory.getProxy();
                log.info("ProxyFactory proxy={}, target={}", proxy.getClass(), orderController.getClass());
                return proxy;
            }
        
            @Bean
            public OrderServiceV2 orderServiceV2(LogTrace logTrace) {
                OrderServiceV2 orderService = new OrderServiceV2(orderRepositoryV2(logTrace));
                ProxyFactory factory = new ProxyFactory(orderService);
                factory.addAdvisor(getAdvisor(logTrace));
                OrderServiceV2 proxy = (OrderServiceV2) factory.getProxy();
                log.info("ProxyFactory proxy={}, target={}", proxy.getClass(), orderService.getClass());
                return proxy;
            }
        
            @Bean
            public OrderRepositoryV2 orderRepositoryV2(LogTrace logTrace) {
                OrderRepositoryV2 orderRepository = new OrderRepositoryV2();
                ProxyFactory factory = new ProxyFactory(orderRepository);
                factory.addAdvisor(getAdvisor(logTrace));
                OrderRepositoryV2 proxy = (OrderRepositoryV2) factory.getProxy();
                log.info("ProxyFactory proxy={}, target={}", proxy.getClass(), orderRepository.getClass());
                return proxy;
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

### 정리

+ ProxyFactory를 통해 JDK, CGLIB 신경쓰지 않고 프록시 생성가능
+ 어드바이저, 어드바이스, 포인트컷을 통해 프록시 로직 수정없이 부가 기능, 적용 범위 설정 가능

+ 문제점

  + 설정이 너무 많다.

    + ProxyFactoryConfig들만 봐도 @Configuration으로 등록할 스프링 빈 코드가 중복도 많고 길다.
    + 컴포넌트 스캔을 사용할 수 없어서 무조건 수작업이다.
   
  => 이를 빈 후처리기를 통해 해결한다.
