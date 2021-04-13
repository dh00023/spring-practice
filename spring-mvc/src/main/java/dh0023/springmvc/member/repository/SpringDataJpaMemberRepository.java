package dh0023.springmvc.member.repository;

import dh0023.springmvc.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data Jpa는 interface로 생성하면
 * Proxy로 구현체를 만들고 Bean을 등록해준다.
 */
public interface SpringDataJpaMemberRepository extends JpaRepository<Member, Long>, MemberRepository {

    /**
     * 메서드 이름 만으로 조회 기능 제공(By, And, Or 등등 규칙을 지키면 된다.)
     * @param name
     * @return
     */
    @Override
    Optional<Member> findByName(String name);
}
