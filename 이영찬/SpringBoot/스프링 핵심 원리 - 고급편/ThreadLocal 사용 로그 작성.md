# 로그 작성

### Thread

+ Why?
  이전 수동 로그 작성 클래스를 생성하여 스프링 빈에 등록 후 사용하였다.
  이 때 모든 비즈니스 로직에 TraceId 객체를 넣어서 동기화를 시켜야 했기 때문에 상당히 불편한 방식이었다.
  이를 해결하기 위해서 Thread를 이용하여 해결한다.

+ 비즈니스 로직 클래스

  Controller, Service, Repository는 이전과 같다.
  단지 파라미터로 넘겨주던 TraceId가 필요없어졌을 뿐이다.

+ LogTrace

      public interface LogTrace {
        TraceStatus begin(String message);
        void end(TraceStatus status);
        void exception(TraceStatus status, Exception e);
      }

  로그 생성 인터페이스

+ FieldLogTrace


      @Slf4j
      public class FieldLogTrace implements LogTrace {
  
        private static final String START_PREFIX = "-->";
        private static final String COMPLETE_PREFIX = "<--";
        private static final String EX_PREFIX = "<X-";
    
        private TraceId traceIdHolder; //traceId 동기화
    
        @Override
        public TraceStatus begin(String message) {
            syncTraceId();
            TraceId traceId = traceIdHolder;
            Long startTimeMs = System.currentTimeMillis();
            //로그 출력
            log.info("[{}] {}{}", traceId.getId(), addSpace(START_PREFIX, traceId.getLevel()), message);
            return new TraceStatus(traceId, startTimeMs, message);
        }
    
        private void syncTraceId() {
            if (traceIdHolder == null) {
                traceIdHolder = new TraceId();
            } else {
                traceIdHolder = traceIdHolder.createNextId();
            }
        }
    
        @Override
        public void end(TraceStatus status) {
            complete(status, null);
        }
    
        @Override
        public void exception(TraceStatus status, Exception e) {
            complete(status, e);
        }
    
        private void complete(TraceStatus status, Exception e) {
            Long stopTimeMs = System.currentTimeMillis();
            long resultTimeMs = stopTimeMs - status.getStartTimeMs();
            TraceId traceId = status.getTraceId();
            if (e == null) {
                log.info("[{}] {}{} time={}ms", traceId.getId(),
                        addSpace(COMPLETE_PREFIX, traceId.getLevel()), status.getMessage(),
                        resultTimeMs);
            }
            else {
                log.info("[{}] {}{} time={}ms ex={}", traceId.getId(),
                        addSpace(EX_PREFIX, traceId.getLevel()), status.getMessage(), resultTimeMs,
                        e.toString());
            }
    
            releaseTraceId();
        }
    
        private void releaseTraceId() {
            if (traceIdHolder.isFirstLevel()) {
                traceIdHolder = null;
            } else {
                traceIdHolder = traceIdHolder.createPreviousId();
            }
        }
    
        private static String addSpace(String prefix, int level) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < level; i++) {
                sb.append( (i == level - 1) ? "|" + prefix : "|   ");
            }
            return sb.toString();
        }
      }

  LogTrace 인터페이스의 구현체
  이전 HelloTrace와 별로 달라진 것은 없지만 TraceId를 싱글톤 객체의 필드로 생성한다.
  이를 통해 이전처럼 각 로직마다 동기화할 필요가 없고 syncTraceId() 메소드를 통해서 TraceId를 생성하거나 가져와서 다음 단계의 TraceId를 생성하여 사용한다.

+ 결과

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/e9fead80-0ff1-45a5-a527-644fb3a64780)


+ 문제점
  
  동시성 문제!!
  위 결과에서 별로 문제될 것이 없어 보인다. 하지만 해당 로직이 끝나기 전에 다른 로직이 들어온다면?

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/121c59f6-dc5f-4c9a-81d8-c5649bc1a6e2)

  큰 문제가 발생한다. 같은 Id를 가진 TraceId가 두 번의 로직에 모두 적용된다. 특히 1번 로직은 문제없이 진행되지만 2번 로직은 1번의 TraceId와 레벨을 그대로 가지기 때문에
  결과 값이 다르게 나오고 같은 Id를 가지기 때문에 구별하는 것도 쉽지 않다.
  그렇다면 어떻게 해결할까?

### ThreadLocal

ThreadLocal은 각 사용중인 Thread에 맞는 저장소를 만들어 사용한다. 이를 통해서 각 Thread에서 저장되는 값들을 구별하여 저장할 수 있어 동시성 문제를 해결한다.

![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/246d6019-a62a-4448-aa24-cdb76df637a6)

+ ThreadLocalLogTrace

      @Slf4j
      public class ThreadLocalLogTrace implements LogTrace {
    
        private static final String START_PREFIX = "-->";
        private static final String COMPLETE_PREFIX = "<--";
        private static final String EX_PREFIX = "<X-";
    
        private ThreadLocal<TraceId> traceIdHolder = new ThreadLocal<>(); //traceId 동기화
    
        @Override
        public TraceStatus begin(String message) {
            syncTraceId();
            TraceId traceId = traceIdHolder.get();
            Long startTimeMs = System.currentTimeMillis();
            //로그 출력
            log.info("[{}] {}{}", traceId.getId(), addSpace(START_PREFIX, traceId.getLevel()), message);
            return new TraceStatus(traceId, startTimeMs, message);
        }
    
        private void syncTraceId() {
            TraceId traceId = traceIdHolder.get();
            if (traceId == null) {
                traceIdHolder.set(new TraceId());
            } else {
                traceIdHolder.set(traceId.createNextId());
            }
        }
    
        @Override
        public void end(TraceStatus status) {
            complete(status, null);
        }
    
        @Override
        public void exception(TraceStatus status, Exception e) {
            complete(status, e);
        }
    
        private void complete(TraceStatus status, Exception e) {
            Long stopTimeMs = System.currentTimeMillis();
            long resultTimeMs = stopTimeMs - status.getStartTimeMs();
            TraceId traceId = status.getTraceId();
            if (e == null) {
                log.info("[{}] {}{} time={}ms", traceId.getId(),
                        addSpace(COMPLETE_PREFIX, traceId.getLevel()), status.getMessage(),
                        resultTimeMs);
            }
            else {
                log.info("[{}] {}{} time={}ms ex={}", traceId.getId(),
                        addSpace(EX_PREFIX, traceId.getLevel()), status.getMessage(), resultTimeMs,
                        e.toString());
            }
    
            releaseTraceId();
        }
    
        private void releaseTraceId() {
            TraceId traceId = traceIdHolder.get();
            if (traceId.isFirstLevel()) {
                traceIdHolder.remove(); //destroy
            } else {
                traceIdHolder.set(traceId.createPreviousId());
            }
        }
    
        private static String addSpace(String prefix, int level) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < level; i++) {
                sb.append( (i == level - 1) ? "|" + prefix : "|   ");
            }
            return sb.toString();
        }
    }

  앞 FieldLogTrace를 대신할 LogTrace의 구현체이다.
  TraceId 객체를 직접 사용하는 것이 아닌 ThreadLocal 객체에 타입으로 TraceId를 넣어 사용한다.
  이 때 ThreadLocal의 set, get, remove를 사용하여 객체에 값을 넣거나 꺼내고 삭제한다.
  주의할 점은 ThreadLocal 객체의 사용이 완료되어 반환할 때 꼭 remove를 사용하여 ThreadLocal을 삭제해야 한다.

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/9e0ba5dd-4d43-4227-8e76-82fa6179150b)

  해당 이미지처럼 삭제가 완료되지 않으면 값은 그대로 남아있고 다른 요청이 들어와 Thread를 사용할 때 저장되어있는 값이 메모리 누수나 데이터 변경 오류를 발생시킬 수 있다.

+ LogConfig
  
      @Configuration
      public class LogTraceConfig {
        @Bean
        public LogTrace logTrace() {
            return new ThreadLocalLogTrace();
        }
      }
  
  config 파일도 사용할 구현체로 변경후 스프링 빈에 등록한다. 

+ 결과

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/83a3a2d3-6f0f-469d-bea8-167c3ac399c4)

  조금 복잡해 보이긴 해도 TraceId의 아이디 값이 두개로 따로 생성된 것을 볼 수 있다.
  아이디 값끼리 묶어서 보면 맞게 레벨별로 로그가 출력되는 것을 볼 수 있다.
  ThreadLocalLogTrace 클래스의 complete에서 사용이 완료된 TraceId를 remove를 통해서 제거하기 때문에 메모리 문제도 없다.

+ 결론

  동시성 문제는 어디서나 발생할 수 있는 흔한 문제이고 또한 심각한 문제이다. 따라서 동시성 문제가 발생하는지 꼭 확인하고 넘어갈 필요가 있다.
  특히 트래픽이 많이 발생하는 프로그램에서는 더욱 더 세밀하게 확인해야할 문제다.
  다음으로 템플릿 메서드와 콜백 패턴을 사용해보겠다.
