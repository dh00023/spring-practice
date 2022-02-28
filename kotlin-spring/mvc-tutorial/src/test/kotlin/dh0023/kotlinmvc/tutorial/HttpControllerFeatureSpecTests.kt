package dh0023.kotlinmvc.tutorial

import com.ninjasquad.springmockk.MockkBean
import dh0023.kotlinmvc.tutorial.blog.Article
import dh0023.kotlinmvc.tutorial.blog.ArticleRepository
import dh0023.kotlinmvc.tutorial.blog.User
import dh0023.kotlinmvc.tutorial.blog.UserRepository
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.every
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest
class HttpControllerFeatureSpecTests(
    @Autowired val mockMvc: MockMvc,
    @MockkBean var userRepository: UserRepository,
    @MockkBean var articleRepository: ArticleRepository
) : FeatureSpec({

    feature("Article 조회") {
        scenario("전체 조회하기") {
            // given
            val juergen = User("springjuergen", "Juergen", "Hoeller")
            val spring5Article =
                Article("Spring Framework 5.0 goes GA", "Dear Spring community ...", "Lorem ipsum", juergen)
            val spring43Article =
                Article("Spring Framework 4.3 goes GA", "Dear Spring community ...", "Lorem ipsum", juergen)

            // when
            every { articleRepository.findAllByOrderByAddedAtDesc() } returns listOf(
                spring5Article,
                spring43Article
            )

            // then
            mockMvc.perform(get("/api/article/").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("\$.[0].author.login").value(juergen.login))
                .andExpect(jsonPath("\$.[0].slug").value(spring5Article.slug))
                .andExpect(jsonPath("\$.[1].author.login").value(juergen.login))
                .andExpect(jsonPath("\$.[1].slug").value(spring43Article.slug))
        }
    }

    feature("User 조회") {
        scenario("전체 조회하기") {
            // given
            val juergen = User("springjuergen", "Juergen", "Hoeller")
            val smaldini = User("smaldini", "Stéphane", "Maldini")
            // when
            every { userRepository.findAll() } returns listOf(juergen, smaldini)
            // then
            mockMvc.perform(get("/api/user/").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("\$.[0].login").value(juergen.login))
                .andExpect(jsonPath("\$.[1].login").value(smaldini.login))
        }
    }
})
