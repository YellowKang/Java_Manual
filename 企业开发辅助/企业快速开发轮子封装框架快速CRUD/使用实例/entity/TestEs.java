package com.kang.shop.usertestserver.entity;

import com.kang.shop.es.base.BaseEsEntity;
import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

@Data
@Document(indexName = "testes",type = "test")
public class TestEs extends BaseEsEntity {

    @Field(index = true,analyzer = "ik_max_word",searchAnalyzer = "ik_max_word")
    private String name;
    private String email;
    @Field(index = true,analyzer = "ik_max_word",searchAnalyzer = "ik_max_word")
    private String address;
    @Field(index = true)
    private Integer age;
    private String url;
    @Field(index = true,analyzer = "ik_max_word",searchAnalyzer = "ik_max_word")
    private String title;

}
