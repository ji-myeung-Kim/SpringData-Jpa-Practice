package study.datajpa.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.repository.MemberRepository;

import javax.annotation.PostConstruct;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepository memberRepository;

    @GetMapping("/members/{id}")
    public String findMember(@PathVariable("id") Long id) {
        Member member = memberRepository.findById(id).get();
        return member.getUsername();
    }

    @GetMapping("/members2/{id}")
    public String findMember2(@PathVariable("id") Member member) {
        return member.getUsername();
    }

    /**
     * 페이징 기법 localhost:8080/members?page=0&size=3&sort=id,desc&sort=username,desc
     * yml에 글로벌로 default 설정한 10개 보다 5개를 먼저 인식
     * one-indexed-parameters: true 사용해서 페이지 시작이 1로 설정 가능 But....
     * 3으로 검색할때 number가 2로 떠......
     * "last": false,
     *     "totalElements": 100,
     *     "totalPages": 20,
     *     "size": 5,
     *     "number": 2,
     *     "sort": {
     *         "empty": true,
     *         "unsorted": true,
     *         "sorted": false
     *     },
     *     "first": false,
     *     "numberOfElements": 5,
     *     "empty": false
     */
    @GetMapping("/members")
    public Page<MemberDto> list(@PageableDefault(size = 5) Pageable pageable) {
        return memberRepository.findAll(pageable).map(MemberDto::new);
//        return memberRepository.findAll(pageable).map(member -> new MemberDto(member));

    }

    @PostConstruct
    public void init() {
        for (int i= 0; i < 100; i++) {
           memberRepository.save(new Member("user" + i, i));
        }
    }

}
