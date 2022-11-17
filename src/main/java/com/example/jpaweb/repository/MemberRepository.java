package com.example.jpaweb.repository;

import com.example.jpaweb.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {
    // select m from Member m where m.name = ?
    // 이게 그냥 된다 Name 을 보고 그냥 되는 것이다.
    List<Member> findByName(String name);
}