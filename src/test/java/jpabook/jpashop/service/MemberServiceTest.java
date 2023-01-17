package jpabook.jpashop.service;

import jpabook.jpashop.domain.member.Member;
import jpabook.jpashop.domain.member.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired
    MemberService memberService;

    @Autowired
    MemberRepository memberRepository;

    @Test
    @Rollback(value = false)
    public void 회원가입() {
        // given
        Member member = new Member();
        member.setName("Lim");

        // when
        Long savedId = memberService.join(member);

        // then
        assertThat(member).isEqualTo(memberRepository.findOne(savedId));
    }

    @Test
    public void 중복_회원_예외() {
        Member member1 = new Member();
        member1.setName("Lim");
        Member member2 = new Member();
        member2.setName("Lim");

        memberService.join(member1);

        assertThrows(IllegalStateException.class,
                () -> memberService.join(member2));
    }
}