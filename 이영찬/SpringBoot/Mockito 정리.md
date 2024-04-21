# Mockito

JUnit5, Mocktio

### Mockito

Java 프로그래밍에서의 단위 테스트를 위한 Mocking 오픈 소스 프레임 워크

Mock 객체를 쉽게 생성하고 관리하고 검증할 수 있는 아주 쌈@뽕한 프레임 워크
진짜 객체처럼 작동하지만 사실 개발자가 직접 관리할 수 있다.
Spring Boot는 기본적으로 Mockito, JUnit5 적용 돼있어서 의존성 추가 안해도 됨

### 내가 쓰는 방식 정리

+ Mockito를 Test 클래스에 추가

      @Slf4j
      @ExtendWith(MockitoExtension.class)
      @Transactional
      @SpringBootTest

+ Static import

      import static org.assertj.core.api.Assertions.*;
      import static org.junit.jupiter.api.Assertions.*;
      import static org.mockito.Mockito.*;
      import static semicolon.MeetOn_WhenToMeet.domain.time_table.dto.TimeTableDto.*;
      import static semicolon.MeetOn_WhenToMeet.domain.when_to_meet.dto.WhenToMeetDto.*;

+ when 메소드 이용

      when(cookieUtil.getCookieValue("channelId", request))
                .thenReturn(String.valueOf(channelId));
      when(whenToMeetChannelService.channelExists(channelId, request.getHeader("Authorization")))
                .thenReturn(true);

  내부 메소드에 접근했을 때 실제 값 대신 thenReturn 절에 들어가는 값이 리턴됨
  단, 타입은 원래 리턴과 같아야하고 Static이나 Private 메소드는 사용 불가

+ MockHttpServletResponse / MockHttpServletRequest

  실제 HttpServletResponse, HttpServletRequest 대신 Mock 객체의 Response, Request 사용
  Client의 실제 요청없이 Mock 객체 기반으로 테스팅 가능
  MockCookie를 이용하여 Cookie 세팅도 가능

+ MockedStatic / Private etc

  Mockito의 단점인 static, private(사실 둘다 테스팅 안하는게 좋지만, 세상사 맘대로 되는게 없지) 테스팅 가능
  
      @Test
      void 웬투밋_저장() {
          //given
          Long channelId = 1L;
          WhenToMeetSaveRequestDto whenToMeetSaveRequestDto =
                  WhenToMeetSaveRequestDto.builder().eventName("test").endDate(LocalDate.now())
                          .endTime(1).startTime(1).startDate(LocalDate.now()).build();
          WhenToMeet whenToMeet = WhenToMeet.builder().id(1L).build();
  
          //when
          when(cookieUtil.getCookieValue("channelId", request))
                  .thenReturn(String.valueOf(channelId));
          when(whenToMeetChannelService.channelExists(channelId, request.getHeader("Authorization")))
                  .thenReturn(true);
          try (MockedStatic<WhenToMeet> mockedStatic = Mockito.mockStatic(WhenToMeet.class)) {
              mockedStatic.when(() -> WhenToMeet.toWhenToMeet(whenToMeetSaveRequestDto, channelId))
                      .thenReturn(whenToMeet);
              when(whenToMeetRepository.existsByChannelId(channelId))
                      .thenReturn(false);
              when(whenToMeetRepository.save(whenToMeet))
                      .thenReturn(whenToMeet);
  
              //then
              assertThat(whenToMeet.getId()).isEqualTo(whenToMeetService.saveWhenToMeet(whenToMeetSaveRequestDto, request));
          }
      }

  try~catch로 사용가능한 static 메소드인지 판별하고 when절이랑 비슷한 방식으로 사용 가능
  try~catch 들어가는 것 부터 석 나감, 어지간하면 static 메소드는 테스팅하지 말자
  
   
