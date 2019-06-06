package com.kang.shop.es.base;

import com.kang.shop.common.web.Code;
import com.kang.shop.common.web.Message;
import com.kang.shop.common.web.ResultVo;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.index.query.QueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Result;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@NoRepositoryBean
public abstract class BaseEsController<T extends BaseEsEntity, PK extends Serializable, M extends BaseEsService<T, PK>>  {

    @Autowired
    protected M baseService;


    @ApiOperation("根据id查询")
    @GetMapping(value = {"findById/{id}"})
    public ResultVo findById(@PathVariable PK id) {
        return ResultVo.result(this.baseService.findById(id),Code.OK_CODE,Message.OK);
    }

    @ApiOperation("分页查询")
    @GetMapping(value = {"/"})
    public ResultVo get(HttpServletRequest request, HttpServletResponse response, @ApiParam("页码") @RequestParam(required = false) Integer page, @ApiParam("每页条数") @RequestParam(required = false) Integer limit, @ApiParam("排序方式") @RequestParam(required = false) String sortBy, @ApiParam("正序/倒序") @RequestParam(required = false) Integer direction) {
        int pageNumber = 0;
        int pageSize = 20;
        if (page != null) {
            pageNumber = Integer.valueOf(page) - 1;
        }

        if (limit != null) {
            pageSize = Integer.valueOf(limit);
        }

        Sort.Direction direct = Sort.Direction.DESC;
        if (StringUtils.isBlank(sortBy)) {
            sortBy = "id";
        }

        if (direction != null) {
            direct = direction == 0 ? Sort.Direction.ASC : Sort.Direction.DESC;
        }

        Sort sorter = new Sort(direct, new String[]{sortBy});
        Pageable pageable = new PageRequest(pageNumber, pageSize, sorter);
        Page<T> pages = this.baseService.findAll(pageable);
        return ResultVo.result(pages,Code.OK_CODE,Message.OK);
    }

    @ApiOperation("根据id更新实体")
    @PutMapping(value = {"/{id}"})
    public ResultVo update(@PathVariable PK id, @RequestBody T model) {
        model.setId(String.valueOf(id));
        model.setUpdateTime(new Date());
        model = this.baseService.save(model);
        return ResultVo.result(model,Code.OK_CODE,Message.OK);
    }

    @ApiOperation("保存实体")
    @PostMapping(value = {"/"})
    public ResultVo save(@RequestBody T entity) {
        entity.setCreateTime(new Date());
        entity.setUpdateTime(new Date());
        return ResultVo.result(this.baseService.save(entity),Code.OK_CODE,Message.OK);
    }

    @ApiOperation("根据id删除实体")
    @DeleteMapping(value = {"{id}"})
    public ResultVo delete(@PathVariable PK id) {
        this.baseService.delete(id);
        return ResultVo.result(Code.OK_CODE,Message.OK);
    }

    @ApiOperation("保存实体列表")
    @PostMapping(value = {"/saveEntities"})
    public ResultVo save(@RequestBody List<T> entities) {
        return ResultVo.result(this.baseService.save(entities),Code.OK_CODE,Message.OK);
    }

    @ApiOperation("根据id判断数据是否存在")
    @GetMapping(value = {"/exists/{id}"})
    public ResultVo exists(@PathVariable PK id) {
        return ResultVo.result(this.baseService.exists(id),Code.OK_CODE,Message.OK);
    }

    public Iterable<T> findAll() {
        return this.baseService.findAll();
    }

    @ApiOperation("根据id列表批量查询")
    @GetMapping(value = {"/findByIds"})
    public ResultVo findAll(@RequestParam List<PK> pks) {
        return ResultVo.result(this.baseService.findAll(pks),Code.OK_CODE,Message.OK);
    }

    @ApiOperation("统计数据总数")
    @GetMapping(value = {"/count"})
    public ResultVo count() {
        return ResultVo.result(this.baseService.count(),Code.OK_CODE,Message.OK);
    }

    @ApiOperation("批量删除实体")
    @DeleteMapping(value = {"/deleteByIds"})
    public ResultVo delete(@RequestBody List<T> entities) {
        this.baseService.delete(entities);
        return ResultVo.result(Code.OK_CODE,Message.OK);
    }

    @ApiOperation("字符串查询")
    @GetMapping({"queryStringQuery"})
    public ResultVo queryStringQuery(@RequestParam String queryString) {
        return ResultVo.result(this.baseService.queryStringQuery(queryString),Code.OK_CODE,Message.OK);
    }

    public Iterable<T> findAll(Sort sorter) {
        return this.baseService.findAll(sorter);
    }

    public T index(T entity) {
        return this.baseService.index(entity);
    }

    public Iterable<T> search(QueryBuilder queryBuilder) {
        return this.baseService.search(queryBuilder);
    }

    public Page<T> search(QueryBuilder queryBuilder, Pageable pageable) {
        return this.baseService.search(queryBuilder, pageable);
    }

    public Page<T> search(SearchQuery searchQuery) {
        return this.baseService.search(searchQuery);
    }

    public Page<T> searchSimilar(T entity, String[] var2, Pageable pageable) {
        return this.baseService.searchSimilar(entity, var2, pageable);
    }

    public void refresh() {
        this.baseService.refresh();
    }

}
