package com.kwj.repository;

import com.kwj.dto.MemberFormDto;
import com.kwj.entity.Member;
import com.kwj.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
class MemberServiceTest {

    @Autowired
    MemberService memberService;

    @Autowired
    PasswordEncoder passwordEncoder;

    public Member createMember(){
        MemberFormDto dto = new MemberFormDto();
        dto.setEmail("test@test.com");
        dto.setName("테스트회원");
        dto.setAddress("김포시 장기동");
        dto.setPassword("1234");
        return Member.createMember(dto, passwordEncoder);
    }//end of method

    @Test
    @DisplayName("회원가입 테스트")
    public void saveMemberTest(){

        Member member = createMember();
        Member savedMember = memberService.saveMember(member);

        assertEquals(member.getEmail(), savedMember.getEmail());
        assertEquals(member.getName(), savedMember.getName());
        assertEquals(member.getAddress(), savedMember.getAddress());
        assertEquals(member.getPassword(), savedMember.getPassword());
        assertEquals(member.getRole(), savedMember.getRole());

        System.out.println(member.toString());
        System.out.println(savedMember.toString());
    }//end of method

    @Test
    @DisplayName("중복회원가입 테스트")
    public void saveDuplicateMemberTest(){
        Member member1 = createMember();
        Member member2 = createMember();
        memberService.saveMember(member1);

        Throwable e = assertThrows(IllegalStateException.class, () ->{
            memberService.saveMember(member2);
        });

        assertEquals("이미 가입된 회원입니다",e.getMessage());
        System.out.println(e.getMessage());
    }//end of method
}//end of class