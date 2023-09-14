# FrontController 도입
![image](https://github.com/LAB-2023/LAB_study/assets/129240433/c4ee643f-c5c3-422e-8f64-b03e3efb6a88)


# V1
![image](https://github.com/LAB-2023/LAB_study/assets/129240433/cebefcb6-f0d9-4cc6-8803-cc4d48919850)

        controllerMap.put("/front-controller/v1/members/new-form",new MemberFormControllerV1()); 선언 후
        
        String requestURI = request.getRequestURI();
        ControllerV1 controller = controllerMap.get(requestURI);
        controller.process(request,response);

        requestURI를 조회해서 실제 호출할 컨트롤러를 controllerMap 에서 찾는다.
        컨트롤러를 찾고 controller.process(request, response); 을 호출해서 해당 컨트롤러를 실행한다.

# V2
![image](https://github.com/LAB-2023/LAB_study/assets/129240433/7a6b1349-4d65-4d1a-914f-d8ca54021b6f)

    MyView 객체에서 생성하면
    public void render(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        RequestDispatcher dispatcher = request.getRequestDispatcher(viewPath);
        dispatcher.forward(request, response);
    }
        이 부분 중복을 제거 할 수 있다.
        String viewPath = "/WEB-INF/views/new-form.jsp";
        RequestDispatcher dispatcher = request.getRequestDispatcher(viewPath);
        dispatcher.forward(request,response);

        마지막에 
        return new MyView("/WEB-INF/views/new-form.jsp"); 추가 한다.
---
# V1 & V2
        컨트롤러에서 서블릿에 종속적인 HttpServletRequest를 사용했다.
        그리고 Model도 request.setAttribute() 를 통해 데이터를 저장하고 뷰에 전달했다.
        서블릿의 종속성을 제거하면 Model을 직접 만들고, 추가로 View 이름까지 전달하는 객체를 만들어야한다.
        (컨트롤러에서 HttpServletRequest를 제거하면 직접 request.setAttribute() 를 호출할 수 도 없다. 따라서 Model이 별도로 필요하다.)
---
# V3 (framework에 종속적이지 servlet에 종속적이지 않다.)
![image](https://github.com/LAB-2023/LAB_study/assets/129240433/1a27878f-22f7-475f-89c2-00e389068344)

        HttpServletRequest를 제거하였기 때문에 ModelView 객체를 만들어야한다.

        FrontController에서 코드를 추가하고
        private static MyView viewResolver(String viewName) {
          return new MyView("/WEB-INF/views/" + viewName + ".jsp");
        }

        컨트롤러에
        ModelView("이름") 추가.

# V4
![image](https://github.com/LAB-2023/LAB_study/assets/129240433/a357a657-1504-4a12-af70-3117a35c1bf8)

      컨트롤러가 ModelView 를 반환하지 않고, ViewName만 반환한다.
      인터페이스에 ModelView가 없고, model 객체는 파라미터로 전달되기 사용하면 되고, 결과로 뷰의 이름만 반환해주면 된다.
      String process(Map<String, String> paraMap, Map<String, Object> model);

      프론트 컨트롤러에
      Map<String, Object> model = new HashMap<>();
      모델 객체를 프론트 컨트롤러에서 생성해서 넘겨준다. 컨트롤러에서 모델 객체에 값을 담으면 여기에 그대로 담겨있게 된다.
      
      컨트롤러에
      return "new-form"; 만 추가하면 끝이다.

      컨트롤러가 직접 뷰의 논리 이름을 반환하므로 이 값을 사용해서 실제 물리 뷰를 찾을 수 있다.
      String viewName = controller.process(paramMap, model);
      MyView view = viewResolver(viewName);

      

# V5 (V3, V4 방식 둘다 적용가능)
![image](https://github.com/LAB-2023/LAB_study/assets/129240433/5b73cfb5-4ec7-4f9f-a60e-f1fc154eb7fa)

핸들러 어댑터: 중간에 어댑터 역할을 하는 어댑터가 추가되었는데 이름이 핸들러 어댑터이다.

어댑터 역할을 해주는 덕분에 다양한 종류의 컨트롤러를 호출할 수 있다.

핸들러: 컨트롤러의 이름을 더 넓은 범위인 핸들러로 변경했다. 

어댑터가 있기 때문에 꼭 컨트롤러의 개념 뿐만 아니라 어떠한 것이든 해당하는 종류의 어댑터만 있으면 다 처리할 수 있기 때문이다.

        ModelView handle(HttpServletRequest request, HttpServletResponse response, Object handler) throws SecurityException, IOException;
        어댑터는 실제 컨트롤러를 호출하고, 그 결과로 ModelView를 반환해야 한다.
        실제 컨트롤러가 ModelView를 반환하지 못하면, 어댑터가 ModelView를 직접 생성해서라도 반환해야 한다.
        이전에는 프론트 컨트롤러가 실제 컨트롤러를 호출했지만 이제는 이 어댑터를 통해서 실제 컨트롤러가 호출된다.


        handler를 컨트롤러 V3로 변환한 다음에 V3 형식에 맞도록 호출한다.
                ControllerV3 controller = (ControllerV3) handler;
        
        supports() 를 통해 ControllerV3 만 지원하기 때문에 타입 변환은 걱정없이 실행해도 된다.
            public boolean supports(Object handler){
                return (handler instanceof ControllerV3);
            }
        
        ControllerV3는 ModelView를 반환하므로 그대로 ModelView를 반환하면 된다.


        frontController에
        매핑 정보의 값이 ControllerV3 , ControllerV4 같은 인터페이스에서 아무 값이나 받을 수 있는 Object 로 변경되었다.
        private final Map<String, Object> handlerMappingMap = new HashMap<>();
            
        생성자 핸들러 매핑과 어댑터를 초기화(등록)한다.
            public FrontControllerServletV5(){
                initHandlerMappingMap();
                initHandlerAdapters();
            }

         V3, V4 등록
                private void initHandlerAdapters() {
                        handlerAdapters.add(new ControllerV3HandlerAdapter());
                        handlerAdapters.add(new ControllerV4HandlerAdapter());
                }

          컨트롤러(Controller) 핸들러(Handler)
          이전에는 컨트롤러를 직접 매핑해서 사용했다. 그런데 이제는 어댑터를 사용하기 때문에, 컨트롤러 뿐만
          아니라 어댑터가 지원하기만 하면, 어떤 것이라도 URL에 매핑해서 사용할 수 있다. 그래서 이름을
          컨트롤러에서 더 넒은 범위의 핸들러로 변경했다

          handler 어탭터 조회
          handler 를 처리할 수 있는 어댑터를 adapter.supports(handler) 를 통해서 찾는다.
          handler가 ControllerV3 인터페이스를 구현했다면, ControllerV3HandlerAdapter 객체가
          반환된다.
            for (MyHandlerAdapter adapter : handlerAdapters) {
                if(adapter.supports(handler)){
                        return adapter;
                    }
                }


        어댑터 호출
        ModelView mv = adapter.handle(request, response, handler);
        어댑터의 handle(request, response, handler) 메서드를 통해 실제 어댑터가 호출된다.
        어댑터는 handler(컨트롤러)를 호출하고 그 결과를 어댑터에 맞추어 반환한다.

        컨트롤러 V4
        어댑터 변환(어댑터가 필요한 이유)
                ModelView mv = new ModelView(viewName);
                mv.setModel(model);
                return mv;
                
                위의 ControllerV4는 뷰의 이름을 반환한다. 
                하지만 어댑터는 뷰의 이름이 아니라 ModelView 를 만들어서 반환해야 한다.
                ControllerV4 는 뷰의 이름을 반환했지만, 어댑터는 이것을 ModelView로 만들어서 형식을 맞추어 반환한다.
        
