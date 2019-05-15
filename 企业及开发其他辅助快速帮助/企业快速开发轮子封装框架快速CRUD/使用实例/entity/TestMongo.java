package com.kang.shop.usertestserver.entity;

import com.kang.shop.mongo.base.BaseMongoEntity;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@ToString
@Document(collection = "test")
public class TestMongo extends BaseMongoEntity {

    private String username;
    private String email;
    private String birthday;
    private String password;
    private Integer age;
    private String address;
    private String phone;
}
