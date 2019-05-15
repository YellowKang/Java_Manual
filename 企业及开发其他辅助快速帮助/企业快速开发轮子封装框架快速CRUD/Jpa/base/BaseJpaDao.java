package com.kang.shop.jpa.base;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@NoRepositoryBean
public interface BaseJpaDao<T extends BaseJpaEntity, PK extends Serializable> extends JpaRepository<T, PK>, JpaSpecificationExecutor<T> {

    Page<T> findByCreateTime(Date createBy, Pageable page);

    List<T> findByCreateTime(Date createBy);

    Page<T> findByUpdateTime(Date modifiedBy, Pageable page);

    List<T> findByUpdateTime(Date modifiedBy);

}
