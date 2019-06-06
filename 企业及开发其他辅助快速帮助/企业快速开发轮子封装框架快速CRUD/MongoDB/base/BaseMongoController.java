package com.kang.shop.mongo.base;

import com.kang.shop.common.web.Code;
import com.kang.shop.common.web.Message;
import com.kang.shop.common.web.ResultVo;
import com.kang.shop.mongo.entity.ExamplePageRequest;
import com.kang.shop.mongo.entity.ExampleRequest;
import com.kang.shop.mongo.entity.PageRequest;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * @Author BigKang
 * @Date 2019/5/8 16:45
 * @Summarize BaseMongo的Controller层简化开发
 */
public class BaseMongoController<T extends BaseMongoEntity, PK extends Serializable, S extends BaseMongoService<T, PK>> {

    @Autowired
    protected S baseService;

    @ApiOperation("分页查询")
    @PostMapping(value = {"/page"})
    public ResultVo get(HttpServletRequest request, HttpServletResponse response, @ApiParam("分页参数") @RequestBody PageRequest pageRequest) {
        return ResultVo.result(this.baseService.findAll(pageRequest.pageable()), Code.OK_CODE,Message.OK);
    }

    @ApiOperation("分页实例查询")
    @PostMapping(value = {"/findByExamplePage"})
    public ResultVo findByExamplePage(HttpServletRequest request, HttpServletResponse response, @ApiParam("实例及分页参数、排序方式") @RequestBody ExamplePageRequest<T> exampleRequest) {
        Pageable pageable = exampleRequest.page().pageable();
        Example<T> example = exampleRequest.example();
        Page<T> all = baseService.findAll(example, pageable);
        return ResultVo.result(all,Code.OK_CODE,Message.OK);
    }

    @ApiOperation("统计实例数目")
    @PostMapping(value = {"/countByExample"})
    public ResultVo countByExample(HttpServletRequest request, HttpServletResponse response, @ApiParam("实例") @RequestBody ExampleRequest<T> exampleRequest) {
        Example<T> example = exampleRequest.example();
        Long count = baseService.count(example);
        return ResultVo.result(count,Code.OK_CODE,Message.OK);
    }

    @ApiOperation("判断实例是否存在")
    @PostMapping(value = {"/exists"})
    public ResultVo exists(HttpServletRequest request, HttpServletResponse response, @ApiParam("实例") @RequestBody ExampleRequest<T> exampleRequest) {
        Example<T> example = exampleRequest.example();
        boolean exists = baseService.exists(example);
        return ResultVo.result(exists,Code.OK_CODE,Message.OK);
    }

    @ApiOperation("根据id更新实体")
    @PutMapping(value = {"/{id}"})
    public ResultVo update(@PathVariable PK id, @RequestBody T entity) {
        entity.setId(String.valueOf(id));
        entity.setUpdateTime(new Date());
        entity = this.baseService.save(entity);
        return ResultVo.result(entity, Code.OK_CODE);
    }

    @ApiOperation("保存实体")
    @PostMapping(value = {"/"})
    public ResultVo save(@RequestBody T entity) {
        entity.setCreateTime(new Date());
        entity.setUpdateTime(new Date());
        entity = this.baseService.save(entity);
        return ResultVo.result(entity, Code.OK_CODE,Message.OK);
    }

    @ApiOperation("保存多个实体")
    @PostMapping(value = {"/entities"})
    public ResultVo saveEntities(@RequestBody List<T> entities) {
        Iterator var2 = entities.iterator();

        while (var2.hasNext()) {
            T entity = (T) var2.next();
            entity.setCreateTime(new Date());
            entity.setUpdateTime(new Date());
        }

        List<T> entityList = this.baseService.insert(entities);
        return ResultVo.result(entityList, Code.OK_CODE,Message.OK);
    }

    @ApiOperation("根据ID删除实体")
    @DeleteMapping(value = {"/{id}"})
    public ResultVo deleteEntityById(@PathVariable PK id) {
        this.baseService.deleteById(id);
        return ResultVo.result((Object) null, Code.OK_CODE);
    }

    @ApiOperation("删除多个实体")
    @DeleteMapping(value = {"/deleteBatch"})
    public ResultVo deleteEntities(@RequestBody List<T> entities) {
        this.baseService.deleteAll(entities);
        return ResultVo.result((Object) null, Code.OK_CODE);
    }

    public Iterable<T> findAll() {
        return this.baseService.findAll();
    }

    @ApiOperation("统计数据总数")
    @GetMapping(value = {"/count"})
    public  ResultVo count() {
        Long count = this.baseService.count();
        return ResultVo.result(count,Code.OK_CODE);
    }

    @ApiOperation("多功能通用分页查询")
    @PutMapping("/page")
    public ResultVo currencySearch(){
        baseService.findAll();
        return null;
    }
}
