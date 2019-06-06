package com.kang.shop.es.base;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.SearchQuery;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;

public class BaseEsServiceImpl<T extends BaseEsEntity, PK extends Serializable,M extends BaseEsDao<T,PK>> implements BaseEsService<T, PK> {

    @Autowired
    protected M baseDao;

    @Autowired
    protected ElasticsearchTemplate elasticsearchTemplate;


    public T findById(PK id) {
        return this.baseDao.findById(id).get();
    }

    public T save(T entity) {
        return  this.baseDao.save(entity);
    }

    public Iterable<T> save(Iterable<T> entities) {
        return this.baseDao.saveAll(entities);
    }

    public boolean exists(PK pk) {
        return this.baseDao.existsById(pk);
    }

    public Iterable<T> findAll() {
        return this.baseDao.findAll();
    }

    public Iterable<T> findAll(Iterable<PK> pks) {
        return this.baseDao.findAllById(pks);
    }

    public long count() {
        return this.baseDao.count();
    }

    public void delete(PK pk) {
        this.baseDao.deleteById(pk);
    }

    public void delete(T entity) {
        this.baseDao.delete(entity);
    }

    public void delete(Iterable<? extends T> entities) {
        this.baseDao.deleteAll(entities);
    }

    public void deleteAll() {
        this.baseDao.deleteAll();
    }

    public Iterable<T> findAll(Sort sorter) {
        return this.baseDao.findAll(sorter);
    }

    public Page<T> findAll(Pageable pageable) {
        return this.baseDao.findAll(pageable);
    }

    public T index(T entity) {
        return this.baseDao.index(entity);
    }

    public Iterable<T> search(QueryBuilder queryBuilder) {
        return this.baseDao.search(queryBuilder);
    }

    public Page<T> search(QueryBuilder queryBuilder, Pageable pageable) {
        return this.baseDao.search(queryBuilder, pageable);
    }

    public Page<T> search(SearchQuery searchQuery) {
        return this.baseDao.search(searchQuery);
    }

    public Page<T> searchSimilar(T entity, String[] var2, Pageable pageable) {
        return this.baseDao.searchSimilar(entity, var2, pageable);
    }

    public void refresh() {
        this.baseDao.refresh();
    }

    public Page<T> queryStringQuery(String queryString) {
        return this.elasticsearchTemplate.queryForPage(new NativeSearchQuery(QueryBuilders.queryStringQuery(queryString)), (Class)((ParameterizedType)this.getClass().getGenericSuperclass()).getActualTypeArguments()[0]);
    }

    public long count(QueryBuilder queryBuilder) {
        return this.search(queryBuilder, new PageRequest(0, 1)).getTotalElements();
    }

    public long count(SearchQuery searchQuery) {
        return this.search(searchQuery).getTotalElements();
    }


}
