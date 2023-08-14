# 개요
이전에 서블릿과 jsp의 단점을 개선하기 위해 만들어진 mvc 패턴을 이용한 프로젝트를 만들어보았다. 하지만 그 방법에도 단점이 있었는데
중복 처리가 지나치게 많다는 점이었다.

![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/c46d513c-af25-4b25-ab58-dc1de098ef6e)

위 이미지에서 볼 수 있드시 컨트롤러를 모두 직접 접근하다보니 포워드나 request, reponse 객체 생성에서 중복된 코드가 많다.
이 때문에 프론트 컨트롤러라는 것을 도입하여 컨트롤러에 직접 접근하는 것이 아닌 프론트 컨트롤러를 통해 각 컨트롤러에 접근한다.

![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/e1b910ba-bd4a-4f3a-9489-79841c5ffedc)

+ 프론트 컨트롤러 특징
  + 서블릿 하나로 클라이언트의 요청을 받는다
  + 프론트 컨트롤러가 요청에 맞는 컨트롤러를 찾아 호출한다.
  + 입구를 하나로 설정하여 중복 처리를 제거하여 공통 처리가 가능하다.
 
# v1

+ 구조
  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/570ed473-75fd-4244-8734-bf87d127d353)

  요청이 들어왔을 때 프론트 컨트롤러에서 매핑정보 확인을 통해 컨트롤러를 찾고 컨트롤러를 호출한다. 이후 컨트롤러에서 jsp 포워드를 통해 응답한다.


  + 컨트롤러 인터페이스
  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/f0930a29-5116-4eef-b09e-97d13ceb940b)

  컨트롤러 인터페이스를 통해서 모든 컨트롤러를 구현과 관계없이 로직의 일관성을 가져갈 수 있다.

  + 회원 저장 컨트롤러
  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/09348a65-4a4f-42b7-aa29-b4cc24ea30f5)
  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/03e70ec9-c288-4bdb-aa23-67c8529dae60)

  + 회원 목록 컨트롤러
  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/67db353b-3878-4c1b-b2eb-d7edec96f6d4)
  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/69aae42d-5ee2-4167-95c2-f2b76516cbb1)

  위 두 컨트롤러 모두 기존 서블릿과 로직은 거의 같다.

  + 프론트 컨트롤러
  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/457517b4-8062-41cb-808c-3cf77e65a32a)
  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/517ca2f6-22f6-402d-bf62-7ad818f821cf)

  JSP는 이전 MVC에서 사용했던 것을 그대로 사용하고 controllerMap을 이용하여 서블릿의 url과 해당 컨트롤러를 매핑하여 request에 맞는 서블릿을 사용한다.
  
  => 이는 MVC의 로직을 거의 변형하지 않고 사용하면서 공통 처리 부분을 최대한 줄인 것이다. 때문에 해당 방식에도 중복 코드가 많이 발생하는 것을 볼 수 있다.
    모든 컨트롤러에서 뷰로 이동하는 부분에서 중복이 있고 깔끔하지 않다는 점이다.

# v2

+ 구조
  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/15137ab6-bb39-4904-9322-6503b6b7e6cf)

  요청이 들어왔을 때 매핑 확인과 컨트롤러 호출까지는 같지만 MyView라는 객체를 생성하여 MyView를 통해서 JSP forward를 진행한다는 차이점이 있다.
  이를 통해 모든 컨트롤러에서 직접 jsp에 forward하는 것을 막을 수 있다.

+ MyView

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/f5c464e4-9331-4068-a508-05053fd4e98d)
  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/2fb909f8-a0c1-47b7-a30e-5f9415538da5)

  render() 메소드를 통해서 MyView 객체의 viewPath를 forward하는 것을 볼 수있다.

+ 컨트롤러 인터페이스
  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/85892865-a08b-4ea8-9f40-4a1cb5cb799b)

  v1과 동일하다.

+ 회원 등록
  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/b11944ce-66d7-403f-8ab5-bb44f5bd42cd)

  컨트롤러에서 forward() 하지 않는다. 단순히 MyView 객체를 생성하여 뷰 이름만 반환해주면 된다.

+ 회원 저장
  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/9c06a4ab-86f7-4be0-b38f-a5aed1c0db35)
  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/ace1d164-52ee-4f5f-853f-7940fbe475b3)

  마찬가지로 회원 저장 로직만 실행하고 따로 forward 없이 viewPath만 MyView 객체에 넣어 생성한다.

+ 회원 목록
  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/f80dbf98-6fc2-4015-ae0c-c7e15f858953)
  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/0ccf8388-6e29-4ffa-bedf-015d6ba68b63)

+ 프론트 컨트롤러
  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/1e8407d0-e7ad-45e0-8326-10ccd9616f4c)
  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/76f4b650-b519-4b12-b434-e38324c5cb7f)

  컨트롤러의 반환 타입이 MyView 이기 때문에 프론트 컨트롤러는 컨트롤러 호출 결과로 MyView를 반환 받고 이를 render() 메소드를 통해서 JSP forward를 진행한다.

# v3
  v3에서 주목할 점은 서블릿의 종속성을 제거하는 것과 뷰 이름의 중복을 제거하는 것이다.
  컨트롤러 입장에서 request, response 객체를 굳이 사용하지 않고 있고 요청 파라미터 정보는 자바의 Map을 통해서 넘긴다면
  컨트롤러가 서블릿과 연관되지않고 작동할 수 있다는 것이다. 또한 reques는 객체는 Model 객체를 만들어 대신 사용할 수 있고 
  이렇게 된다면 컨트롤러 입장에서는 서블릿 기술이 전혀 필요하지 않다. 이 때문에 테스트 코드, 구현 코드가 매우 단순해진다.
  뷰 이름의 중복에서는 컨트롤러에서 뷰 이름에 중복이 있고 이를 뷰의 논리 이름을 통해서 조금 더 간단하게 구현할 수 있다.
  뷰의 논리 이름은 프론트 컨트롤러에서 물리 이름으로 바꿔주면 향후 뷰의 폴더 위치가 바뀌더라도 프론트 컨트롤러만 수정해서 쉽게 고칠 수 있다.

+ 구조
![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/02322750-367f-4c5f-a667-9bb7b2d4416a)

위에서 말했던 것처럼 컨트롤러에서 MyView가 아닌 ModelView를 반환하여 이를 viewResolver를 통해 MyView로 반환해준다.

+ ModelView
![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/2ac752b9-f6fa-4d9f-9691-536500fb4377)
![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/f102e046-23de-44f7-bf89-d7c56e9f9709)

뷰의 이름과 뷰 렌더링에 필요한 model 객체를 생성한다. 단순히 map으로 되어있기 때문에 뷰에 필요한 데이터를 key, value로 넣어주면 된다.

+ 컨트롤러 인터페이스
![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/e67f5f9f-fb9a-4083-a517-9c095d908b59)

컨트롤러에서 ModelView를 반환한다.

+ 회원 등록
![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/7b25741b-13a8-424e-b768-91cc935baf7b)

ModelView 객체를 생성할 때 view의 논리적 이름인 "new-form"을 지정한다. 실제 물리적 이름은 프론트 컨트롤러에서 처리한다.

+ 회원 저장
![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/81e920c6-d8b9-46e7-9437-1045778144f2)
![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/e6c91646-659a-4e25-a569-f9ffecf43fd4)

request객체를 직접 사용할 수 없기 때문에 값을 ModelView의 map에 넣어 준다.

+ 회원 목록
![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/4b0b2c67-5230-4e93-9246-7c8487582b4f)
![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/6034c07e-8f72-4636-a4dd-6c86a1abe30e)

회원 저장과 같은 방식이다.

+ 프론트 컨트롤러
![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/7bfc5b96-869f-44db-95c2-cb55edc4c1e4)
![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/010567d2-8553-4209-9d49-894d7ad87823)
![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/aef2a683-12a3-4fe5-97c9-54e139666ce8)
![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/547bbe14-31e3-4c2c-aa6c-3e6c13d79e26)

먼저 createParamMap(request) 메소드를 이용해서 요청 데이터를 모두 Map에 저장하여 paramMap을 생성한다.
이후 ModelView를 paramMap을 넣어 만들어주고 ModelView의 viewName을 통해 MyView를 생성하고 render() 메소드를 호출하여 forward한다.

+ MyView
![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/ffe9b5ee-5381-491a-ba93-a57162e764a8)

MyView에서 받은 model을 request에 담아주고 렌더링해준다.

# v4
앞서 만든 v3는 서블릿 종속성을 제거하고 뷰 경로 중복 제거 등 잘 설계되어있다. 하지만 실제 개발자 입장에서 항상 ModelView 객체를 생성하고 반환하는 것은
번거롭다. 소위 실용성이 떨어진다.
이를 개선해보자.

+ 구조
![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/14a762ad-4bc1-4809-a506-f3e04ac48195)

v3와 구조는 같지만 컨트롤러에서 ModelView가 아닌 ViewName을 반환한다.

+ 컨트롤러 인터페이스
![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/a7ad7586-e56c-4048-8671-60b5d58be466)

ModelView가 아닌 ViewName을 반환하기 때문에 String 값을 반환해준다.

+ 회원 등록
![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/8654ba13-74fc-49f6-80e8-b184b285b1e2)

단순하게 논리 이름인 "new-form"만 반환한다.

+ 회원 저장
![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/6704b50f-beca-4846-9acc-82526287f54b)

모델이 파라미터로 전달되기 때문에 모델을 직접 생성하지 않고 paramMap에서 데이터를 꺼내서 모델에 담기만 하면된다.

+ 회원 목록
![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/2eed89fc-32b6-42d8-9fef-9cab406605fb)

마찬가지다.

+ 프론트 컨트롤러
![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/2f23fe59-f869-4a3d-9cbe-eb992339a5bb)
![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/100c7420-c7d4-4233-9ac7-e064d3f78474)
![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/4a5a6ebb-9c67-4268-b67e-fb6dcdd854fa)
![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/ed0d58d5-5714-447d-8bd4-4ee828f23bac)

v3와 마찬가지로 createParamMap을 통해서 request의 값을 모두 paramMap에 넣어주고 모델을 생성한 후 컨트롤러를 통해서
모델을 채우고 그에 따른 논리 이름을 받는다. 이후 논리 이름으로 viewResolver를 통해서 MyView를 생성하고 포워드한다.

+ 특징
  v4의 컨트롤러는 매우 단순하고 실용적이다. 컨트롤러가 모델을 생성할 필요없이 프론트 컨트롤러에서 만들어 넘겨주어 채우고
  뷰의 논리 이름을 반환하여 단순하게 생성할 수 있다.

# v5
v5는 유연한 컨트롤러라는 특징이 있다. 위에서 본 4가지 컨트롤러 방식을 하나로 통일할 수 없다면 어떻게 해야할까?
결론부터 말하면 어댑터 패턴을 이용하면 된다.

+ 구조
![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/48bdf55e-8884-4ad2-a09a-ee05ee428f8f)

핸들러 구조를 볼 수 있다. 프론트 컨트롤러에서 컨트롤러에 직접 접근하는 것이 아니라 핸들러 어댑터를 통해서 
다양한 종류의 컨트롤러들 중 필요한 컨트롤러에 접근할 수 있다.
먼저 핸들러 어댑터 목록에서 필요한 핸들러 어댑터를 찾고 그에 맞는 핸들러 어댑터를 통해서 컨트롤러에 접근할 수 있다.

+ MyHandlerAdapter
![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/597ec874-d596-4750-a9a2-931371a8172e)

boolean supports를 통해서 해당 handler가 어댑터에서 처리할 수 있는지 판단한다.
이후 handle 메소드를 통해 실제 컨트롤러를 호출하고 ModelView를 반환한다.
어댑터가 프론트 컨트롤러 대신 실제 컨트롤러를 호출하는 것이다.

+ ControllerV3HandlerAdapter
![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/434a8867-cee7-4d16-b4f1-61a728b79216)
![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/a703a4fc-7cff-4c16-8350-7a896094b15e)

v3의 컨트롤러를 처리하는 어댑터이다.
supports에서 ControllerV3를 처리할 수 있도록 설정해준다.
이후 v3 형식에 맞게 코드를 작성한다. v3는 ModelView를 이용하기 대문에 ModelView를 리턴해준다.

+ FrontControllerServletV5

![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/9ee264ff-c5f7-41cb-8013-072ae81df532)
![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/f00b994e-e13f-43a9-b701-c9813168d96d)
![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/83bdda7b-1e66-473d-9370-fea9ad409f9a)
![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/bb4c11a3-f6ab-48d5-addf-904f63fe3981)

먼저 handlerMappingMap과 handlerAdapters를 생성한다.
handlerMappingMap에는 v3의 컨트롤러를 매핑시켜주고 handlerAdapters에는 v3 핸들러 어댑터 객체를 넣어준다.
이후 service 메소드에서 getHandler 메소드를 통해서 받은 request의 uri(key)에 대한 handlerMappingMap의 value 값을 
handler 객체에 넣어준다. 만약 handler 객체가 null이라면 404 status를 보내고 아니라면 handler에 대한 어탭터 값을 getHandlerAdapter()
메소드를 통해 adapter 객체를 생성한다. adapter의 handle 메소드를 통해 ModelView를 생성하고 v3 방식으로 포워드 해준다.


+ ControllerV4HandlerAdapter 추가
![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/a075438c-9171-41bd-bf04-d6500be6297d)
![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/5115e9e7-724c-40b4-9aef-c2c3478d8aa2)

v4핸들러 어댑터와 마찬가지로 supports에서 v4 컨트롤러가 처리될 수 있도록 하고 handle 메소드에서 v4에 맞게 로직을 생성한 후 리턴한다.

+ FrontControllerServletV5 수정
![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/080b4cda-05bb-4b91-865f-2a2aba648a2c)

간단하다. handlerMappingMap과 handlerAdapters에 v4의 컨트롤러 정보와 어댑터 객체를 넣어준다. 이후 
v4든 v3든 각자에 맞는 컨트롤러가 있다면 컨트롤러에 접근하여 포워드해 줄 것이다.
이 때 v4 컨트롤러 어댑터에서는 컨트롤러를 통해서 받아온 viewName 값을 ModelView로 만들어주는데 이 것은 형식을 맞추기 위한 것이다!!
