package com.mari.querydsl.repository;

import com.mari.querydsl.entity.Member;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberRepositoryTest {

    @Autowired
    private MemberRepository repository;
    @Autowired
    private EntityManager em;

    @Test
    void selectTest(){
        em.persist(new Member("member1",0,null));
        em.flush();
        em.clear();

        List<Member> result = repository.findByUsername("member1");
        assertThat(result.get(0).getUsername()).isEqualTo("member1");

    }
}