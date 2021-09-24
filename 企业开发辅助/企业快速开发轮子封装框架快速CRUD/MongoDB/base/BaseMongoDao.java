package com.kang.shop.mongo.base;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;

/**
 * @Author BigKang
 * @Date 2019/5/8 16:45
 * @Summarize BaseMongo的Dao层，用于dao层继承
 */
@NoRepositoryBean
public interface BaseMongoDao<T extends BaseMongoEntity, PK extends Serializable> extends MongoRepository<T, PK> {
}
