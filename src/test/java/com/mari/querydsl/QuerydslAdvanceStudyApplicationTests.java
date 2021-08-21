package com.mari.querydsl;

import com.mari.querydsl.entity.Hello;
import com.mari.querydsl.entity.QHello;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

@SpringBootTest
class QuerydslAdvanceStudyApplicationTests {

	@Autowired
	private EntityManager em;

	@Test
	@Commit
	@Transactional
	void contextLoads() {

		Hello hello = new Hello();
		em.persist(hello);
		JPAQueryFactory jpaQueryFactory = new JPAQueryFactory(em);
		QHello qHello = QHello.hello;
		Hello result = jpaQueryFactory
				.selectFrom(qHello)
				.fetchOne();
		Assertions.assertThat(result.getId()).isEqualTo(hello.getId());
	}

}
