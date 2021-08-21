package com.mari.querydsl;

import com.mari.querydsl.entity.Member;
import com.mari.querydsl.entity.QMember;
import com.mari.querydsl.entity.Team;
import com.querydsl.core.QueryFactory;
import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

import static com.mari.querydsl.entity.QMember.member;

import static com.mari.querydsl.entity.QTeam.team;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class QuerysdslTest {

    @Autowired
    private EntityManager em;
    JPAQueryFactory queryFactory = new JPAQueryFactory(em);

    @BeforeEach
    void insert(){
        queryFactory = new JPAQueryFactory(em);

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
    void jpql(){
       Member member =  em.createQuery("select m from Member m where m.username=:username",Member.class)
                .setParameter("username", "member1")
                .getSingleResult();

        Assertions.assertEquals(member.getAge(),20);

    }
    @Test
    void queryDsl(){
        Member result = queryFactory
                .select(member)
                .from(member)
                .where(member.username.eq("member1"))
                .fetchOne();

        Assertions.assertEquals(result.getAge(),20);
    }

    @Test
    void search(){
        Member result = queryFactory
                .selectFrom(member)
                .where(member.username.eq("member1"),
                        member.age.eq(20))
                .fetchOne();

        Team result2 = queryFactory
                .selectFrom(team)
                .where(team.name.eq("team1"))
                .fetchOne();

        Assertions.assertEquals(result.getTeam().getName(),result2.getName());
    }


    @Test
    void searchList(){
        List<Member> result = queryFactory
                .selectFrom(member)
                .fetch();
        Assertions.assertEquals(result.size(),4);

        QueryResults<Member> result2 = queryFactory
                .selectFrom(member)
                .fetchResults();
        Assertions.assertEquals(result2.getTotal(),4);
    }

    @Test
    void sort(){
        em.persist(new Member("member5",100,null));
        em.persist(new Member("member6",100,null));
        em.persist(new Member(null,40));
        List<Member> result = queryFactory
                .selectFrom(member)
                .orderBy(member.username.desc().nullsLast(),member.age.desc())
                .fetch();
        result.stream().forEach(System.out::println);
        Assertions.assertEquals(result.get(0).getAge(),100);

    }

    @Test
    void paging(){
        QueryResults<Member> result = queryFactory.selectFrom(member)
                .orderBy(member.id.desc())
                .offset(1)
                .limit(2)
                .fetchResults();

        assertThat(result.getTotal()).isEqualTo(4);
        assertThat(result.getLimit()).isEqualTo(2);
        assertThat(result.getOffset()).isEqualTo(1);
        result.getResults().stream().forEach(System.out::println);
    }

    @Test
    void group(){
        List<Tuple> result = queryFactory.select(
                member.count()
                ,member.age.max()
                ,member.age.min())
                .from(member)
                .fetch();
       // result.stream().forEach(System.out::println);
        Tuple tuple = result.get(0);
        assertThat(tuple.get(member.count())).isEqualTo(4);
        assertThat(tuple.get(member.age.max())).isEqualTo(35);
        assertThat(tuple.get(member.age.min())).isEqualTo(20);

    }
    @Test
    void joinAndGroupBy(){
        List<Tuple> result = queryFactory
                .select(team.name, member.age.avg())
                .from(member)
                .join(member.team, team)
                .groupBy(team.id)
                .fetch();
        result.stream().forEach(System.out::println);
        Tuple tuple = result.get(0);
        assertThat(tuple.get(member.age.avg())).isEqualTo(25);

    }
    @Test
    void join(){
        List<Member> result = queryFactory.select(member)
                .from(member)
                .join(member.team , team)
                .where(team.name.eq("team1"))
                .fetch();

        assertThat(result)
                .extracting("username")
                .containsExactly("member1","member2");
    }

    @Test
    void theta_join(){
        em.persist(new Member("team1"));
        em.persist(new Member("team2"));
        em.persist(new Member("team3"));

        List<Member> result = queryFactory
                .select(member)
                .from(member,team)
                .where(member.username.eq(team.name))
                .fetch();

        assertThat(result)
                .extracting("username")
                .containsExactly("team1","team2");
    }
    @Test
    void left_join(){
        em.persist(new Member("member5",100,null));
        em.persist(new Member("member6",100,null));
        List<Tuple> result = queryFactory.select(member,team)
                .from(member)
                .leftJoin(member.team , team)
               // .where(team.name.eq("team1"))
                .fetch();

        result.stream().forEach(System.out::println);
    }
    @Test
    void left_join_on(){
        em.persist(new Member("member5",100,null));
        em.persist(new Member("member6",100,null));
        List<Tuple> result = queryFactory.select(member,team)
                .from(member)
                .leftJoin(member.team , team)
                .on(team.name.eq("team1"))
                .fetch();

        result.stream().forEach(System.out::println);
    }

    @Test
    void join_where(){
        em.persist(new Member("member5",100,null));
        em.persist(new Member("member6",100,null));
        List<Tuple> result = queryFactory.select(member,team)
                .from(member)
                .join(member.team , team)
                .where(team.name.eq("team1"))
                .fetch();

        result.stream().forEach(System.out::println);
        assertThat(result.size()).isEqualTo(2);
    }

    @Test
    void join_where_on(){
        em.persist(new Member("member5",100,null));
        em.persist(new Member("member6",100,null));
        List<Tuple> result = queryFactory.select(member,team)
                .from(member)
                .join(member.team , team)
                .on(team.name.eq("team1"))
                .fetch();

        result.stream().forEach(System.out::println);
        //assertThat(result.size()).isEqualTo(2);
    }
}
