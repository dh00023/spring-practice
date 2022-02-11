package dh0023.springmvc.member.repository;

import dh0023.springmvc.member.domain.Member;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

class MemoryMemberRepositoryTest {
    MemoryMemberRepository repository = new MemoryMemberRepository();

    /**
     * 하나의 테스트가 끝날때마다 공용 데이터를 지워준다.
     * @AfterEach 어노테이션은 매 테스트 수행 끝마다 실행히켜준다.
     */
    @AfterEach
    public void afterEach(){
        repository.clearStore();
    }
    /**
     * 테스트 어노테이션으로 해당 함수를 테스트 가능
     */
    @Test
    public void save(){
        Member member = new Member();
        member.setName("string");

        repository.save(member);

        Member result = repository.findById(member.getId()).get();

//        Assertions.assertEquals(member, null);
        // member가 result랑 같은지 비교
        assertThat(member).isEqualTo(result);
    }


    @Test
    public void findByName(){
        Member member = new Member();
        member.setName("faker");
        repository.save(member);

        Member member2 = new Member();
        member2.setName("dahye");
        repository.save(member2);

        Member result = repository.findByName("faker").get();

        assertThat(result).isEqualTo(member);
    }

    @Test
    public void findAll(){
        Member member = new Member();
        member.setName("faker");
        repository.save(member);

        Member member2 = new Member();
        member2.setName("dahye");
        repository.save(member2);

        List<Member> results = repository.findAll();

        assertThat(results.size()).isEqualTo(2);
    }


}
