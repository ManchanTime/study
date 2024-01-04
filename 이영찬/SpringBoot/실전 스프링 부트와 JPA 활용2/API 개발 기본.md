# Rest API를 이용하여 이전 JpaShop api를 새로 생성

사실 상 템플릿 엔진보다는 현재 싱글 웹 개발(Vue.js, react) 등 사용하기 때문에 Rest API를 주로 사용
클래스에 @Controller 대신 @Controller와 @ResponseBody가 함께 있는 @RestController 사

### 회원 등록 API

##### v1 엔티티 그대로 사용

+ MemberApiController

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/d247f1c6-c490-4711-9529-61d81ff92e6e)

  @RequestBody를 통해 클라이언트에서 받은 값을 Member 엔티티로 치환하여 받고 @Valid로 엔티티의 유효 조건 적용
  이후 MemberService에서 구현한 join 메소드를 통해서 Member 엔티티를 바로 저장

+ Postman 실행

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/1060c162-2681-4a88-9afe-268403d71a7f)

  Post형식으로 Json으로 데이터 입력

+ 결과

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/c4b90d6a-9be3-4dd2-a274-0960b1b1a41d)

+ 특징
  엔티티를 직접 건드리는 작업이기 때문에 엔티티의 형식이 바뀌거나하는 상황 발생 시 api 딴에서 바로 스펙 오류를 찾을 수 없음
  엔티티에 API 검증을 위한 @Valid가 직접 들어간다. 또한 다양한 API에 맞게 데이터를 자유롭게 꺼낼 수 없다.

##### v2 DTO를 이용

+ MemberApiController

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/a0391f54-3232-4960-b799-871ce9b936b6)

  CreateMemberResponse, CreateMemberRequest Dto를 생성하여 값을 대신 받고 전달함
  @RequestBody로 CreateMemberRequest 폼으로 값을 치환하여 받음
  이 값을 토대로 Member 객체를 생성하고 저장한  CreateMemberResponse 폼으로 객체 정보를 클라이언트에 반환함.

+ Dto

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/4c573c46-0c23-4c55-957b-7340be8cbb4f)

+ Postman 실행

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/4c1b8779-f2ce-4f78-9da3-294f86b01c5f)

  v1과 마찬가지로 Json 타입의 Post형식으로 데이터 저장

+ 결과

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/bec2af3d-d006-4fe1-adf3-e800864f28b1)

+ 특징
  엔티티를 직접 건드리지않고 Dto를 통해서 데이터를 저장하기 때문에 스펙이 바뀔 시 에러를 바로 찾을 수 있어서 유지 보수 면에서 좋다.
  @Valid 로직이 Dto에 적용되기 때문에 엔티티에 적용하지 않아도 된다.
  엔티티와 뷰를 분리할 수 있다.
  API에 따른 다양한 형태로 값을 추출할 수 있다.

### 회원 수정 API

##### 수정할 정보를 DTO로 받기

+ MemberApiController

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/47e1bfee-2d53-49cc-8e80-0801abc21998)

  수정할 대상의 id를 @PathVariable로 받고 수정할 정보를 UpdateMemberRequest라는 Dto를 사용하여 받음

+ Dto

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/00fef53a-a7fa-42d9-97e1-8a4f6c398c3d)

+ Postman 실행

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/2725a8a6-4e1c-4b0c-af59-6328a39fd74c)

  id가 53인 Member의 이름을 hello2-update로 수정

+ 결과

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/d7bce97a-cddd-48d2-8c0b-05c1ca695e68)

+ 특징
  PUT을 사용한다. PUT은 전체 업데이트를 사용할 때 사용하기 때문에 부분 업데이트 시에는 PATCH나 POST가 REST 스타일에 맞다.

### 회원 조회 API

##### v1 엔티티 그대로 조회

+ MemberApiController

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/e34dcb82-74b5-46ff-8f46-ea2fa6929eed)

  전체 Member를 MemberService의 findMembers 메소드로 List로 꺼내서 그대로 클라이언트에게 보내준다.

+ PostMan 실행

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/7aefb06b-bd61-404f-a340-9e501b06d5cb)

+ 특징

  v1 회원 등록과 마찬가지로 엔티티를 클라이언트에게 그대로 노출하고 있어 같은 문제가 발생한다.
  또한 원하는 값만 출력할 수 없어 모든 데이터를 보내주거나 @JsonIgnore를 사용해야하기 때문에 엔티티를 직접 수정해야 한다.

##### v2 Dto 사용

+ MemberApiController

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/b8728fa5-4f0c-406b-b262-f2d526cf22a0)

+ Dto

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/929b3332-547a-416a-a729-ac66fee30e14)

+ Postman 실행

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/10911e9f-b5d4-4e0c-99ce-b03267bc5824)

+ 특징
  DTO를 사용하기 때문에 엔티티에 직접 접근할 필요없이 데이터를 클라이언트에게 보낼 수 있다.
  Result라는 클래스의 제네릭 T 클래스를 사용하여 타입에 유연하게 값을 반환할 수 있다.
  여러 값을 조회할 때 List를 사용하지 않는 것이 좋다. 왜냐하면 실무에서는 주로 한 종류의 값이 아닌 꺼내온 값들의 개수나 타입 등
  여러 종류의 값을 묶어서 보내야할 때가 많기 때문이다. 따라서 T클래스인 data를 생성하여 값을 data로 묶어서 보내고 추가적인 값들까지 한번에 보내는 것이 좋다.
