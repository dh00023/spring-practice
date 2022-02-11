package dh0023.kotlinmvc.tutorial.blog

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository

interface ArticleRepository : CrudRepository<Article, Long> {

    fun findBySlug(slug: String): Article?

    @Query("select a.title, a.headline, a.content from Article a order by addedAt desc")
    fun findAllByOrderByAddedAtDesc(): List<Article>
}