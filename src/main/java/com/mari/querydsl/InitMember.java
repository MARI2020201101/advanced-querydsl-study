package com.mari.querydsl;

import com.mari.querydsl.entity.Member;
import com.mari.querydsl.entity.Team;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Random;
import java.util.stream.IntStream;

@Profile("local")
@Component
@RequiredArgsConstructor
public class InitMember {
    private final InitMemberService initMemberService;

    @PostConstruct
    public void init() {
        initMemberService.init();
    }

    @Component
    @RequiredArgsConstructor
    static class InitMemberService {

        private final EntityManager em;

        @Transactional
        public void init(){
            Team teamA = new Team("teamA");
            Team teamB = new Team("teamB");

            em.persist(teamA);
            em.persist(teamB);
            IntStream.rangeClosed(1,100).forEach(i->{
                int age = (int) ((Math.random()*50)+1);
                if(i%2==0){
                    em.persist(new Member("member"+i, age, teamA));
                }else{
                    em.persist(new Member("member"+i, age, teamB));
                }
            });

        }
    }
}
