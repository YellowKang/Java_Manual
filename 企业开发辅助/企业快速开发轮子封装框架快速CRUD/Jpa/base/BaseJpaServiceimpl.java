package com.kang.shop.jpa.base;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class BaseJpaServiceimpl<T extends BaseJpaEntity, PK extends Serializable, M extends BaseJpaDao<T, PK>> implements BaseJpaService<T, PK> {

    @Autowired
    protected M baseDao;

    public Page<T> findAll(Pageable page) {
        Page<T> result = this.baseDao.findAll(page);
        return result;
    }

    public Optional<T> findById(PK id) {
        return this.baseDao.findById(id);
    }

    public T save(T entity) {
        Date date = new Date();
        Long id = entity.getId();
        if (id == null) {
            entity.setCreateTime(date);
        } else {
            entity.setUpdateTime(date);
        }

        return this.baseDao.saveAndFlush(entity);
    }

    @Override
    public T update(PK id, T entity) {
        if(id != null && entity != null){
            entity.setId(Long.valueOf(String.valueOf(id)));
        }
        entity.setUpdateTime(new Date());
        return baseDao.saveAndFlush(entity);
    }

    public List<T> save(Iterable<T> entities) {
        return this.baseDao.saveAll(entities);
    }

    public List<T> findAll() {
        return this.baseDao.findAll();
    }

    public void delete(PK id) {
        this.baseDao.deleteById(id);
    }

    public void deleteInBatch(Iterable<T> entities) {
        this.baseDao.deleteInBatch(entities);
    }

    public Page<T> findAll(Specification specification, Pageable pageable) {
        return this.baseDao.findAll(specification, pageable);
    }

    public Optional<T> findOne(Specification<T> specification) {
        return this.baseDao.findOne(specification);
    }

    public List<T> findAll(Specification<T> specification) {
        return this.baseDao.findAll(specification);
    }

    public List<T> findAll(Specification<T> specification, Sort sort) {
        return this.baseDao.findAll(specification, sort);
    }

    public long count(Specification<T> specification) {
        return this.baseDao.count(specification);
    }

    public Page<T> findByCreateTime(Date createdBy, Pageable page) {
        return this.baseDao.findByCreateTime(createdBy, page);
    }

    public List<T> findByCreateTime(Date createdBy) {
        return this.baseDao.findByCreateTime(createdBy);
    }

    public Page<T> findByUpdateTime(Date modifiedBy, Pageable page) {
        return this.baseDao.findByUpdateTime(modifiedBy, page);
    }

    public List<T> findByUpdateTime(Date modifiedBy) {
        return this.baseDao.findByUpdateTime(modifiedBy);
    }

    @Override
    public Long count() {
        return baseDao.count();
    }

}
