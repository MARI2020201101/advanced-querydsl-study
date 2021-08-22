package com.mari.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.persistence.EntityManager;

@SpringBootApplication
public class QuerydslAdvanceStudyApplication {

	public static void main(String[] args) {
		SpringApplication.run(QuerydslAdvanceStudyApplication.class, args);
	}

	@Bean
	public JPAQueryFactory queryFactory(EntityManager em){
		return new JPAQueryFactory(em);
	}
}
