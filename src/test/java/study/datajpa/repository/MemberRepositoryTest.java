package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(value = false)
class MemberRepositoryTest {

    @Autowired MemberRepository memberRepository;
    @Autowired TeamRepository teamRepository;

    @Test
    public void testMember() {
        Member member = new Member("memberA");
        Member savedMember = memberRepository.save(member);

        Member findMember = memberRepository.findById(savedMember.getId()).get();
        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member);
    }

    @Test
    public void basicCRUD() {
        Member member1 = new Member("member1");
        Member member2 = new Member("member1");
        memberRepository.save(member1);
        memberRepository.save(member2);

        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();

        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        //리스트 조회 검증
        List<Member> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        //카운트 검증
        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        memberRepository.delete(member1);
        memberRepository.delete(member2);

        long deleteCount = memberRepository.count();
        assertThat(deleteCount).isEqualTo(0);

    }

    @Test
    public void findByUsernameAndAgeGreaterThen() {
        Member m1 = new Member("aaa", 10);
        Member m2 = new Member("aaa", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("aaa", 15);
        assertThat(result.get(0).getUsername()).isEqualTo("aaa");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result.size()).isEqualTo(1);

    }

    @Test
    public void testNamedQuery() {

        Member m1 = new Member("aaa", 10);
        Member m2 = new Member("bbb", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsername("aaa");
        Member findMember = result.get(0);
        assertThat(findMember).isEqualTo(m1);
    }

    @Test
    public void testQuery() {

        Member m1 = new Member("aaa", 10);
        Member m2 = new Member("bbb", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);


        List<Member> result = memberRepository.findUser("aaa", 10);

        assertThat(result.get(0)).isEqualTo(m1);
    }

    @Test
    public void findUsernameList() {

        Member m1 = new Member("aaa", 10);
        Member m2 = new Member("bbb", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<String> usernameList = memberRepository.findUsernameList();
        for (String s : usernameList) {
            System.out.println("s= " + s);

        }
    }

    @Test
    public void findMemberDto() {

        Team team1 = new Team("teamA");
        teamRepository.save(team1);

        Team team2 = new Team("teamB");
        teamRepository.save(team2);

        Member m1 = new Member("aaa", 10);
        m1.setTeam(team1);
        memberRepository.save(m1);

        Member m2 = new Member("bbb", 12);
        m2.setTeam(team1);
        memberRepository.save(m2);

        Member m3 = new Member("ccc", 14);
        m3.setTeam(team2);
        memberRepository.save(m3);

        Member m4 = new Member("ddd", 16);
        m4.setTeam(team2);
        memberRepository.save(m4);

        List<MemberDto> memberDto = memberRepository.findMemberDto();
        for (MemberDto dto : memberDto) {
            System.out.println("s= " + dto);

        }

        List<MemberDto> memberDto2 = memberRepository.findMemberDto2(team2.getName());
        for (MemberDto dto : memberDto2) {
            System.out.println("s= " + dto);

        }

    }

    @Test
    public void findByNames() {

        Member m1 = new Member("aaa", 10);
        Member m2 = new Member("bbb", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByNames(Arrays.asList("aaa","bbb"));
        for (Member member : result) {
            System.out.println("result= " + member);
        }
    }

    @Test
    public void returnType() throws NotFoundException {

        Member m1 = new Member("aaa", 10);
        Member m2 = new Member("bbb", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        /**
         * 단건인 경우 = null ----> try catch알아서 해서 null로처리
         * 리스트로 찾는데 없을 경우 = 0
         * optional인데 결과값이 2개인 경우 Incorrect~~~ 예외터짐
         * NonUniqueResultException이 터져!!~~(jpa) -> 스프링프레임워크 exception(IncorrectResultsizeDataAccessException)
         */
//        Member findMember = memberRepository.findMemberByUsername("aaa");
//        System.out.println("findMember =" + findMember);
        Optional<Member> findMember = memberRepository.findOptionalByUsername("dadf");

        System.out.println("findMember =" + findMember);
    }

    public static class NotFoundException extends Throwable{
        public NotFoundException(String s) {

        }
    }

    @Test
    public void paging() {
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10;
        //0번째에서 3개 가져와
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        //when
        /**
         * 여기서도 entity절대 안돼 dto로 변화!!
         */
        Page<Member> page = memberRepository.findByAge(age, pageRequest);

        Page<MemberDto> toMap = page.map(member -> new MemberDto(member.getId(), member.getUsername(), null));
        System.out.println("toMap = " + toMap.getContent());
        /**        더보기 식으로 paging 효과줄 때 하나 더가져와서 눈속임 효과 적용
        total count가 안 날라감**/
//        Slice<Member> page = memberRepository.findByAge(age, pageRequest);
        /**
         * List로도 가져올 수 있음
         */
//        List<Member> page = memberRepository.findByAge(age, pageRequest);
//
//        for (Member member : page) {
//            System.out.println("member = " + member);
//
//        }

        //then
        List<Member> content = page.getContent();
        long totalElements = page.getTotalElements();

        assertThat(content.size()).isEqualTo(3);
        assertThat(page.getTotalElements()).isEqualTo(5);
        assertThat(page.getNumber()).isEqualTo(0);
        assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.isFirst()).isTrue();
        assertThat(page.hasNext()).isTrue();
        }
}