package com.kang.shop.usertestserver.entity;

import com.kang.shop.jpa.base.BaseJpaEntity;
import lombok.Data;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Table;


@Data
@ToString
@Entity
@Table(name = "t_test_jpa")
public class TestJpa extends BaseJpaEntity {

    private String username;
    private String email;
    private String birthday;
    private String password;
    private Integer age;
    private String address;
    private String phone;

}
