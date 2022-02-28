package dh0023.kotlinmvc.tutorial.blog

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ArticleRepository : CrudRepository<Article, Long> {

    fun findBySlug(slug: String): Article?

//    @Query("select a.title, a.headline, a.content from Article a order by addedAt desc")
    fun findAllByOrderByAddedAtDesc(): List<Article>
}
