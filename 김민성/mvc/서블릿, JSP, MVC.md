#  redirect vs forward
리다이렉트는 실제 클라이언트(웹 브라우저)에 응답이 나갔다가, 클라이언트가 redirect 경로로 다시요청한다.

따라서 클라이언트가 인지할 수 있고, URL 경로도 실제로 변경된다.

반면에 포워드는 서버 내부에서 일어나는 호출이기 때문에 클라이언트가 전혀 인지하지 못한다.

# 회원 등록(서블릿)
        Member member = new Member(username,age);
        Member 객체를 만든다.
        Member 객체를 MemberRepository를 통해서 저장한다.

# 회원 조(서블릿)
        List<Member> members = memberRepository.findAll();
        memberRepository.findAll() 을 통해 모든 회원을 조회한다.
        
        for (Member member : members)
        회원 목록 HTML을 for 루프를 통해서 회원 수 만큼 동적으로 생성하고 응답한다.

# 회원 등록(jsp)
      저장 후 확인 할 때
      <li>id=<%=member.getId()%></li>
      <li>username=<%=member.getUsername()%></li>
      <li>age=<%=member.getAge()%></li>

      <% ~~ %>
      이 부분에는 자바 코드를 입력할 수 있다.
      <%= ~~ %>
      이 부분에는 자바 코드를 출력할 수 있다.

# 회원 조회(jsp)
        List<Member> members = memberRepository.findAll();
        memberRepository.findAll() 을 통해 모든 회원을 조회한다.
        
        for (Member member : members)
        회원 목록 HTML을 for 루프를 통해서 회원 수 만큼 동적으로 생성하고 응답한다.

### MVC(컨트롤러 공통)
        String viewPath = "/WEB-INF/views/new-form.jsp";
        RequestDispatcher dispatcher = request.getRequestDispatcher(viewPath);
        dispatcher.forward(request, response);
        
        new-form.jsp를 호출한다.
        (dispatcher.forward() : 다른 서블릿이나 JSP로 이동할 수 있는 기능이다. 서버 내부에서 다시 호출이 발생한다.)
        
# 회원 등록(MVC 뷰)
        jsp에서
        <form action="save" method="post">
        username: <input type="text" name="username" />
        age: <input type="text" name="age" />
        <button type="submit">전송</button>
        </form>
        형식으로 저장

# 회원 저장(MVC 컨트롤러)
        //model에 데이터를 보관한다.
        request.setAttribute("member", member);
        
        HttpServletRequest를 Model로 사용한다.
        request가 제공하는 setAttribute() 를 사용하면 request 객체에 데이터를 보관해서 뷰에 전달할 수 있다.
        뷰는 request.getAttribute() 를 사용해서 데이터를 꺼낸다.

# 회원 저장(MVC 뷰)
        <ul>
          <li>id=${member.id}</li>
          <li>username=${member.username}</li>
          <li>age=${member.age}</li>
        </ul>
        
        request의 attribute에 담긴 데이터를 편리하게 조회할 수 있다.

# 회원 조회(MVC 컨트롤러)
        List<Member> members = memberRepository.findAll();
        request.setAttribute("members", members);

# 회원 조회(MVC 뷰)
         반복문
         <c:forEach var="item" items="${members}">
           <tr>
             <td>${item.id}</td>
             <td>${item.username}</td>
             <td>${item.age}</td>
           </tr>
         </c:forEach>


# MVC 컨트롤러 단점
        공통 처리가 어렵다는 문제가 있다.
        문제를 해결하려면 컨트롤러 호출 전에 먼저 공통 기능을 처리해야 한다.
        프론트 컨트롤러(Front Controller) 패턴을 도입하면 이런 문제를 깔끔하게 해결할 수 있다.(입구를 하나로)
        스프링 MVC의 핵심이 바로 프론트 컨트롤러에 있다.
