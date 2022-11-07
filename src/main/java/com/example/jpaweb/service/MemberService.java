package com.example.jpaweb.service;

import com.example.jpaweb.domain.Member;
import com.example.jpaweb.repository.MemberRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true) // JPA에서 조회하는것에 대해서 성능을 좀 더 최적화해준다.
@RequiredArgsConstructor // 1이 없어도 lombok 의 이것만 있어도 가능하다.
public class MemberService {
    private final MemberRepository memberRepository;

    /*
    1
    @Autowired // 자동으로 인젝션 해준다 이게 없어도 된다.
    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }
     */

    /**
     * 회원가입
     */
    @Transactional
    public Long join(Member member) {
        validateDuplicateMember(member); // 중복 회원 검증
        memberRepository.save(member);
        return member.getId();
    }

    private void validateDuplicateMember(Member member) {
        // EXCEPTION
        List<Member> findMembers = memberRepository.findByName(member.getName());
        if(!findMembers.isEmpty()) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    // 회원 전체 조회
    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    public Member findOne(Long memberID) {
        return memberRepository.findOne(memberID);
    }

}
