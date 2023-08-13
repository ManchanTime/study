# 요구사항

+ 상품 도메인 모델
  + 상품 ID
  + 상품명
  + 가격
  + 수량

+ 상품 관리 기능
  + 상품 목록
  + 상품 상세
  + 상품 등록
  + 상품 수정

  상품 목록
  
  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/86fdba59-9c2c-4285-870b-0f1da075a086)

  상품 상세

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/fd671650-17cd-483d-933e-dceeb9014a1f)

  상품 등록

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/2d86231f-561a-4066-991a-d7d058577e69)

  상품 수정

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/2c77b6a9-2458-4f03-bd69-c932cf765885)

+ 서비스 제공 흐름

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/0b51ddb9-6f5c-4a64-a66a-31c00bbd122e)


### 상품 도메인 개발

Item 객체 클래스

![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/0b89e799-b934-438f-9e24-70f6e5d0572b)

상품 저장소

![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/53c0c15b-e474-4bfc-86ee-0b7f0a083d8c)
![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/7dbb4cdd-4b69-48ac-934e-a1d6f1e1a36f)

테스트 코드는 잘 짜본다.

### 상품 서비스 HTML
부트스트랩을 통해서 CSS 파일을 넣어준다.
아이템 목록은 items.html
아이템 상세는 item.html
아이템 등록은 addForm.html
아이템 수정은 editForm.html
로 생성하여 사용한다. 이 떄 /resources/static에 넣어 스프링 부트가 정적 리소스를 제공한다.
이후 각 파일에 Thymeleaf의 기능을 넣어서 동적으로 작동하게 할 것이다.


### 컨트롤러 초기 설정
앞서 공부한 스프링 부트에서 제공하는 컨트롤러 방식을 사용할 것이다.
  
![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/f1806671-947c-4a7d-82f1-402840ebebde)
![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/b18beada-9d86-487b-a631-b49eedeb5d0a)
![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/b5f7c09e-754d-47d3-8c0e-de699fca7a6e)

@RequestMapping으로 중복되는 경로를 지정하고 @RequiredArgsConstructor로 final이 붙은 멤버변수만 사용하여 생성자를 자동으로 만든다.
@PostConstruct 값으로 초기 값을 저장소에 넣어준다.


### 상품 서비스 HTML Thymeleaf 적용

![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/6d9cba59-a4b7-4d98-b6cc-66c06bdadc6d)

로 타임리프 등록

![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/2952acad-21d6-4058-b3cd-5e1343161fa9)

헤드 태그에 값을 추가해서 css의 경로를 동적으로 변경할 수 있다.

![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/51ef5458-5f92-4af2-a229-497d86d84c5a)

모델로 받은 값을 th:each를 이용해서 자바의 for 처럼 반복문으로 동적 처리할 수 있도록 한다.

이 때 URL 링크는 @{...} 방식을 사용한다.
클릭의 속성을 변경할 때는 th:onclick="|location.href='@{...}'|" 방식을 사용한다.
"|~|" 방식을 리터럴 대체 문법이라 해서 문장에 따로 토큰없이 값을 넣어 문장을 만들 수 있다.

변수는 ${} 방식으로 넣어준다. 이 떄 프로퍼티 접근법을 사용한다. ex) item.name (= item.getName())

HTML의 값을 동적으로 변경할 때는 th:text를 사용한다.

URL 링크를 표현할 때 값을 넣기 위해서는
th:href="@{basic/items/{itemId}(itemId=${item.id})}" 처럼 사용한다.
(...)안에 사용하는 값의 이름이 무엇인지 설정할 수 있다. 또한 쿼리 파라미터도 생성할 수 있다.


### 상품 상세

+ 컨트롤러

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/9adb82f1-64b1-43c3-9079-6500d6d4255b)
  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/118a275d-b76d-433d-8336-56ec2bba60f9)

  @PathVariable로 넘어온 상품ID로 상품을 조회하고 받아온 상품을 모델에 담고 뷰 템플릿을 호출한다.

+ 뷰(HTML)
  
  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/ff6ef296-c606-4854-ac82-25f67d7069fc)

  각 상품의 정보가 출력되는 부분에 thymeleaf를 통해서 값을 동적으로 담아준다.

### 상품 등록

+ 컨트롤러

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/2fd1ed0d-768c-4bbd-8020-08c365abf48d)

+ 뷰

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/e57a3d18-d122-4127-8612-800b46912264)

  <form></form> 태그에 th:action을 추가한다. 이를 통해 현재 URL 데이터를 전송한다.
  GET과 POST 모두 같은 URL을 사용하기 때문이다.

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/30d952ac-3d26-49ae-bc01-96f0508fbd2f)

  취소 태그에 값을 추가해서 취소 클릭시 상품 목록으로 이동한다.

### 상품 등록처리

+ 컨트롤러
 
  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/b6c37bae-629b-4548-9d69-1c8bab9c80d7)

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/a91c205e-5095-4cbd-b0bc-1ee37f6b367b)

  사용할 수 있는 방법이 2가지(@RequestParam 이용, Model 사용) 더 있지만 가장 간단한 방법 2가지만 작성하겠다.
  이 때 Model이 없다면 @ModelAttribute는 자동으로 class 이름의 앞글자(Item)만 소문자로 바꿔서 모델에 이름으로 넣어준다.
  또한 @ModelAtrribute는 생략 가능하다.

### 상품 수정

+ 컨트롤러

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/cd688eac-a14d-4084-b043-01d40ec8f616)

  수정에 필요한 정보를 조회하고 수정용 폼 뷰를 호출한다.

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/5afb3e9f-5222-4772-b1e2-eacc288c416b)

  수정 폼에서 받은 값을 POST 형식으로 상품 수정한다.
  이 떄 redirect:/basic/items/{itemId}를 통해서 웹에 /basic/items/{itemId}를 호출하도록 한다.
  이를 통해서 마지막 뷰 템플릿 대신 상품 상세 화면으로 이동할 수 있다.
  이 때 redirect에는 @PathVariable 값을 사용할 수 있다.

+ 뷰

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/27d2fb58-05a2-44a0-9c0f-f9b449583f19)

  수정할 값을 입력하는 부분에 Thymeleaf로 원래 값을 넣어준다.
  취소 버튼은 상세정보 폼과 비슷하다.

### PRG Post/Redirect/Get

지금 상황에서 상품 등록 처리에서 문제를 발견할 수 있다.

![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/8b581ecf-af96-49f0-86d1-14d59de98b0a)

위 이미지를 잘 살펴보면 상품 저장 시에 내부 호출로 상품 상세 페이지로 갈 수 있다.
하지만 이 때 새로고침을 하게되면 아이디가 바뀌면서 상품이 중복 등록되는 것을 볼 수 있는데 이는 

![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/eadf7ce4-fb9f-4fc6-9547-1561b4ea7e11)

위 이미지에서 상품 등록 폼에서 상품 등록 후 /add 페이지에서 바뀌지 않고 이 때 새로고침을 누른다면
가장 마지막에 했던 요청을 다시하게 되고 이는 상품 등록 POST이기 때문이다.

이는 아까 상품 수정에서 사용했던 redirect를 통해 해결할 수 있다.

![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/46e59bad-b6bb-48cc-a07c-35b3a624f74d)

redirect를 통해서 상품 저장 후에 뷰 템플릿으로 이동하는 것이 아닌 상품 상세 화면으로 이동하게 만든다.

+ 컨트롤러
  
  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/a6a60cec-e39a-476e-b465-f5986fb8e487)

  여기서 return 값으로 "redirect:/basic/items/" + item.getId()를 사용하는 것은 URL 인코딩이 안되기 때문에 위험하다.
  이를 RedirectAttributes를 통해 해결한다.

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/7e301db7-d01c-4ddd-8333-1694c944786b)

  RedirectAttributes를 사용하면 URL 인코딩 뿐만 아니라 pathVariable, 쿼리 파라미터까지 처리할 수 있다.
  또한 status를 등록하여 뷰 템플릿을 동적으로 사용할 수 있다.

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/0bef1647-651f-4798-98f3-3ad3fb5aa128)

  status가 true 일때만 저 문구가 출력된다.(th:if 사용) 
