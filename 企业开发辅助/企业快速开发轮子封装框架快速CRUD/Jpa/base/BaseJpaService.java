package com.kang.shop.jpa.base;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @Author BigKang
 * @Date 2019/5/12 16:27
 * 通用Service接口层，，基于Jpa快速开发
 */
public interface BaseJpaService<T extends BaseJpaEntity, PK extends Serializable> {

    List<T> findAll();

    Page<T> findAll(Pageable page);

    List<T> findAll(Specification<T> specification);

    List<T> findAll(Specification<T> specification, Sort sort);

    Page<T> findAll(Specification specification, Pageable pageable);

    Optional<T> findById(PK id);

    Optional<T> findOne(Specification<T> specification);

    T save(T entity);

    T update(PK id,T entity);

    List<T> save(Iterable<T> entities);

    void delete(PK id);

    void deleteInBatch(Iterable<T> entities);

    long count(Specification<T> specification);

    Page<T> findByCreateTime(Date var1, Pageable var2);

    List<T> findByCreateTime(Date var1);

    Page<T> findByUpdateTime(Date var1, Pageable var2);

    List<T> findByUpdateTime(Date var1);

    Long count();

}
