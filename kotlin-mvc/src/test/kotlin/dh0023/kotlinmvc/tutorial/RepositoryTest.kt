package dh0023.kotlinmvc.tutorial

import dh0023.kotlinmvc.tutorial.blog.Article
import dh0023.kotlinmvc.tutorial.blog.ArticleRepository
import dh0023.kotlinmvc.tutorial.blog.User
import dh0023.kotlinmvc.tutorial.blog.UserRepository
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.data.repository.findByIdOrNull

/**
 * JPA 컴포넌트만을 테스트하기 위한 어노테이션
 * full-auto config를 해제하고, JPA테스트와 연관된 config만 적용
 */
@DataJpaTest
// (@AutoConfigureTestDatabase(replace = Replace.NONE)
class RepositoryTest @Autowired constructor(
    val entityManager: TestEntityManager,
    val userRepository: UserRepository,
    val articleRepository: ArticleRepository
){
    @Test
    fun findByIdOrNull() {
        val juergen = User("springjuergen", "Juergen", "Hoeller")

        //  persistenceContext(영속성 컨텍스트)에 저장
        entityManager.persist(juergen)

        val article = Article("Spring Framework 5.0 goes GA", "Dear Spring community ...", "Lorem ipsum", juergen)
        entityManager.persist(article)

        // db에 한번에 호출
        entityManager.flush()

        var list = articleRepository.findAll()

        // CrudRepository.findByIdOrNull Kotlin extension provided by default with Spring Data
        val found = articleRepository.findByIdOrNull(article.id!!)
        assertThat(found).isEqualTo(article)
    }

    @Test
    fun findByLogin() {
        val juergen = User("springjuergen", "Juergen", "Hoeller")

        //  persistenceContext(영속성 컨텍스트)에 저장
        entityManager.persist(juergen)
        // db에 한번에 호출
        entityManager.flush()

        val user = userRepository.findByLogin(juergen.login)

        assertThat(user).isEqualTo(juergen)
    }
}