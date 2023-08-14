# 서블릿
앞에서 정리했던 것처럼 서블릿은 

![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/496b524b-d281-49c0-a507-fe956c8d9883)

을 기반으로 하여 request, reponse 객체를 이용해 client와 request, reponse를 주고받는다. 여기서

![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/f6667c99-31a2-4ba2-9cbe-8189e8efdb53)

처럼 response 객체를 통해 기본적인 reponse를 작성하고 PrintWriter 객체를 이용하여 html 코드를 작성해 사용한다.
기본적으로 설정해두었던 MemberRepository와 Member를 통해서 회원 저장, 회원 목록 출력의 기능을 수행할 수 있다.

### 회원 등록

![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/50c6d55d-901a-40b2-91dc-df04d2c1008a)
response 기본설정

![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/9fa093b8-bbc6-44e5-9fee-99ead9a0420b)
html코드

### 회원 저장

![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/04fe1393-e3b5-4792-8c00-e5bf00837b41)
response 기본설정

![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/edc18cff-75d8-404a-8ea8-8719a24463b8)
html코드

### 회원 목록

![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/6a8f4837-aa72-4f89-a1c8-aa6ed0fec210)
response 기본설정

![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/737de4c9-634e-452f-b7fe-c2147776d367)
![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/5f5e35d2-3bc7-4c50-a780-0b7673044be2)
html 코드

### 특징
따로 html 파일없이 서블릿과 자바 코드만으로 HTML을 작성할 수 있다. 이 때 서블릿으로 원하는 HTML 동적으로 생성할 수 있다.
여러 조건을 추가하며 HTML을 바꿀 수 있다는 장점이 있지만 매우 복잡하고 비효율적인 것을 볼 수 있다.
자바 코드로 HTML을 만드는 것보다 HTML 문서에 동적으로 변경할 수 있는 자바 코드를 넣는 것이 더 효율적일 것이다. 이 때문에 템플릿 엔진이 등장했다.
JSP, Thymeleaf, Freemarker, Velocity 등이 있다.

# JSP
JSP는 앞서 말했던 것처럼 서블릿으로 HTML 코드를 모두 자바 코드로 작성하는 것이 비효율적이기 때문에 등장한 것이다. 따라서 HTML 파일을 따로 작성해야한다.

### 회원 등록

![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/055d2766-cb41-4931-bc25-406641a727e2)

단순한 정적 HTML이기 때문에 기본적인 HTML과 별로 차이가 없다. JSP는 서버 내부에서 서블릿으로 변환되어 사용하는데 이 때 형태는 
서블릿 코드와 거의 비슷한 모습으로 변환된다.

### 회원 저장

![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/9895997a-06b4-4831-8202-9e459e4faaf7)
![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/a1cd5f84-a7f4-4d3e-a3fe-a7728b5bdaa1)

회원 저장 시에는 받은 데이터를 저장하는 MemberRepository의 save 메소드를 사용해야하기 때문에 자바 코드는 필수적이다.
JSP에서는 <% ~ %>를 통해서 실제 자바 코드를 작성하여 실행할 수 있고 <%@ ~ %>를 통해서 실제 클래스나 라이브러리를 import 할 수 있다.
위 코드에서 확인할 수 있드시 <% ~ %> 사이에서 서블릿에서 작성한 로직과 같은 로직을 사용하는 것을 볼 수 있고 밑에는 HTML 코드를 그대로 사용하는 것을 볼 수 있다.

### 회원 목록

![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/7c01519c-2852-472e-ab55-5b74bff001b5)
![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/5235e3b5-9ded-48fc-aa39-eb65c5ef14a4)

위 회원 저장 코드와 마찬가지로 윗 부분에서는 서블릿에서 작성한 로직 부분과 같고 밑에 HTML 코드는 서블릿의 HTML 코드와 같은 것은 볼 수 있다.

### 특징
서블릿에서는 모든 로직과 HTML 코드를 자바 코드에 섞어서 작성하여 지저분하고 복잡했다.
JSP를 사용하면서 뷰를 생성하는 HTML 부분과 자바 코드를 분리하여 HTML을 작성하고 중간중간 필요한 부분에만 자바코드를 사용하여 동적으로 적용하였다.
하지만 JSP도 단점이 있는데 회원 저장 JSP를 보면 절반 정도는 비즈니스 로직을 위한 자바 코드이고 나머지 절반은 HTML 코드인 것을 확인할 수 있다.
아직 작은 프로젝트임에도 불구하고 다양한 자바 코드가 모두 JSP에 노출되어있다. 이는 JSP가 너무 많은 역할을 하는 것이고 이는 유지보수에서 상당한 비용을 가져올 것이다.
이 때문에 비즈니스 로직과 HTML를 분리하는 방법이 고안되었는데 이를 MVC 패턴이라고 한다.

### MVC 패턴
앞서 말했던 것처럼 JSP이나 서블릿으로 모든 것을 처리하려하면 너무 많은 역할을 한곳에서 처리하게 되고 이는 유지보수의 어려움으로 이어진다.
특히 HTML과 비즈니스 로직은 변경 라이프 사이클이 다르기 때문에 수정할 때 서로에게 영향을 줄 가능성이 낮다. 때문에 한 곳에서 처리하기에는 비효율 적이다.
특히 JSP는 뷰 렌더링 최적화 템플릿이기 때문에 뷰 파트만 담당하는 것이 좋다.

MVC 패턴은 Model, View, Controller라 해서 하나의 서블릿이나 JSP에서 처리하던 것을 Controller와 View라는 영역으로 역할을 나눈 것이다.

+ Controller
  컨트롤러는 HTTP 요청을 받아 파라미터를 검증하고 비즈니스 로직을 실행한다. 이후 뷰에 전달한 결과를 모델에 담는다.
+ Model
  뷰에 출력할 결과 데이터를 담는다. 이 때문에 뷰는 비즈니스 로직이나 데이터 접근에 관여하지 않고 뷰 렌더링에 치중할 수 있다.
+ View
  모델에 담겨있는 데이터를 사용하여 뷰 렌더링에 집중한다.

이 때 컨트롤러에 비즈니스 로직을 담아서 사용하면 좋겠지만 컨트롤러가 너무 많은 역할을 하게 된다. 이 때문에 일반적으로
비즈니스 로직은 service 계층에서 처리하고 컨트롤러는 서비스를 호출하는 역할만 하게된다.

![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/df18f788-81f0-4517-a79e-88dd542191f4)

### 회원 등록
+ 컨트롤러
  
  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/ffbf1598-ac02-4050-b3c1-86d17f1eac74)

  서블릿을 이용한다.
  이 때 viewPath(jsp경로)에서 WEB-INF는 컨트롤러를 거치지않으면 jsp 파일에 접근할 수 없도록 하는 것이다.
  dispatcher.forward()를 통해서 다른 서블릿 or JSP로 이동할 수 있다.
  forward는 서버에서 서버로 이동하는 것이기 때문에 redirect처럼 클라이언트에서 인지하는 것이 아니고 경로 또한 바뀌지 않는다.
  
+ 뷰
  
  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/e83855d1-5fda-4b61-a4ad-43819c6fcb71)

  뷰 렌더링에는 jsp를 사용하여 간단하게 정리 할 수 있다.

### 회원 저장
+ 컨트롤러

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/806a81c6-e4e0-471d-a0ee-48038f531746)
  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/cb845157-c940-4ecb-b9e8-f73d8bec8d41)

  마찬가지로 서블릿을 이용한다. 
  
+ 모델
  HttpServletRequest 객체를 Model로 사용하여 setAttribute(키, 밸류)를 통해 데이터를 보관한다.
  
+ 뷰

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/f12674c5-4241-45f9-9cfa-daf24450aa1e)
  
  JSP를 이용하여 작성하고 이 때 JSP에서 제공하는 ${}를 통해서 서블릿에서 받아온 모델을 이용한다.

### 회원 목록
+ 컨트롤러

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/956c5f25-d379-4fc5-8a52-68e1f6163cc0)

  서블릿을 이용한다.
  
+ 모델

  마찬가리고 request 객체를 members 리스트를 모델에 보관한다.
  
+ 뷰

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/3fa11fb6-f4ed-41a5-bd46-b967b63a0a1f)
  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/87186dd0-8afd-43ee-a895-b453c2f1e821)

  모델에 담아둔 members를 JSP가 제공하는 taglib 기능을 사용하여 반복 출력한다. 덕분에 <% ~ %>를 사용하여 for문을 통한 자바코드 반복문을 작성하지 않아도 된다.

### 한계
MVC 패턴을 적용하여 컨트롤러와 뷰의 역할을 명확하게 구분할 수 있다. 뷰는 HTML 코드만을 컨트롤러는 자바코드만을 사용할 수 있게 된 것이다.
하지만 이 또한 단점이 있다.

+ 포워드 중복
  View로 이동하는 코드에서 항상 포워드가 중복 호출되는 것이다.

+ ViewPath 중복
  prefix : /WEB-INF/views/, suffix:.jsp가 중복되는 것이고 이 때 템플릿 엔진을 교체한다면 모든 코드룰 변경해야한다.

+ 사용하지 않는 코드
  response 객체와 같은 사용하지 않는 코드 또한 작성해야한다.

+ 테스트 코드 작성이 어려움
  
+ 공통 처리가 어려움
  기능이 복잡해질 수 록 컨트롤러에서 공통으로 처리할 부분이 늘어나고 이를 단순 공통 메소드로 뽑기에는 호출 문제 또한 중복이다.

  => 프론트 컨트롤러(수문장)를 통해 해결한다. 이를 도입하여 컨트롤러 호출 전에 공통 기능을 처리해주는 전처리 역할을 해줄 수 있다.
