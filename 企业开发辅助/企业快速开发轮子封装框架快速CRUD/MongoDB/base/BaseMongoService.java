package com.kang.shop.mongo.base;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.Serializable;
import java.util.List;

/**
 * @Author BigKang
 * @Date 2019/5/8 16:46
 * @Summarize MongoService父类，简化开发
 */
public interface BaseMongoService<T extends BaseMongoEntity, ID extends Serializable> {

    /**
     * 添加方法
     * @param entity
     * @return
     */
    T save(T entity);

    /**
     * 查询所有
     * @return
     */
    Iterable<T> findAll();

    /**
     * 统计数量
     * @return
     */
    long count();

    /**
     * 根据实体条件删除
     * @param entity
     */
    void delete(T entity);

    /**
     * 根据id删除
     * @param id
     */
    void deleteById(ID id);

    /**
     * 删除多条
     * @param entities
     */
    void deleteAll(Iterable<T> entities);

    /**
     * 查询所有并排序
     * @param sorter
     * @return
     */
    Iterable<T> findAll(Sort sorter);

    /**
     * 分页查询
     * @param pageable
     * @return
     */
    Page<T> findAll(Pageable pageable);

    <S extends T> Iterable<S> findAll(Example<S> example);

    <S extends T> Iterable<S> findAll(Example<S> example, Sort sorter);

    <S extends T> Page<S> findAll(Example<S> example, Pageable pageable);

    <S extends T> long count(Example<S> example);

    <S extends T> boolean exists(Example<S> example);

    <S extends T> S insert(S s);

    <S extends T> List<S> insert(Iterable<S> s);
}
