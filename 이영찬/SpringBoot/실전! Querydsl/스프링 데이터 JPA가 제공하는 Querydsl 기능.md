# 스프링 데이터 JPA가 제공하는 Querydsl 기능

### 인터페이스 제공

+ ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/bfaac5b6-3144-470a-91c0-39645363f712)

  해당 인터페이스를 상속받는 것으로 Predicate 객체를 매개변수로 넣을 수 있음

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/41760586-d21c-4aec-869e-c7fc6af4df86)

  Predicate 객체를 토대로 조건 쿼리를 추가하여 동적 쿼리 가능

+ 한계점
  + 조인이 불가능하다. 특히 fetch 조인이 되지않아서 JPA의 장점을 이용할 수 없다.
  + 클라이언트가 Querydsl에 의존하여 조건을 추가해야 한다. 서비스 클래스부터 Querydsl 구현 기술이 첨가된다.
  + 복잡한 실무 환경에서 한계가 명확하다.

### Querydsl Web 지원

+ 예시

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/a9a2a2b6-6486-424c-93f9-364b0fe86264)

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/93655c47-378e-46e0-830f-9973fd3f1ca0)

  Q 클래스에 바로 조건을 추가할 수 있다.

+ 한계점
  + 단순한 조건만 가능하다.
  + 조건 커스텀이 명확하지 않고 복잡하다.
  + 컨트롤러부터 Querydsl 구현 기술이 첨가된다.
  + 복잡한 환경에서 한계가 명확하다.

### 리포지토리 지원 - QuerydslRepositorySupport

+ MemberRepositoryImpl

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/6cfb4c6f-67fb-4922-bcdd-08f54bcd2e37)

  QuerydslRepositorySupport를 상속받고 대응하는 엔티티 클래스를 super에 넣어준다.

  + search

    ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/992dc476-7b91-46e7-8cfd-717b3db94396)

    JPAQueryFactory를 이용하지 않고 QuerydslRepositorySupport에서 바로 from, where, select 등 여러 쿼리 조건을 넣어 쿼리문을 생성할 수 있다.

+ 한계점
  + Querydsl 3.x 버전을 기반으로 생성되어 select -> from -> where... 순이 아닌 from -> where -> select... 형식으로 쿼리문을 생성해야 한다.
  + Querydsl 4.x 버전에서 제공하는 JPAQueryFactory를 바로 이용할 수 없다.(JPAQueryFactory를 따로 생성해야 함)
  + 스프링 데이터 Sort 기능에 버그가 있음

### Querydsl 지원 클래스 직접 만들기

+ Querydsl4RepositorySupport

  @Repository
  
  public abstract class Querydsl4RepositorySupport {
  
      private final Class domainClass;
      private Querydsl querydsl;
      private EntityManager entityManager;
      private JPAQueryFactory queryFactory;
      public Querydsl4RepositorySupport(Class<?> domainClass) {
          Assert.notNull(domainClass, "Domain class must not be null!");
          this.domainClass = domainClass;
      }
      @Autowired
      public void setEntityManager(EntityManager entityManager) {
          Assert.notNull(entityManager, "EntityManager must not be null!");
          JpaEntityInformation entityInformation =
                  JpaEntityInformationSupport.getEntityInformation(domainClass, entityManager);
          SimpleEntityPathResolver resolver = SimpleEntityPathResolver.INSTANCE;
          EntityPath path = resolver.createPath(entityInformation.getJavaType());
          this.entityManager = entityManager;
          this.querydsl = new Querydsl(entityManager, new
                  PathBuilder<>(path.getType(), path.getMetadata()));
          this.queryFactory = new JPAQueryFactory(entityManager);
      }
      @PostConstruct
      public void validate() {
          Assert.notNull(entityManager, "EntityManager must not be null!");
          Assert.notNull(querydsl, "Querydsl must not be null!");
          Assert.notNull(queryFactory, "QueryFactory must not be null!");
      }
      protected JPAQueryFactory getQueryFactory() {
          return queryFactory;
      }
      protected Querydsl getQuerydsl() {
          return querydsl;
      }
      protected EntityManager getEntityManager() {
          return entityManager;
      }
      protected <T> JPAQuery<T> select(Expression<T> expr) {
          return getQueryFactory().select(expr);
      }
      protected <T> JPAQuery<T> selectFrom(EntityPath<T> from) {
          return getQueryFactory().selectFrom(from);
      }
      protected <T> Page<T> applyPagination(Pageable pageable,
                                            Function<JPAQueryFactory, JPAQuery> contentQuery) {
          JPAQuery jpaQuery = contentQuery.apply(getQueryFactory());
          List<T> content = getQuerydsl().applyPagination(pageable,
                  jpaQuery).fetch();
          return PageableExecutionUtils.getPage(content, pageable,
                  jpaQuery::fetchCount);
      }
      protected <T> Page<T> applyPagination(Pageable pageable,
                                            Function<JPAQueryFactory, JPAQuery> contentQuery, Function<JPAQueryFactory,
              JPAQuery> countQuery) {
          JPAQuery jpaContentQuery = contentQuery.apply(getQueryFactory());
          List<T> content = getQuerydsl().applyPagination(pageable,
                  jpaContentQuery).fetch();
          JPAQuery countResult = countQuery.apply(getQueryFactory());
          return PageableExecutionUtils.getPage(content, pageable,
                  countResult::fetchCount);
      }
  }

  해당 클래스를 생성하고 필요한 쿼리 처리 메소드를 생성하여 사용

+ MemberTestRepository

  ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/3b101a7a-4525-48d3-8164-815f2212a81c)

  Querydsl4RepositorySupport를 상속받고 생성자로 Member.class를 넘기면서 기본 세팅

  + 기본 쿼리

    ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/ebf50647-1e79-4723-9c06-4518142ec551)

    select, selectFrom 바로 사용 가능

  + 페이징

    ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/91856065-3a11-40cf-841d-39b936fd9fb4)

    기본적으로 사용했던 스프링 데이터 JPA에서 제공하는 Querydsl 기능

    ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/88df962a-5f4a-4831-b412-954acf5fbae2)

    ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/c5a319eb-f9a1-40cc-9498-1b8040911e24)

    applyPagination 메소드를 이용하여 페이징을 repository 딴에서 상당히 간단하게 사용 가능(한 줄로 처리가능)

    ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/728e8cb6-4dab-4e85-8c31-1662c8e13784)

    ![image](https://github.com/ManchanTime/TrashBoys/assets/127479677/8871e626-f2eb-42d9-a6a3-e75ab5762249)

    applyPagination을 오버라이딩하여 카운트 쿼리 또한 파라미터로 사용 가능
    이전에 복잡한 페이징에서 다뤘던 카운트 쿼리문을 따로 생성하여 주입하는 방식 또한 한줄로 처리 가능

+ 장점
  + 스프링 데이터 JPA가 제공하는 페이징 간단하게 생성 가능
  + 카운트 쿼리문을 따로 생성하여 사용하는 것도 가능
  + 스프링 데이터 Sort가 제대로 작동
  + select, selectFrom -> 순으로 쿼리문 생성 가능
  + EntityManager, JPAQueryFactory 사용 가능능
