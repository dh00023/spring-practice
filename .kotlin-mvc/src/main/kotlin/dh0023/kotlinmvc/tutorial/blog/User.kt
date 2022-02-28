package dh0023.kotlinmvc.tutorial.blog

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

/**
 * postgresql에서는 User는 reserved 예약키워드이므로 사용하지 못한다.
 * https://www.postgresql.org/docs/current/sql-keywords-appendix.html
 */
@Entity(name = "TestUser")
class User(
    var login: String,
    var firstname: String,
    var lastname: String,
    var description: String? = null,
    @Id @GeneratedValue var id: Long? = null
)