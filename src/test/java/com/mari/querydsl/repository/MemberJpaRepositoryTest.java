package com.mari.querydsl.repository;

import com.mari.querydsl.dto.MemberApiDto;
import com.mari.querydsl.dto.MemberSearchCondition;
import com.mari.querydsl.entity.Member;
import com.mari.querydsl.entity.Team;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
public class MemberJpaRepositoryTest {

    @Autowired
    private EntityManager em;

    @Autowired
    private MemberJpaRepository repository;
    @Autowired
    private JPAQueryFactory queryFactory;
    @BeforeEach
    void insert(){


        Team team1 = new Team("team1");
        Team team2 = new Team("team2");

        em.persist(team1);
        em.persist(team2);

        Member member1 = new Member("member1",20,team1);
        Member member2 = new Member("member2",30,team1);
        Member member3 = new Member("member3",25,team2);
        Member member4 = new Member("member4",35,team2);

        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);

        em.flush();
        em.clear();
    }

    @Test
    void selectDtoTest(){
        System.out.println(repository.selectMember());
    }

    @Test
    void selectWtihConditionTest(){
        MemberSearchCondition condition = new MemberSearchCondition();
       // condition.setTeamname("team1");
      //  condition.setGoeAge(10);

        System.out.println(repository.selectMemberWithCond(condition));

    }
    @Test
    void searchConditionTest(){
        MemberSearchCondition condition = new MemberSearchCondition();
        condition.setTeamname("team2");
        condition.setGoeAge(30);
        condition.setLoeAge(40);
        List<MemberApiDto> result = repository.searchWithCondition(condition);
        System.out.println(repository.searchWithCondition(condition));
        assertThat(result.get(0).getUsername()).isEqualTo("member4");
    }
}
