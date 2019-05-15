package com.kang.shop.mybatis.plus.base;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.kang.shop.common.web.Code;
import com.kang.shop.common.web.Message;
import com.kang.shop.common.web.ResultVo;
import com.kang.shop.mybatis.plus.entity.CurrencySearch;
import com.kang.shop.mybatis.plus.entity.DateParam;
import com.kang.shop.mybatis.plus.entity.PageRequest;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.xml.crypto.Data;
import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class BaseMPController<T, PK extends Serializable, M extends IService<T>> {

    @Autowired
    protected M baseService;

    @ApiOperation("根据id查询实体类")
    @GetMapping("/{id}")
    public ResultVo get(@PathVariable PK id){
        T byId = baseService.getById(id);

        return ResultVo.result(byId,Code.OK_CODE,Message.OK);
    }

    @ApiOperation("保存实体")
    @PostMapping("/")
    public ResultVo save(@RequestBody T t) {
            boolean save = baseService.save(t);
            if(save){
                return ResultVo.result(Code.OK_CODE,Message.OK);
            }else{
                return ResultVo.result(Code.Failure_CODE,Message.Failure);
            }
    }

    @ApiOperation("根据id删除实体")
    @DeleteMapping("/{id}")
    public ResultVo deleteById(@PathVariable PK id){
            boolean b = baseService.removeById(id);
            return ResultVo.result(Code.OK_CODE,Message.OK);
    }

    @ApiOperation("修改实体")
    @PutMapping("/")
    public ResultVo updateById(@RequestBody T t){

            boolean b = baseService.updateById(t);
            return ResultVo.result(Code.OK_CODE,Message.OK);
    }

    @ApiOperation("分页查询")
    @PostMapping("/page")
    public ResultVo page(@RequestBody PageRequest<T> request){
        IPage<T> page = baseService.page(request.generatePage());
        return ResultVo.result(page,Code.OK_CODE,Message.OK);
    }

    @ApiOperation("多功能通用分页查询")
    @PostMapping("/currencySearch")
    public ResultVo currencySearch(@RequestBody CurrencySearch<T> currencySearch) throws ParseException {
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        if(currencySearch.getQuery() != null && currencySearch.getQuery().size() > 0){
            currencySearch.getQuery().forEach((v)->{
                queryWrapper.in(v.getField(),v.getValue());
            });
        }

        if(currencySearch.getLike() != null && currencySearch.getLike().size() > 0){
            currencySearch.getLike().forEach(v -> {
                queryWrapper.like(v.getField(),v.getValue().get(0));
            });
        }

        if(currencySearch.getBetween() != null && currencySearch.getBetween().size() > 0){
            currencySearch.getBetween().forEach(v -> {
                queryWrapper.between(v.getField(),v.getStart(),v.getEnd());
            });
        }

        if(currencySearch.getDateParam() != null && !currencySearch.getDateParam().yesNull()){
            DateParam dateParam = currencySearch.getDateParam();
            if(!StringUtils.isEmpty(dateParam.getStartDate())){
                queryWrapper.ge("create_time",dateParam.start());
            }
            if(!StringUtils.isEmpty(dateParam.getEndDate())){
                queryWrapper.le("create_time",dateParam.end());
            }
        }

        if(currencySearch.getNot() != null && currencySearch.getNot().size() > 0){
            currencySearch.getNot().forEach(v -> {
                if(!StringUtils.isEmpty(v.getField())){
                    queryWrapper.notIn(v.getField(),v.getValue());
                }
            });
        }

        if (currencySearch.getPageOrder() == null || currencySearch.getPageOrder().generatePage() == null){
            currencySearch.setPageOrder(new PageRequest<T>());
        }

        if(currencySearch.getPageOrder() != null && currencySearch.getPageOrder().getOrder() != null &&  currencySearch.getPageOrder().getOrder().size() > 0){
            currencySearch.getPageOrder().getOrder().forEach(v -> {
                if(!StringUtils.isEmpty(v.getField())){
                    if("DESC".equals(v.getSort())){
                        queryWrapper.orderByDesc(v.getField());
                    }else if("ASC".equals(v.getSort())){
                        queryWrapper.orderByAsc(v.getField());
                    }else{
                        queryWrapper.orderByDesc(v.getField());
                    }
                }
            });
        }
        IPage<T> page = baseService.page(currencySearch.getPageOrder().generatePage(), queryWrapper);
        return ResultVo.result(page,Code.OK_CODE,Message.OK);
    }

    @ApiOperation("多功能通用统计")
    @PostMapping("/currencyCount")
    public ResultVo currencyCount(@RequestBody CurrencySearch<T> currencySearch) throws ParseException {
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        if(currencySearch.getQuery() != null && currencySearch.getQuery().size() > 0){
            currencySearch.getQuery().forEach((v)->{
                queryWrapper.in(v.getField(),v.getValue());
            });
        }

        if(currencySearch.getLike() != null && currencySearch.getLike().size() > 0){
            currencySearch.getLike().forEach(v -> {
                queryWrapper.like(v.getField(),v.getValue().get(0));
            });
        }

        if(currencySearch.getBetween() != null && currencySearch.getBetween().size() > 0){
            currencySearch.getBetween().forEach(v -> {
                queryWrapper.between(v.getField(),v.getStart(),v.getEnd());
            });
        }

        if(currencySearch.getDateParam() != null && !currencySearch.getDateParam().yesNull()){
            DateParam dateParam = currencySearch.getDateParam();
            if(!StringUtils.isEmpty(dateParam.getStartDate())){
                queryWrapper.ge("create_time",dateParam.start());
            }
            if(!StringUtils.isEmpty(dateParam.getEndDate())){
                queryWrapper.le("create_time",dateParam.end());
            }
        }

        if(currencySearch.getNot() != null && currencySearch.getNot().size() > 0){
            currencySearch.getNot().forEach(v -> {
                if(!StringUtils.isEmpty(v.getField())){
                    queryWrapper.notIn(v.getField(),v.getValue());
                }
            });
        }
        Integer integer = baseService.count(queryWrapper);
        Map<String,Object> map = new HashMap<>();
        map.put("count",integer);
        return ResultVo.result(map,Code.OK_CODE,Message.OK);
    }
}
