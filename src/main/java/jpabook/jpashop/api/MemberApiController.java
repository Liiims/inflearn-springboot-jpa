package jpabook.jpashop.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jpabook.jpashop.domain.member.Member;
import jpabook.jpashop.service.MemberService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController // @Controller + @ResponseBody
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    /**
     * 등록 v1 : 요청 값으로 Member 엔티티를 직접 받는다. <br><br>
     * 문제점 <br>
     * - 엔티티에 프레젠테이션 계층(= 화면)을 위한 로직(검증..)이 들어간다. <br>
     * - 실무에서는 한 엔티티를 위한 API 가 다양하게 만들어지는데, 한 엔티티에 각각의 API 를 위한 모든 요청사항을 담기는 어렵다. <br>
     * - 엔티티 변경 시 API 스펙도 변한다. => API 요청 스펙에 맞추어 별도의 DTO 를 파라미터로 받는다.
     */
    @PostMapping("/api/v1/members")
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member) { // @RequestBody : JSON Data -> Member
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    /**
     * 등록 v2 : 요청 값으로 Member 엔티티 대신 별도의 DTO 를 받음
     */
    @PostMapping("/api/v2/members")
    public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request) {
        Member member = new Member();
        member.setName(request.getName());

        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    @Data
    static class CreateMemberRequest {
        @NotEmpty
        private String name;
    }

    @Data
    static class CreateMemberResponse {
        private Long id;

        public CreateMemberResponse(Long id) {
            this.id = id;
        }
    }
}
