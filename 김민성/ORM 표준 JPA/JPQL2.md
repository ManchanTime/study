# 경로 표현식
* 상태 필드(state field): 경로 탐색의 끝, 탐색X
* 단일 값 연관 경로: 묵시적 내부 조인(inner join) 발생, 탐색O

      "select m.team from Member m"을 하면
      코드 상으로는 m.team으로 찍으면 되지만
      실제 쿼리문은 Member와 Team의 inner join이 발생한다.
  
* 컬렉션 값 연관 경로: 묵시적 내부 조인 발생, 탐색X

      "select t.members from Team t" 하면 t.members가 탐색 끝이라 검색이 안됨.
      "selet m.username from Team t join t.members m"으로 사용해야 한다.

# fetch join
* 연관된 엔티티나 컬렉션을 sQL 한 번에 함께 조회
  
      [JPQL]
      select m from Member m join fetch m.team
      [SQL]
      SELECT M.*, T.* FROM MEMBER M
      INNER JOIN TEAM T ON M.TEAM_ID=T.ID

  ![image](https://github.com/LAB-2023/LAB_study/assets/129240433/606eab0e-605c-40d8-bad4-f98916a4df75)
  
      String query = "select m From Member m";
      List<Member> resultList = em.createQuery(query, Member.class).getResultList();
  
      System.out.println("resultList size : " + resultList.size()); //2

# collection fetch join
![image](https://github.com/LAB-2023/LAB_study/assets/129240433/f0e3dc88-5120-4b5c-a5f4-0f045926a7f9)


    String query_collection = "select t From Team t join fetch t.members";
    List<Team> resultList_collection = em.createQuery(query_collection, Team.class).getResultList();

    System.out.println("resultList_collection size : " + resultList_collection.size()); //3

# distinct fetch join

    String query_distinct = "select distinct t From Team t join fetch t.members";
    List<Team> resultList_distinct = em.createQuery(query_distinct, Team.class).getResultList();
    
    System.out.println("resultList_distinct size : " + resultList_distinct.size()); //2

# 일반 join
![image](https://github.com/LAB-2023/LAB_study/assets/129240433/e5ccfe18-a382-4837-b603-63f3a46ca7c5)

    //lazy로 되어 teamA, teamB query문이 따로 사용될 때 작성된다.
    //teamA가 사용될 때 쿼리문 작성, teamB가 사용될 때 쿼리문 작성. 각각 작성된다.
    String query_join = "select t From Team t join t.members m";
    List<Team> resultList_join = em.createQuery(query_join, Team.class).getResultList();

    System.out.println("resultList_join size : " + resultList_join.size()); //3
    for (Team team : resultList_join) {
        System.out.println("team = " + team.getName() + " " + team.getMembers().size());
        for(Member member_collection : team.getMembers()){
            System.out.println("--->member_collection = " + member_collection);
        }
    }

# fetch join 특징과 한계
* 페치 조인 대상에는 별칭을 줄 수 없다. 
  * "select t from Team t join fetch t.members m"처럼 t.members m 별칭을 줄 수 없다.
* 둘 이상의 컬렉션은 페치 조인 할 수 없다.
  * 컬렉션 자체가 데이터가 뻥튀기 되는데 컬렉션 컬렉션 하면 더 뻥튀기됨
* 컬렉션을 페치 조인하면 페이징 API(setFirstResult, setMaxResults)를 사용할 수 없다.
  ![image](https://github.com/LAB-2023/LAB_study/assets/129240433/3438ccbb-e193-46c7-857f-9862a2a4bf8a)

  * 그림 봤을 때 만약 페이징을 하게 되면 teamA 입장에서는 멤버가 1명 밖에 없다고 인식할 가능성이 있다.
  * "select t from Team t join fetch t.members"를
  "select m from Member m join fetch m.team"으로 다대일로 바꾸면 페이징 가능

# 벌크 연산
int resultCount = em.createQuery("update Member m set m.age = 20").executeUpdate();

System.out.println("resultCount = " + resultCount);
회원 전체 나이를 20으로 변경

벌크연산도 JPQL이라 쿼리문이 작성되어 flush가 자동으로 된다. 하지만 영속성은 안바뀌기 때문에 초기화를 시켜줘야한다.

    //벌크연산
    int resultCount = em.createQuery("update Member m set m.age = 20").executeUpdate();
    System.out.println("resultCount = " + resultCount);
    
    //clear없으면 아래 age가 기본값으로 출력됨. 20으로 출력 안됨
    //em.clear();    clear하면 20으로 출력됨
    System.out.println("member1 age : " + member.getAge());
    System.out.println("member2 age : " + member2.getAge());
    System.out.println("member3 age : " + member3.getAge());
