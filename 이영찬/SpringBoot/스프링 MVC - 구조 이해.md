# 스프링 MVC 전체 구조
![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/911bbcbd-41c6-487a-b7c7-bd9b12ca7957)
![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/4d95b6ca-8263-48dd-8e47-6a0f4c1c2149)

앞서 작성한 MVC 프레임워크 만들기에서 나온 구조 이미지와 별로 차이가 없다.
프론트 컨트롤러가 DispatcherServlet으로 바뀐 정도이다.
물론 DispatcherServlet 또한 프론트 컨트롤러 패턴으로 구현되어 있다.
DispatcherServlet도 부모 클래스에서 HttpServlet을 상속받아 서블릿으로 동작한다.
이 때 서블릿으로 등록되면서 모든 경로에 대해서 매핑되고 이후 더 자세한 경로가 매핑되었을 때 자세한 경로가 우선순위가 높아 자세한 경로로 매핑된다.

+ 요청 흐름
  서블릿이 호출되면 HttpServlet이 제공하는 service()가 호출된다.
  스프링 MVC에서 DispatcherServlet의 부모인 FrameworkServlet에서 service() 오버라이드 해두었고
  이를 이용하여 DispatcherServlet.doDispatcher()가 호출된다.

+ doDispatch()
  여기서는 먼저 핸들러를 조회하고 찾은 핸들러를 기반으로 핸들러 어댑터 조회를 시작한다. 이후 받은 어댑터를 실행하여 ModelAndView를 얻는다.
  받은 모델을 기반으로 렌더링을 시작하는데 여기서 뷰 리졸버를 통해서 뷰를 찾고 뷰 객체를 만들어 렌더링한다.

+ 강점
  DispatcherServlet이 인터페이스로 선언되어 코드 변경없이 기능을 변경, 확장할 수 있다.
  이를 통해 커스텀 컨트롤러 작성이 가능하다.

+ 주요 인터페이스
  핸들러 매핑, 핸들러 어댑터, 뷰 리졸버, 뷰

# 핸들러 매핑과 핸들러 어댑터

+ 과거 버전 스프링 컨트롤러
  
  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/a974adb1-f28e-4e6c-a67a-4e4a70c88b57)

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/ccad7228-b654-47ee-85d5-a6e5959694f9)

  어노테이션을 따로 사용하지 않고 컴포넌트 어노테이션만으로 스프링 빈을 등록한 뒤 컨트롤러 인터페이스를 상속받아 작성하였다.

+ 방식
  핸들러 매핑으로 컨트롤러를 찾는다. 이후 핸들러 어댑터를 통해서 핸들러를 실행할 어댑터를 찾아 실행한다.
  스프링에서 이미 필요한 핸들러 매핑과 핸들러 어댑터를 구현해두어 개발자가 직접 만드는 일은 거의 없다!!

+ 핸들러 매핑
  
  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/c54cf555-7113-4f90-bda2-32ab8d7230ae)

+ 핸들러 어댑터
  
  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/6df81d57-a8a6-406c-9165-931fb6fc4f34)

  위 순서를 기반으로 각각 핸들러와 어댑터를 찾는다. 순서는 주로 사용하는 순으로 되어있다.

+ 뷰 리졸버
  
  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/78286098-3053-4ecb-b7d0-e4b352d75328)

  View를 사용할 수 있게 되었지만 실제로는 에러가 발생한다.
  
  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/95bb58f0-6d66-4f83-b9ce-90fe8d6d8ccc)
  를 추가하여 에러를 해결한다.

  스프링 부트는 InternalResourceViewResolver라는 뷰 리졸버를 자동으로 등록하는데 이 때 위에서 작성한 prefix와 suffix를 사용한다.
  이를 사용하는 이유는 실제 코드에서 경로를 전체("/WEB-INF/views/new-form.jsp")를 추가하는 것이 비효율적이기 때문이다.

  + 작동방식
    뷰 리졸버는 인터페이스이기 때문에 뷰에 필요한 뷰 리졸버를 찾아야한다. 이 때문에 핸들러 매핑과 같은 방식으로 자주 사용하는 뷰 리졸버 순으로 정렬되어있고
    여기서 맞는 뷰 리졸버를 찾는다.(앞선 코드는 jsp 기반이기 때문에 InternalResourceViewResolver를 반환한다.)

  + 순서
    핸들러 어댑터를 통해 new-form 이라는 논리 뷰 이름을 획득하고 new-form에 맞는 뷰 리졸버를 호출하여 뷰를 만들어 포워드하고 렌더링한다.

# 스프링 MVC

+ v1(@RequestMapping)
  이 애노테이션을 사용하여 매우 유연하고 실용적인 컨트롤러 작성이 가능하다.

  + 회원 등록
    
    ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/f55b50d6-6629-46bf-b9f2-261f8b947dd5)

    @Controller를 통해서 자동으로 빈 생성이 가능하고 @RequestMapping을 통해 요청 정보를 매핑한다.
    이후 ModelAndView에 담아 반환한다.

  + 회원 저장
    
    ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/714b306d-8b93-4fa4-b465-6acdc57e6978)
    ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/cc0725be-bd9f-46f5-9c74-63e5012333e1)

  + 회원 목록
    
    ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/22702fe4-fde6-4c53-9f98-1e88fd9c6d88)

  + 단점
    @RequestMapping이 클래스 단위가 아니라 메서드 단위로 적용되어 있다.

+ v2(클래스 통합)

  + 컨트롤러v2
    
    ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/700822ba-94b3-41a4-afe4-07d36e767fc9)
    ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/d6dfc1fa-e3cc-405e-8044-5c9bf9622f74)

    하나의 컨트롤러에 모든 기능을 통합할 수 있다. 또한 @RequestMapping("/springmvc/v2/members")로 중복되는 경로를 제거하였다.

  + 단점
    반환값이 ModelAndView이기 때문에 추가로 작성할 코드가 많다.

+ v3(String 값으로 반환)

  + 컨트롤러v3
    
    ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/845da0f0-44e9-4e40-b021-02d34c6765b6)
    ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/2d4efd66-589c-435e-b435-bf084d6cc70d)

    Model을 파라미터로 받아서 직접 모델을 생성할 필요가없다.
    ViewName을 반환한다. 이 때 필요한 요청 파라미터는 @RequestParam을 통해서 받을 수 있다.
    또한 앞서 사용한 @RequestMapping은 요청이 GET인지 POST인지 확인할 방법이 없어 모두 사용가능하게 하거나
    @RequestMapping(~~, methond = ~)를 통해 확인하였다. 하지만 v3에서는 @GetMapping, @PostMapping을 통해 확실하게 요청 종류를 확인할 수 있다.
    @GetMapping, @PostMapping은 @RequestMapping을 내부에서 가지고 있다.
