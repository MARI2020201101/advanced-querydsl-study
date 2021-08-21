package com.mari.querydsl.entity;

import lombok.Data;
import lombok.Getter;

import org.springframework.data.jpa.repository.EntityGraph;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Data
public class Hello {

    @Id
    @GeneratedValue
    private Long id;
}
