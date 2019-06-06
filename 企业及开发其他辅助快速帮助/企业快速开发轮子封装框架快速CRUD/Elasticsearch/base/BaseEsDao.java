package com.kang.shop.es.base;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;

@NoRepositoryBean
public interface BaseEsDao <T extends BaseEsEntity, PK extends Serializable> extends ElasticsearchRepository<T, PK> {

}
