package com.example.jpaweb.api;

import com.example.jpaweb.domain.Member;
import com.example.jpaweb.service.MemberService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

@RestController
@RequiredArgsConstructor
public class MemberApiController {
    private final MemberService memberService;

    @PostMapping("/api/v1/members")
    public CreateMemberReponse saveMemberV1(@RequestBody @Valid Member member) {
        Long id = memberService.join(member);
        return new CreateMemberReponse(id);
    }

    @PostMapping("/api/v2/members")
    public CreateMemberReponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request) { // 엔티티는 엔티티용으로만 사용하고
        // 실제 API에 필요한 값들은 따로 빼준다.
        // 유지보수성이 높고 가독성이 높다. 엔티티가 변경이 되어도 api 스펙이 바뀌지않는다.
        // 엔티티를 외부에 노출하거나, 반환받는경우는 절대 없으니 그냥 안하는게 좋다.
        // 그냥 API에서 절대 엔티티를 사용하지 않는다 DTO를 통해서 데이터를 받자.
        Member member = new Member();
        member.setName(request.getName());

        Long id = memberService.join(member);
        return new CreateMemberReponse(id);
    }

    @Data
    static class CreateMemberRequest { // api 스펙마다 이렇게 만들어주는 것이 좋다. 엔티티를 바로 받아오면 안된다.
        @NotEmpty
        private String name;

    }

    @Data
    static class CreateMemberReponse {
        private Long id;

        public CreateMemberReponse(Long id) {
            this.id = id;
        }
    }

}
