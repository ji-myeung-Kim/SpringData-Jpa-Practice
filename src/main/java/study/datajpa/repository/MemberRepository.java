package study.datajpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {
    List<Member> findByUsernameAndAgeGreaterThan(String aaa, int i);

    List<Member> findByUsername(@Param("username") String username);

    /**
     * 주의사항!!!! Query문 작성할때 =:username, =:age 이런식으로 :와 변수명들은 붙어있어야 한다!!!
     */
    @Query("select m from Member m where m.username=:username and m.age=:age")
    List<Member> findUser(@Param("username") String username, @Param("age") int age);

    @Query("select m.username from Member m")
    List<String> findUsernameList();


    @Query("select new study.datajpa.dto.MemberDto( m.id, m.username, t.name)from Member m join m.team t")
    List<MemberDto> findMemberDto();

    @Query("select new study.datajpa.dto.MemberDto( m.id, m.username, t.name)from Member m join m.team t where t.name =:name")
    List<MemberDto> findMemberDto2(@Param("name") String teamname);

    @Query("select m from Member m where m.username in :names")
    List<Member> findByNames(@Param("names") Collection<String> names);


    List<Member> findListByUsername(String username);//컬렉션
    Member findMemberByUsername(String username);//단건
    Optional<Member> findOptionalByUsername(String username);

//    Page<Member> findByAge(int age, Pageable pageable);

    /**
     * countquery를 분리해서 해주기도 함
     */
//    @Query(value = "select m from Member m left join m.team t",
//            countQuery = "select count(m) from Member m")
//    Page<Member> findByAge(int age, Pageable pageable);

    //여기에 sorting 조건을 넣을 수도 있음!
    @Query(value = "select m from Member m")
    Page<Member> findByAge(int age, Pageable pageable);

//    List<Member> findByAge(int age, Pageable pageable);

    @Modifying (clearAutomatically = true)//executeUpdate역할을 실행시켜주는 애노테이션
    @Query("update Member m set m.age = m.age + 1 where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);

    @Query("select m from Member m left join fetch m.team")
    List<Member> findMemberFetchJoin();

    @Override
    @EntityGraph(attributePaths = {"team"}) //jpql없이 fetch join 성능 최적화
    List<Member> findAll();

    @EntityGraph
    @Query("select m from Member m")
    List<Member> findMemberEntityGraph();

//    @EntityGraph(attributePaths = ("team"))
    @EntityGraph("Member.all")
    List<Member> findEntityGraphByUsername(@Param("username") String username);


    @QueryHints(value =@QueryHint( name = "org.hibernate.readOnly", value = "true"))
    Member findReadOnlyByUsername(String username);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Member findLockByUsername(String username);
}
