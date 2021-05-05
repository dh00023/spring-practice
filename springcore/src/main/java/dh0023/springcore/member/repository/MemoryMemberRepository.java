package dh0023.springcore.member.repository;

import dh0023.springcore.member.domain.Member;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class MemoryMemberRepository implements MemberRepository{

    // 실무에서는 동시성 이슈가 있으므로 ConcurrentHashMap 사용 필요
    // 개발용으로만 사용
    private static Map<Long, Member> store = new HashMap<>();

    @Override
    public void save(Member member) {
        store.put(member.getId(), member);
    }

    @Override
    public Member findById(Long memberId) {
        return store.get(memberId);
    }
}
