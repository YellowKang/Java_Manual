package com.kang.shop.mongo.base;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.Serializable;
import java.util.List;

/**
 * MongoBaseService实现类，封装大量方法
 * @param <T>
 * @param <PK>
 */
public class BaseMongoServiceImpl<T extends BaseMongoEntity, PK extends Serializable, M extends BaseMongoDao<T, PK>> implements BaseMongoService<T, PK> {
    @Autowired
    protected M baseDao;

    public T save(T entity) {
        return (T) this.baseDao.save(entity);
    }

    public Iterable<T> findAll() {
        return this.baseDao.findAll();
    }

    public long count() {
        return this.baseDao.count();
    }

    public void delete(T entity) {
        this.baseDao.delete(entity);
    }

    public void deleteById(PK pk) {
        this.baseDao.deleteById(pk);
    }

    public void deleteAll() {
        this.baseDao.deleteAll();
    }

    public void deleteAll(Iterable<T> entities) {
        this.baseDao.deleteAll(entities);
    }

    public Page<T> findAll(Pageable pageable) {
        return this.baseDao.findAll(pageable);
    }

    public <S extends T> Iterable<S> findAll(Example<S> example) {
        return this.baseDao.findAll(example);
    }

    public <S extends T> Iterable<S> findAll(Example<S> example, Sort sorter) {
        return this.baseDao.findAll(example, sorter);
    }

    public <S extends T> Page<S> findAll(Example<S> example, Pageable pageable) {
        return this.baseDao.findAll(example, pageable);
    }

    public <S extends T> long count(Example<S> example) {
        return this.baseDao.count(example);
    }

    public <S extends T> boolean exists(Example<S> example) {
        return this.baseDao.exists(example);
    }

    public <S extends T> S insert(S s) {
        return (S) this.baseDao.insert(s);
    }

    public <S extends T> List<S> insert(Iterable<S> s) {
        return this.baseDao.insert(s);
    }

    public Iterable<T> findAll(Sort sorter) {
        return this.baseDao.findAll(sorter);
    }

}
