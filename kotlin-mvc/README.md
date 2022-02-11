# Kotlin Spring Web

```
Caused by: java.lang.IllegalArgumentException: Failed to create query for method public abstract java.util.Iterator dh0023.kotlinmvc.tutorial.blog.ArticleRepository.findAllByOrderByAddedAtDesc()! Cannot invoke "java.lang.Class.isInterface()" because "typeToRead" is null
	at org.springframework.data.jpa.repository.query.PartTreeJpaQuery.<init>(PartTreeJpaQuery.java:96)
	at org.springframework.data.jpa.repository.query.JpaQueryLookupStrategy$CreateQueryLookupStrategy.resolveQuery(JpaQueryLookupStrategy.java:113)
	at org.springframework.data.jpa.repository.query.JpaQueryLookupStrategy$CreateIfNotFoundQueryLookupStrategy.resolveQuery(JpaQueryLookupStrategy.java:254)
	at org.springframework.data.jpa.repository.query.JpaQueryLookupStrategy$AbstractQueryLookupStrategy.resolveQuery(JpaQueryLookupStrategy.java:87)
	at org.springframework.data.repository.core.support.QueryExecutorMethodInterceptor.lookupQuery(QueryExecutorMethodInterceptor.java:102)
	... 111 more
Caused by: java.lang.NullPointerException: Cannot invoke "java.lang.Class.isInterface()" because "typeToRead" is null
	at org.springframework.data.jpa.repository.query.JpaQueryCreator.complete(JpaQueryCreator.java:181)
	at org.springframework.data.jpa.repository.query.JpaQueryCreator.complete(JpaQueryCreator.java:152)
	at org.springframework.data.jpa.repository.query.JpaQueryCreator.complete(JpaQueryCreator.java:59)
	at org.springframework.data.repository.query.parser.AbstractQueryCreator.createQuery(AbstractQueryCreator.java:95)
	at org.springframework.data.repository.query.parser.AbstractQueryCreator.createQuery(AbstractQueryCreator.java:81)
	at org.springframework.data.jpa.repository.query.PartTreeJpaQuery$QueryPreparer.<init>(PartTreeJpaQuery.java:217)
	at org.springframework.data.jpa.repository.query.PartTreeJpaQuery.<init>(PartTreeJpaQuery.java:92)
	... 115 more
```
JPA조회시 다음과 같은 오류가 발생하고 있음.

[https://github.com/spring-projects/spring-data-jpa/issues/2408](https://github.com/spring-projects/spring-data-jpa/issues/2408) 을 보면, 

```
The fix was applied post 2.7 M2. It’s gonna be shipped with the upcoming M3.
```

spring data jpa 2.7.0-M3에 해결되어 반영될 예정이라고 한다.
그 이전까지는 `@Query` 로 지정하면 해결된다고 되어있어, `@Query`로 지정하여 수행시 이슈 해결되는것 확인할 수 있음.
