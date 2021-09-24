package com.kang.shop.es.base;

import org.elasticsearch.index.query.QueryBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.query.SearchQuery;

import java.io.Serializable;

public interface BaseEsService<T extends BaseEsEntity, ID extends Serializable> {

    T findById(ID var1);

    T save(T var1);

    Iterable<T> save(Iterable<T> var1);

    boolean exists(ID var1);

    Iterable<T> findAll();

    Iterable<T> findAll(Iterable<ID> var1);

    long count();

    void delete(ID var1);

    void delete(T var1);

    void delete(Iterable<? extends T> var1);

    void deleteAll();

    Iterable<T> findAll(Sort var1);

    Page<T> findAll(Pageable var1);

    T index(T var1);

    Iterable<T> search(QueryBuilder var1);

    Page<T> search(QueryBuilder var1, Pageable var2);

    Page<T> search(SearchQuery var1);

    Page<T> searchSimilar(T var1, String[] var2, Pageable var3);

    void refresh();

    Page<T> queryStringQuery(String var1);

    long count(QueryBuilder var1);

    long count(SearchQuery var1);


}
