# JPQL
* JPA는 SQL을 추상화한 JPQL이라는 객체 지향 쿼리 언어 제공
* 엔티티 객체를 대상으로 쿼리

    //자동으로 em.flush()가 된다.
    List<Member> resultList = em.createNativeQuery("select MEMBER_ID, city, street, zipcode from MEMBER").getResultList();
     for (Member member1 : resultList) {
          System.out.println("member1 = " + member1);
     }
---

      //type이 Member
      TypedQuery<Member> query = em.createQuery("select m from Member m", Member.class);
      
      //type이 String
      TypedQuery<String> query2 = em.createQuery("select m.username from Member m", String.class);
      
      //type이 String, int 2가지라 Query사용
      Query query3 = em.createQuery("select m.username, m.age from Member m");
  ---

      TypedQuery<Member> query = em.createQuery("select m from Member m", Member.class);
      
      //query.getReslutList()
      //결과가 없으면 빈 리스트 반환
      List<Member> resultList = query.getResultList();
      for (Member member1 : resultList) {
                      System.out.println("member1 = " + member1);
                  }

      //query.getSingleResult()
      //결과가 없으면: javax.persistence.NoResultException
      //둘 이상이면: javax.persistence.NonUniqueResultException
      Member result = query.getSingleResult();
      System.out.println("result = " + result);


---
프로젝션 - 여러 값 조회
1. Query 타입으로 조회

2. Object[] 타입으로 조회

        List<Object[]> resultList2 = em.createQuery("select m.username, m.age from Member m").getResultList();
       
        Object[] result = resultList2.get(0);
       
        System.out.println("username = " + result[0]);
       
        System.out.println("age = " + result[1]);

3. new 명령어로 조회

        //query문에 (패키지이름).(class이름)(받을 데이터, 받을 데이터) from ...
       
        List<MemberDTO> resultList3 = em.createQuery("select new Jpql.MemberDTO(m.username, m.age) from Member m").getResultList();
       
        MemberDTO memberDTO = resultList3.get(0);
       
        System.out.println("memberDTO.getUsername() = " + memberDTO.getUsername());
       
        System.out.println("memberDTO.getAge() = " + memberDTO.getAge());

페이징

    //페이징 쿼리
    String jpql = "select m from Member m order by m.name desc";
    List<Member> resultList = em.createQuery(jpql, Member.class)
    .setFirstResult(10)
    .setMaxResults(20)
    .getResultList()
