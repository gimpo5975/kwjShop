package com.kwj.repository;

import com.kwj.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Member findByEmail(String email);//회원 가입시 중복된 회원이 있는지 검사하기 위해서 이메일로 쿼리 메소드 작성

}//end of interface
