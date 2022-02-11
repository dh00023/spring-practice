package dh0023.springmvc.member.repository;

import dh0023.springmvc.member.domain.Member;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import javax.persistence.EntityManager;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * JPA는 ORM(Object Relation Mapping)
 * JPA 사용시에는 항상 Transaction 어노테이션이 필요하다.
 */
public class JpaMemberRepository implements MemberRepository{

    private final EntityManager entityManager;

    /**
     * 생성자가 한개인 경우에는 @Autowired를 생략할 수 있다.
     * @param entityManager
     */
//    @Autowired
    public JpaMemberRepository(EntityManager entityManager){
        this.entityManager = entityManager;
    }


    @Override
    public Member save(Member member) {
        entityManager.persist(member);
        return member;
    }

    @Override
    public Optional<Member> findById(Long id) {
        Member member = entityManager.find(Member.class, id);
        return Optional.ofNullable(member);
    }

    @Override
    public Optional<Member> findByName(String name) {
        List<Member> result = entityManager.createQuery("select m from Member m where m.name=:name", Member.class).setParameter("name",name).getResultList();
        return result.stream().findAny();
    }

    @Override
    public List<Member> findAll() {
        return entityManager.createQuery("select m from Member m", Member.class).getResultList();
    }
}
