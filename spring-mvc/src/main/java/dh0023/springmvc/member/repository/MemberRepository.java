package dh0023.springmvc.member.repository;

import dh0023.springmvc.member.domain.Member;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 저장소
 * 현재 DB가 정해지지 않았다는 가정하에 구현
 */
public interface MemberRepository {

    Member save(Member member);
    Optional<Member> findById(Long id);
    Optional<Member> findByName(String name);
    List<Member> findAll();
}
