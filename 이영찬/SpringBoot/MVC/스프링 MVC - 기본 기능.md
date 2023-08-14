# 로깅
로깅은 필요한 정보를 출력하여 확인하기 위한 별도의 로깅 라이브러리를 이용하여 로그를 출력하는 것을 말한다.

+ 라이브러리
  + 롬복
    @SLF4J
    
  + 하드코딩
    private Logger log = LoggerFactory.getLogger(getClass());

   + 호출
     log.info("hello = {}", value);

+ 로그 종류
  + trace
  + debug
  + info
  + warn
  + error
  + 로그 레벨은 trace > debug > info > warn > error 순이며 주로 개발 서버는 debug로 운영 서버는 info를 출력한다.
  + 호출 방법에서 "data = " + data 식으로 자바의 문자열 더하기 연산을 사용하지 않는 이유는 자바의 문자열 더하기 특성 때문인데
    로그가 호출되지 않아도 해당 로그의 문자열 더하기 연산은 실행되어 문자열이 생성되고 저장되기 때문이다.

+ 장점
  + 쓰레드 정보, 클래스 이름과 같은 부가 정보를 함께 볼 수 있고 출력 모양을 조정할 수 있다.
  + 로그 레벨에 따라 출력하고 싶은 로그의 단계를 지정할 수 있다.
  + 콘솔에만 출력하는 것이 아니라 파일, 네트워크 등 로그를 위치에 남길 수 있다. (일별, 특정 용량에 따라 로그 분할도 가능하다.)
  + 성능도 빠르다(위에서 설명한 것처럼)


# 요청 매핑

+ @RestController
  
  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/7faf998c-7a41-4004-b624-cfd9c6ef2c0b)
  
  @Controller는 반환 값이 String 이면 뷰 이름으로 인식되어 뷰 렌더링을 통해 뷰 객체를 생성한다. 하지만
  @RestController는 반환 값을 뷰로 찾는 것이 아니라 HTTP 메시지 바디에 바로 입력한다.
  위 이미지에서 반환 값이 "ok" 메세지로 나온다.

  스프링 부트 3.0 이전 버전에서는 URL 뒤에 '/'가 있든 없든 같은 url로 인식했지만 스프링 부트 3.0 이후부터는 서로 다르게 인식한다!!
  

+ HTTP 메서드 매핑
  저번 "스프링 MVC - 기본 기능"에서 정리했던 것처럼 특정 HTTP 메소드를 지정하고 싶다면
  @RequestMapping(~~, method = ~)를 통해 설정할 수 있다.(GET, HEAD, POST, PUT, PATCH, DELETE)
  또는 @GetMapping, @PostMapping 등의 축약 애노테이션을 사용할 수 있다.

+ PathVariable(경로 변수)
  @RequestMapping을 통해 URL 경로를 템플릿화 할 수 있는데, @PathVariable을 사용하여 매칭되는 부분을 편리하게 조회할 수 있다.
  이 때 경로 변수와 파라미터 이름이 같다면 경로 변수는 생략할 수 있다.

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/bffd6760-6150-4d70-ab27-e43fce92ad64)

+ 특정 파라미터 조건 등록
  특정 파라미터를 추가하고 싶다면 밑 방식을 사용하면된다.
  
  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/e0496f9d-0f7f-4db0-8ab6-ea3d557467ea)

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/d2988b26-e1fb-448f-83de-ab8f5cc4dac7)


+ 특정 헤더 조건 매핑
  파라미터 매핑과 비슷하지만, HTTP 헤더를 사용한다.

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/8981b38a-96a2-4fb8-a7c9-23906e82a906)

+ 특정 미디어 조건 매핑(Content-type)
  HTTP 요청의 Content-Type 헤더를 기반으로 미디어 타입으로 매핑한다.

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/dc3e7a7d-a94f-4885-a959-b242c7f844b1)

  ex)
  
  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/99882a40-d839-42b2-a964-7f14fb5544aa)


+ 특정 미디어 조건 매핑(HTTP 요청 Accept, produce)
  HTTP 요청의 Accept 헤더를 기반으로 미디어 타입으로 매핑한다.

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/6c3ecdbf-f760-4582-b48d-efb020649376)

  ex)
  
  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/f9b11b79-8cbb-4dfa-ab2d-66aa1efdd7c7)


# HTTP 요청 - 기본, 헤더 조회
애노테이션 기반 스프링 컨트롤러는 다양한 파라미터를 지원한다.

ex)

![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/02bc1b7b-1ea9-4dc8-b7f0-90dcf87590a7)

+ HttpMethod: HTTP 메서드를 조회한다.
+ Locale: Locale 정보를 조회한다.
+ @RequestHeader MultiValueMap<String, String> headerMap: 모든 HTTP 헤더를 MultiValueMap 형식으로 조회한다.
+ @RequestHeader("host") String host: 특정 HTTP 헤더를 조회한다.(속성: 필수-required, 기본 값-defaultValue)
+ @CookieValue(value="myCookie", required=false) String cookie: 특정 쿠키를 조회한다.(속성은 위와 동일)
  -> MultiValueMap
      Map과 유사하지만 하나의 키에 여러 값을 받을 수 있다.

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/c85306a8-9e59-4853-815c-5ea1d85eb073)


# HTTP 요청 파라미터 - 쿼리 파라미터, HTML FORM

클라이언트에서 서버로 요청 데이터를 전달할 때는 주로 3가지 방법을 사용한다.
+ GET-쿼리 파라미터
  /url?username=hello&age=20
  메시지 바디없이, URL 쿼리 파라미터에 데이터를 포함해서 전달한다.

+ POST-HTML Form
  content-type: application/x-www-form-urlencoded
  메시지 바디에 쿼리 파라미터 형식으로 전달 username=hello&age=20

+ HTTP message body에 데이터를 직접 담아서 요청
  HTTP API에서 주로 사용, JSON, XML, TEXT
  데이터 형식은 주로 JSON을 사용
  POST, PUT, PATCH

### 요청 파라미터
  + GET, 쿼리 파라미터 전송
    
  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/b4e21f7d-c8bc-4869-88cf-735319a8ea07)

  + POST, HTML Form 전송

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/13e66a42-b51c-4da0-a182-eb5c11a01734)

  GET 쿼리 파라미터 전송 방식이든 POST HTML Form 전송 방식이든 둘다 형식이 같기 때문에 구분없이 조회할 수 있다.
  이것을 간단히 요청 파라미터 조회라고 한다.

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/bb525206-f156-43f7-99b2-81941d66dce0)

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/cbb3bb73-a6fe-4363-b6d0-97c9be955c64)


### HTTP 요청 파마리터-@RequestParam

![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/b44933bb-e61a-47c7-9b3f-90ac7fe0b5fd)

@RequestParam: 파라미터 이름으로 바인딩
@ResponseBody: View 조회를 무시하고, HTTP message body에 직접 해당 내용 입력
-> @RequestParam의 name 속싱이 파라미터 이름으로 사용(request.getParameter("username"))

이때 HTTP 파라미터 이름이 변수 이름과 같다면 생략 가능

![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/c25ee407-b1f8-44cc-974a-d7ee95f9e383)

또 HTTP 파라미터의 타입이 String, int, Integer 등의 단순 타입이면 @RequestParam도 생략가능

![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/dfa51ab9-6ded-4444-a7d3-53cc37317497)

+ 파라미터 필수 여부
  
  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/79cfb4e5-82fb-4738-a160-5d4451148809)

  기본 값은 true이기 때문에 생략 가능 => 만약 required=false인 파라미터가 비었다면 null로 채워주기 때문에
  int와 같은 자바에서 null이 불가능한 값은 사용 불가 => Integer 사용

+ 기본 값 적용

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/9ae05a92-3133-4056-ab03-728c5d90de8c)

  파라미터 값이 없는 경우 defaultValue를 사용하여 기본 값을 적용한다. 이 떄 기본 값이 있으므로 required는 사용하지 않는다.

+ 파라미터 Map으로 조회하기

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/e56ac4a4-4a1d-45f0-9556-a0eb0ff4f08e)

  파라미터를 Map, MultiValueMap으로 조회할 수 있다.

### HTTP 요청 파라미터-@ModelAttribute

실제 개발을 하면 요청 파라미터를 받아서 필요한 객체를 만들고 그 객체에 값을 넣어주어야 한다.

객체 예시

![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/692ddb92-255b-4afe-a473-5e232c1e5522)

롬복의 @Data를 통해 get,set 등을 자동으로 적용해준다.

+ @ModelAttribute 적용-modelAttributeV1

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/a6080660-aacf-4044-bf23-968d26ecae32)

  @ModelAttribute를 통해 객체를 손쉽게 생성하고 사용할 수 있다.
  먼저 스프링 MVC는 @ModelAttribute가 있다면 HelloData 객체를 생성한다. 이후 요청 파라미터의 이름으로 HelloData 객체의 프로퍼티(get, set)를 찾는다.
  해당 프로퍼티의 setter를 호출해서 파라미터 값을 바인딩한다.

+ @ModelAtrribute 생략-modelAttributeV2

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/b3460e42-b748-4543-bb93-410221ee87c2)

  @ModelAttribute 또한 생략이 가능하다. 이 때 @RequestParam도 생략할 수 있기 때문에 혼란이 발생할 수 있다.
  때문에 스프링은 생략시 규칙을 적용한다.
  String, int, Integer같은 단순 타입 = @RequestParam
  나머지는 모두 @ModelAttribute(argument resolver 지정 타입 제외)

# HTTP 요청 메시지
HTTP message body에 데이터를 직접 담아서 요청한다.
이 때 요청 파라미터와는 다르게 HTTP 메시지 바디를 통해서 데이터가 직접 넘어오는 경우는 @RequestParam, @ModelAttribute를 사용할 수 없다.

### 단순 텍스트

HTTP 메시지 바디의 데이터를 InputStream을 사용해서 직접 읽을 수 있다.(v1)

![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/665e0160-f21f-4ace-a1b0-c04958dc25a7)

파라미터가 많이 필요하기 때문에 필요한 파라미터만을 뽑아서 사용할 수 있다.(v2)

![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/405d8df7-b669-4e5b-85bf-1e58d236632e)

이 또한 귀찮기 때문에 HttpEntity를 지원한다.(v3)

![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/71dd108d-2af8-4387-9c7d-4d5cf6e511e8)

HttpEntity: HTTP header, body 정보를 편리하게 조회
-> 메시지 바디 정보 직접 조회 가능
-> 요청 파라미터 조회 기능과 관계 없음(@RequestParam, @ModelAttribute)
-> 응답에도 사용 가능(메시지 바디 정보 직접 반환, 헤더 정보 포함 가능, view 조회x)

HttpEntity를 상속받은 객체들인 RequestEntity, ResponseEntity도 각각 HttpMethod, url정보 추가, 요청에서 사용, HTTP 상태 코드 설정가능, 응답에서 사용의 기능을 제공한다.

이것도 귀찮다.(v4)

![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/28f9cdd3-40f7-4e8f-a36f-b51ae70f2c0d)

@RequestBody를 사용하여 더욱 쉽게 HTTP 메시지 바디 정보를 편리하게 조회할 수 있다.
이 때 헤더 정보가 필요하다면 HttpEntity를 사용하거나 @RequestHeader를 사용하면 된다.

@ResponseBody를 사용하면 응답 결과를 HTTP 메시지 바디에 직접 담아서 전달할 수 있다. 물론 이 때도 view를 사용하지 않는다.

### JSON
JSON 데이터 형식을 조회한다.(v1)

HttpServletRequest를 사용해서 직접 HTTP 메시지 바디에서 데이터를 읽어와 문자로 변환한다.

![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/5df7d4ed-5f38-4a93-bf13-8054a32da584)

복잡하다.(v2)
@RequestBody를 이용한다.

![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/b8c4715b-ca06-489d-85fc-44420f99c7b7)

HTTP 메시지에서 데이터를 꺼내고 messageBody에 저장한다. 이를 objectMapper를 통해 자바 객체로 변환한다.

불편하다(v3)
@RequestBody에 직접 객체를 저장하여 꺼내온다.

![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/aa982135-6657-4ce1-9696-bc045bdba0a8)

HTTP 메시지 컨버터가 문자 뿐만 아니라 JSON 객체도 변환해서 객체 변환 작업을 대신해준다.
단 @RequestBody는 절때 생략이 불가하다. 이는 위에서 봤드시 String, int, Integer같은 단순 타입은 @RequestParam으로
@ModelAttribute는 인식되기 때문에 만약 생략한다면 HelloData가 @ModelAttribute로 인식된다.
따라서 HTTP 메시지 바디가 아니라 요청 파라미터로 처리된다.

# HTTP 응답
응답 데이터를 만드는 방법은 정적 리소스, 뷰 템플릿, HTTP 메시지 3가지가 있다.

### 정적 리소스
스프링 부트는 클래스패스의 /static 디렉토리에 정적 리소스를 저장한다. 이는 해당 파일을 변경 없이 그대로 서비스하는 것이다.

### 뷰 템플릿
뷰 템플릿을 거쳐서 HTML이 생성되고 뷰가 응답을 만들어서 전달한다.
일반적으로 HTML을 동적으로 생성하는 용도로 사용하지만 다른 것들도 가능하다.
경로는 src/main/resources/templates 이다.

뷰 템플릿 호출 컨트롤러

![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/b6595cfa-860c-45cc-b94e-750f301da1cb)

![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/47aa616a-43a5-48aa-b333-b6860e2416a0)

String을 반환하는 경우 - View or HTTP 메시지
@ResponseBody가 없다면 response/hello 로 뷰 리졸버가 실행되어 뷰를 찾고 렌더링 한다.

void를 반환하는 경우
@Controller를 사용하고 HttpServletResponse, OutputStream(Writer)같은 HTTP 메시지 바디를 처리하는 파라미터가 없으면 요청 URL을 참고하여 논리 뷰 이름으로 사용한다.
URL : /response/hello
권장x

HTTP 메시지 바디에서 직접 응답 데이터를 출력할 수 있다.(@ResponseBody, @HttpEntity)

### HTTP API, 메시지 바디에 직접 입력
HTTP API를 제공하는 경우에는 HTML이 아닌 데이터를 전달해야하기 때문에 HTTP 메시지 바디에 JSON같은 형식으로 데이터를 실어 보낸다.

HttpServletResponse response 사용하기(v1)

![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/ba011b66-7f7e-47c5-8441-4a0b6b7ccc12)

ResponseEntity 사용하기(v2)

![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/ba2db62e-b91a-4f12-9144-892132b4d8c2)


String 사용하기(v3)

![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/61f7fbb3-ecbb-4dc0-a918-2ce9951412ea)

객체 보내기
ResponseEntity 사용(v1)

![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/b1488073-9bae-4ddd-b776-8de2d37963c5)

객체 자체를 리턴하기(v2)

![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/172726f2-157b-4f1e-ae81-53e1e4330452)

@RestController -> 해당 클래스의 컨트롤러 모두 @ResponseBody가 적용된다.
@RestController = @ResponseBody + @Controller

# HTTP 메시지 컨버터
뷰 템플릿으로 HTML을 생성해서 응답하는 것이 아니라, HTTP API처럼 JSON 데이터를 HTTP 바디에서 직접 읽거나 쓰는 경우 HTTP 메시지 컨버터를 사용한다.

![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/30ffd716-6507-4f31-adb2-b7247f3112aa)

@ResponseBody를 사용할 때 viewResolver 대신 HttpMessageConverter가 동작하여 기본 문자는 StringHttpMessageConverter가 기본 객체는 MappingJackson2HttpMessageConverter가 동작한다.
HTTP 메시지 컨버터는 HTTP 요청, HTTP 응답 둘 다 사용된다. canRead(), canWrite()가 메시지 컨버터가 해당 클래스, 미디어 타입을 지원하는지 체크하고
read(), write()가 메시지 컨버터를 통해 메시지를 읽고 쓰는 기능을 한다.

### HTTP 요청 데이터 읽기
HTTP 요청이 오고 컨트롤러에서 @RequestBody, HttpEntity 파라미터를 사용한다.
메시지 컨버터가 메시지를 읽을 수 있는지 확인하기 위해 canRead()를 호출한다.
이를 만족한다면 read()를 호출하여 객체를 생성하고 반환한다.

### HTTP 응답 데이터 생성
컨트롤러에서 @ResponseBody, HttpEntity로 값이 반환된다.
메시지 컨버터가 메시지를 쓸 수 있는지 확인하기 위해 canWrite()를 호출한다.
canWrite() 조건을 만족하면 write()를 호출하여 HTTP 응답 메시지 바디에 데이터를 생성한다.

### 요청 매핑 핸들러 어뎁터 구조
이는 애노테이션 기반의 컨트롤러, @RequestMapping을 처리하는 핸들러 어댑터인 RequestMappingHandlerApdater에 있다.

동작 방식

![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/c9101f81-d7fe-4e27-b8c3-96fd73348064)

ArgumentResolver를 통해서 HttpServletRequest, Model, @RequestParam, @ModelAttribute와 같은 애노테이션, @RequestBody, HttpEntity같은
HTTP 메시지를 처리하는 부분까지 처리가능한다.
애노테이션 기반 컨트롤러를 처리하는 RequestMappingHandlerApdater는 바로 이 ArgumentResolver를 호출하여 컨트롤러가 필요로 하는 다양한 파라미터의 값을 생성한다.
파라미터 값이 준비되면 컨트롤러를 호출하면서 값을 넘겨준다.

ReturnValueHandler 또한 ArgumentResolver와 비슷하게 응답 값을 변환하고 처리해준다. 컨트롤러에서 반환되는 값이 모두 이를 통해 변환되고 처리된다.

### HTTP 메시지 컨버터 위치

![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/6ff6e050-7f84-4192-8eb6-232405373353)

@RequestBody도 컨트롤러가 필요하는 파라미터의 값에 사용된다. 또한 @ResponseBody의 경우도 컨트롤러의 반환 값을 이용한다.

요청의 경우: ArgumentResolver
응답의 경우: ReturnValueHandler
이를 모두 인터페이스로 제공하기 때문에 필요하면 언제든 기능을 확장할 수 있다. 자주 하지는 않지만 기능 확장은 WebMvcConfigurer를 상속받아 스프링 빈으로 등록하면 된다.
