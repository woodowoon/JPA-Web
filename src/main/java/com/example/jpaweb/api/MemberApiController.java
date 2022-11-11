package com.example.jpaweb.api;

import com.example.jpaweb.domain.Member;
import com.example.jpaweb.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class MemberApiController {
    private final MemberService memberService;

    @GetMapping("/api/v1/members")
    public List<Member> memberV1() {
        return memberService.findMembers();
    }

    @GetMapping("/api/v2/members")
    public Result memberV2() {
        List<Member> findMembers = memberService.findMembers();
        List<MemberDto> collect = findMembers.stream().map(m -> new MemberDto(m.getName()))
                .collect(Collectors.toList());

        return new Result(collect.size(), collect);

    }

    @Data
    @AllArgsConstructor
    static class Result<T> {
        private int count;
        private T data;
    }

    @Data
    @AllArgsConstructor
    static class MemberDto {
        private String name;
    }

    @PostMapping("/api/v1/members") // 회원 등록 v1 api : 엔티티를 그대로 받는 api (사용을 금할것.)
    public CreateMemberReponse saveMemberV1(@RequestBody @Valid Member member) {
        Long id = memberService.join(member);
        return new CreateMemberReponse(id);
    }

    @PostMapping("/api/v2/members") // 회원 등록 v2 api : 엔티티가 아닌 따로 dto 의 형태로 빼낸 api (사용 ok~)
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

    @PatchMapping("/api/v2/members/{id}") // 회원 수정 api
    public UpdateMemberResponse updateMemberV2(
            @PathVariable("id") Long id,
            @RequestBody @Valid UpdateMemberRequest request) { // 변경할 name 을 가져온다.

        memberService.update(id, request.getName()); // 수정
        Member findMember = memberService.findOne(id); // find 하는 이유는 화면에 return 값을 뿌려주기 위해서 find 해준다
        return new UpdateMemberResponse(findMember.getId(), findMember.getName()); // 화면에 뿌려주는 값이다.
    }

    @Data
    static class UpdateMemberRequest {
        private String name;
    }

    @Data
    @AllArgsConstructor
    static class UpdateMemberResponse {
        private Long id;
        private String name;
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
