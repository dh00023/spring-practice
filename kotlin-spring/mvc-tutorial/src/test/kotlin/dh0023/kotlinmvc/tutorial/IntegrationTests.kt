package dh0023.kotlinmvc.tutorial

import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.getForEntity
import org.springframework.http.HttpStatus
import toSlug

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class IntegrationTests(@Autowired val restTemplate: TestRestTemplate) : AnnotationSpec() {

    @BeforeEach
    fun setup() {
        println(">> SetUp")
    }

    @Test
    fun HOME_연결_테스트() {
        val entity = restTemplate.getForEntity<String>("/")
        entity.statusCode shouldBe HttpStatus.OK
        entity.body shouldBe "test"
    }

    @Test
    fun article_slug_param_test() {
        val title = "Reactor Aluminium has landed"
        val entity = restTemplate.getForEntity<String>("/article/${title.toSlug()}")

        entity.statusCode shouldBe HttpStatus.OK
        entity.body shouldContain title to "Lorem ipsum"
    }

    @AfterEach
    fun teardown() {
        println(">> teardown")
    }
}
