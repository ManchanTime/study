# 스프링 AOP 적용

### 실제 유용하게 활용할 수 있는 스프링 AOP

+ 예제
  + @Trace를 이용하여 어노테이션으로 로그 출력하기
  + @Retry를 이용하여 예외 발생 시 재시도 하기
 
  + ExamRepository

        @Repository
        public class ExamRepository {
        
            private static int seq = 0;
        
            /**
             * 5번에 1번 실패하는 요청
             */
            @Trace
            @Retry(4) //횟수 제한 필수
            public String save(String itemId) {
                seq++;
                if (seq % 5 == 0) {
                    throw new IllegalStateException("예외 발생");
                }
                return "ok";
            }
        }

    + seq값을 기준으로 5의 배수일 때 일부러 예외 발생

  + ExamService

        @Service
        @RequiredArgsConstructor
        public class ExamService {
        
            private final ExamRepository examRepository;
        
            @Trace
            public void request(String itemId) {
                examRepository.save(itemId);
            }
        }

  + ExamTest

        @Slf4j
        @Import({TraceAspect.class, RetryAspect.class})
        @SpringBootTest
        public class ExamTest {
        
            @Autowired
            ExamService examService;
        
            @Test
            void test() {
                for (int i = 0; i < 5; i++) {
                    log.info("client request i={}", i);
                    examService.request("data" + i);
                }
            }
        }

    + 5번 돌면서 i 값에 따라 예외 판별 -> i가 4일 때 예외 발생 유도

+ 로그 출력 AOP

  + @Trace

        @Target(ElementType.METHOD)
        @Retention(RetentionPolicy.RUNTIME)
        public @interface Trace {
        }

  + TraceAspect

        @Slf4j
        @Aspect
        public class TraceAspect {
        
            @Before("@annotation(hello.aop.exam.annotation.Trace)")
            public void doTrace(JoinPoint joinPoint) {
                Object[] args = joinPoint.getArgs();
                log.info("[trace] {} args={}", joinPoint.getSignature(), args);
            }
        }

    + @Trace가 있는 메서드를 기준으로 해당 메서드의 파라미터를 출력 -> data0, data1...,data4까지
    + ExamRepository의 save 메서드와 ExamService의 request 메서드에 @Trace 추가

  + 테스트 결과

    ![image](https://github.com/ManchanTime/study/assets/127479677/0c4a9505-9d4c-4bbc-b16d-8aa6ce378fb3)

+ 재시도 AOP

  + @Retry

        @Target(ElementType.METHOD)
        @Retention(RetentionPolicy.RUNTIME)
        public @interface Retry {
            int value() default 3;
        }

    + value()를 설정하여 최소 반복 회수를 설정할 수 있음 -> value 이름은 상관없음
   
  + RetryAspect

        @Slf4j
        @Aspect
        public class RetryAspect {
        
            @Around("@annotation(retry)")
            public Object doRetry(ProceedingJoinPoint joinPoint, Retry retry) throws Throwable {
                log.info("[retry] {} retry={}", joinPoint.getSignature(), retry);
                int maxRetry = retry.value();
                Exception exceptionHolder = null;
        
                for(int retryCount = 1; retryCount <= maxRetry; retryCount++) {
                    try {
                        log.info("[retry] try count={}/{}", retryCount, maxRetry);
                        return joinPoint.proceed();
                    } catch (Exception e) {
                        exceptionHolder = e;
                    }
                }
                throw exceptionHolder;
            }
        }

    + 예외가 발생할 수 있는 메서드에 @Retry를 넣어 최대 @Retry의 value 값만큼 반복하여 예외가 발생하지 않으면 return 아니면 예외 출력
    + ExamRepository에 @Retry 추가 -> @Retry(value = 회수)로 default 값을 여기서 설정 가능(value = ) 생략 가능
   
  + 결과

    ![image](https://github.com/ManchanTime/study/assets/127479677/6eadaaaa-85e5-479f-9c71-760bb98ac0f7)

    i=4 일 때 예외가 발생하고 RetryAspect를 통해 4번(value = 4) 반복 -> 한번 더 실행했을 때 seq++에 의해 seq = 6이 되므로 예외가 발생하지 않아 통과과
