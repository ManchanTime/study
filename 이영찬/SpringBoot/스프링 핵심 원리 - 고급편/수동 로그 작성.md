# 스프링 핵심 원리

### 로그 남기기

단순 스프링 빈으로 등록하여 로그 수동으로 남기기

+ V1 - id, level 상관없이 일단 로그만 남기기

  + HelloTraceV1

    @Slf4j 
    
    @Component
    
    public class HelloTraceV1 {
    
        private static final String START_PREFIX = "-->";
        private static final String COMPLETE_PREFIX = "<--";
        private static final String EX_PREFIX = "<X-";
    
        public TraceStatus begin(String message) {
            TraceId traceId = new TraceId();
            Long startTimeMs = System.currentTimeMillis();
            //로그 출력
            log.info("[{}] {}{}", traceId.getId(), addSpace(START_PREFIX, traceId.getLevel()), message);
            return new TraceStatus(traceId, startTimeMs, message);
        }
        public void end(TraceStatus status) {
            complete(status, null);
        }
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
        }
    
        private static String addSpace(String prefix, int level) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < level; i++) {
                sb.append( (i == level - 1) ? "|" + prefix : "|   ");
            }
            return sb.toString();
        }
    }

  + OrderControllerV1
  
    @RestController
    
    @RequiredArgsConstructor
    
    public class OrderControllerV1 {
    
        private final OrderServiceV1 orderService;
        private final HelloTraceV1 trace;
    
        @GetMapping("/v1/request")
        public String request(String itemId) {
            TraceStatus status = null;
            try {
                status = trace.begin("OrderController.request()");
                orderService.orderItem(itemId);
                trace.end(status);
                return "ok";
            } catch (Exception e) {
                trace.exception(status, e);
                throw e;
            }
        }
    }

  + OrderServiceV1

    @Service
    
    @RequiredArgsConstructor
    
    public class OrderServiceV1 {
    
        private final OrderRepositoryV1 orderRepository;
        private final HelloTraceV1 trace;
    
        public void orderItem(String itemId) {
            TraceStatus status = null;
            try {
                status = trace.begin("OrderService.orderItem()");
                orderRepository.save(itemId);
                trace.end(status);
            } catch (Exception e) {
                trace.exception(status, e);
                throw e;
            }
        }
    }

  + OrderRepositoryV1

    @Repository
    @RequiredArgsConstructor
    public class OrderRepositoryV1 {
    
        private final HelloTraceV1 trace;
    
        public void save(String itemId) {
            TraceStatus status = null;
            try {
                status = trace.begin("OrderRepository.orderItem()");
                if (itemId.equals("ex")) {
                    throw new IllegalStateException("예외 발생!");
                }
                sleep(1000);
                trace.end(status);
            } catch (Exception e) {
                trace.exception(status, e);
                throw e;
            }
        }
    
        private void sleep(int mills) {
            try {
                Thread.sleep(mills);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
  
  + 결과

    + 정상 흐름

      ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/e3c1014b-9325-40cb-8f82-a8bcb9a0068f)

    + 예외 발생

      ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/2a81ab60-894e-474c-b14f-72b90b27ea80)

    코드 자체는 level에 따라 space와 prefix로 구별을 두고 id도 생성하여 넣지만 상위 레벨의 로그에서 레벨과 id를 넘겨주지 않아서
    각 단계 별로 독립적인 로그를 출력한다.

+ V2 - 레벨, 아이디 싱크 맞추기

  + HelloTraceV2

    @Slf4j
    
    @Component
    
    public class HelloTraceV2 {
    
        private static final String START_PREFIX = "-->";
        private static final String COMPLETE_PREFIX = "<--";
        private static final String EX_PREFIX = "<X-";
    
        public TraceStatus begin(String message) {
            TraceId traceId = new TraceId();
            Long startTimeMs = System.currentTimeMillis();
            //로그 출력
            log.info("[{}] {}{}", traceId.getId(), addSpace(START_PREFIX, traceId.getLevel()), message);
            return new TraceStatus(traceId, startTimeMs, message);
        }
    
        public TraceStatus beginSync(TraceId beforeTraceId, String message) {
            TraceId nextId = beforeTraceId.createNextId();
            Long startTimeMs = System.currentTimeMillis();
            //로그 출력
            log.info("[{}] {}{}", nextId.getId(), addSpace(START_PREFIX, nextId.getLevel()), message);
            return new TraceStatus(nextId, startTimeMs, message);
        }
    
        public void end(TraceStatus status) {
            complete(status, null);
        }
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
        }
    
        private static String addSpace(String prefix, int level) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < level; i++) {
                sb.append( (i == level - 1) ? "|" + prefix : "|   ");
            }
            return sb.toString();
        }
    }

  + OrderControllerV2

    @RestController
    
    @RequiredArgsConstructor
    
    public class OrderControllerV2 {
    
        private final OrderServiceV2 orderService;
        private final HelloTraceV2 trace;
    
        @GetMapping("/v2/request")
        public String request(String itemId) {
            TraceStatus status = null;
            try {
                status = trace.begin("OrderController.request()");
                orderService.orderItem(status.getTraceId(), itemId);
                trace.end(status);
                return "ok";
            } catch (Exception e) {
                trace.exception(status, e);
                throw e;
            }
        }
    }

  + OrderServiceV2
    
    @Service
    
    @RequiredArgsConstructor
    
    public class OrderServiceV2 {
    
        private final OrderRepositoryV2 orderRepository;
        private final HelloTraceV2 trace;
    
        public void orderItem(TraceId traceId, String itemId) {
            TraceStatus status = null;
            try {
                status = trace.beginSync(traceId, "OrderService.orderItem()");
                orderRepository.save(status.getTraceId(), itemId);
                trace.end(status);
            } catch (Exception e) {
                trace.exception(status, e);
                throw e;
            }
        }
    }

  + OrderRepositoryV2

    @Repository
    @RequiredArgsConstructor
    public class OrderRepositoryV2 {
    
        private final HelloTraceV2 trace;
    
        public void save(TraceId traceId, String itemId) {
            TraceStatus status = null;
            try {
                status = trace.beginSync(traceId, "OrderRepository.orderItem()");
                if (itemId.equals("ex")) {
                    throw new IllegalStateException("예외 발생!");
                }
                sleep(1000);
                trace.end(status);
            } catch (Exception e) {
                trace.exception(status, e);
                throw e;
            }
        }
    
        private void sleep(int mills) {
            try {
                Thread.sleep(mills);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

  + 결과

    + 정상 흐름

      ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/7be07ccd-3cd0-4908-b926-6b5c3c3cfe9a)

    + 예외 발생

      ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/4b112aa1-6ca6-446d-abe6-952633505d2c)

    HelloTraceV2에 beginSync라는 메소드를 만들어 파라미터로 TraceId를 받아서 해당 TraceId에 대한 다음 레벨의 TraceId를 생성한다.
    원하는데로 Controller -> Service -> Repository로 갈수록 레벨이 높아져 인덱싱되는 것을 볼 수 있고
    아이디 또한 통일되어 가시성도 높아졌다.

+ 결론
  
  수동으로 로그를 생성하는 클래스를 만들어 스프링 빈을 통해 간단하게 로그를 띄울 수 있었다. 하지만 해당 방법은 모든 작업을 수동으로 해야하고
  레벨과 아이디의 싱크를 맞추려면 비즈니스 로직에 해당 로직 TraceId를 파라미터로 넣어줘야 한다는 단점이 있었다.
