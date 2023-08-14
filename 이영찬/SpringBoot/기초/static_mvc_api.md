# Static / Mvc / API 정리

+ ### Static(정적 컨텐츠)
##### 정적 컨텐츠는 말 그대로 정적인 컨텐츠로 따로 로직이나 데이터 값을 다루는 파일이 아닌 화면에 띄우기만 해주는 컨텐츠이다. 
##### 따로 컨텐츠를 resources.static 폴더에 넣어 사용한다.
##### 스프링 컨테이너에서 먼저 컨텐츠관련 컨트롤러가 없는 것을 파악하고 html 파일을 바로 웹브라우저로 보내 화면에 띄워준다.
![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/e267a818-e547-4df1-9cd1-18d30355938c)



+ ### Mvc
##### Mvc는 Model, View, Controller의 줄임말로 Model은 말그대로 사용하고자하는 모델을 뜻하고 View는 화면에 나타야하는 컨텐츠(html), Controller는 컨텐츠에서 사용될 로직들을 말한다.
##### Controller
![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/eb375925-ad49-47b8-85a6-bc3bcab619c3)
+ ##### View
![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/85b721ab-a735-401b-9039-1f1ee51c62d7)
##### 이때 웹브라우저에서 받은 오더를 스프링컨테이너에서 먼저 컨트롤러의 유무를 파악하고 컨트롤러가 있다면 오더에 맞는 return 값에 대한 model을 찾는다.
##### 이후 찾은 모델을 viewResolver가 템플릿 엔진을 처리하여 HTML로 변환 후 웹브라우저에 컨텐츠를 띄워준다.
![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/969d3a51-993e-4e87-a254-e29a2cb9d281)



+ ### API
##### API는 @ResponseBody 어노테이션을 사용한다. 이를 사용할 때 viewResolver를 통해서 템플릿 엔진을 변환하여 웹브라우저에 반환하는 것이 아닌 HTTP의 BODY에 값 자체를 담어서 반환한다.
+ ##### 실행
![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/5ec8a3ef-2d52-4ece-b923-39411acbf817)
+ ##### 결과
![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/ab6e264f-a99c-45c9-a76d-0204c9229f16)
##### 이렇게 값만 사용하는 이유는 무엇인가? 바로 객체를 Json 또는 Xml로 변환하기 위함이다.
+ ##### 실행
![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/a75bf9b9-1cf9-45f5-83e1-d58f30000e79)
+ ##### 결과
![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/590e1767-0caa-4023-afe8-271fc0b6073f)
##### 키 값에 따른 밸류 값이 잘 나오는 것을 확인할 수 있다.

![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/90db56b5-cc8c-4414-9338-e75a828c02e8)
##### 먼저 @ResponseBody 어노테이션을 사용하여 API인 것을 나타내고 이 때 HTTP의 BODY에 문자 내용을 직접 담아 반환한다.
##### 이후 컨트롤러에서 보낸 값을 viewResolver가 아닌 HttpMessageConverter가 동작하여 기본 문자는 StringHttpMessageConverter가 기본 객체는 MappingJackson2HttpMessageConverter(주로 Json 사용)가 byte 처리 등 기타 여러 작업을 처리하여 리턴해준다.
---
