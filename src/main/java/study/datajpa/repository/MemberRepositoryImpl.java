package study.datajpa.repository;

import lombok.RequiredArgsConstructor;
import study.datajpa.entity.Member;

import javax.persistence.EntityManager;
import java.util.List;

@RequiredArgsConstructor
/**
 * MemerRepository + Impl Impl은 꽃 맞춰줘야됨!!!
 */
public class MemberRepositoryImpl implements MemberRepositoryCustom{

    private final EntityManager em;

    /**
     *QUeryDSL사용할 경우 Custom 많이 사용
     * */
    @Override
    public List<Member> findMemberCustom() {
        return em.createQuery("select m from Member m")
                .getResultList();
    }
}
