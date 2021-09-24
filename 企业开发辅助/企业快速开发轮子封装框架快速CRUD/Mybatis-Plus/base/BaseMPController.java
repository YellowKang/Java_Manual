package com.kang.shop.mybatis.plus.base;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.kang.shop.common.web.Code;
import com.kang.shop.common.web.Message;
import com.kang.shop.common.web.ResultVo;
import com.kang.shop.mybatis.plus.entity.CurrencySearch;
import com.kang.shop.mybatis.plus.entity.DateParam;
import com.kang.shop.mybatis.plus.entity.PageRequest;
import com.kang.shop.mybatis.plus.vo.CurrencyUpdateVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author BigKang
 * @Date 2019/5/13 9:23
 * @Summarize Mybatis-Plus通用BaseController
 */
public class BaseMPController<T, PK extends Serializable, M extends IService<T>> {

    @Autowired
    protected M baseService;

    /**
     * 根据id主键进行查询
     * @param id
     * @return
     */
    @ApiOperation("根据id查询实体类")
    @GetMapping("/{id}")
    public ResultVo get(@PathVariable PK id){
        T byId = baseService.getById(id);

        return ResultVo.result(byId,Code.OK_CODE,Message.OK);
    }

    /**
     * 根据类型保存实体类
     * @param t
     * @return
     */
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

    /**
     * 根据id主键进行删除实体类
     * @param id
     * @return
     */
    @ApiOperation("根据id删除实体")
    @DeleteMapping("/{id}")
    public ResultVo deleteById(@PathVariable PK id){
            boolean b = baseService.removeById(id);
            return ResultVo.result(Code.OK_CODE,Message.OK);
    }

    /**
     * 根据实体类型获取id修改实体类
     * @param t
     * @return
     */
    @ApiOperation("修改实体")
    @PutMapping("/")
    public ResultVo updateById(@RequestBody T t){
            boolean b = baseService.updateById(t);
            return ResultVo.result(Code.OK_CODE,Message.OK);
    }

    /**
     * 分页查询，根据请求参数进行分页查询
     * @param request
     * @return
     */
    @ApiOperation("分页查询")
    @PostMapping("/page")
    public ResultVo page(@RequestBody PageRequest<T> request){
        IPage<T> page = baseService.page(request.generatePage());
        return ResultVo.result(page,Code.OK_CODE,Message.OK);
    }

    /**
     * 多功能通用查询接口
     * @param currencySearch 多功能通用查询实体类参数
     * @return
     * @throws ParseException
     */
    @ApiOperation("多功能通用分页查询")
    @PostMapping("/currencySearch")
    public ResultVo currencySearch(@RequestBody CurrencySearch<T> currencySearch) throws ParseException {
        QueryWrapper<T> queryWrapper = searchPageQueryWrapper(currencySearch);
        IPage<T> page = baseService.page(currencySearch.getPageOrder().generatePage(), queryWrapper);
        return ResultVo.result(page,Code.OK_CODE,Message.OK);
    }

    /**
     * 多功能通用统计接口
     * @param currencySearch
     * @return
     * @throws ParseException
     */
    @ApiOperation("多功能通用统计")
    @PostMapping("/currencyCount")
    public ResultVo currencyCount(@RequestBody CurrencySearch<T> currencySearch) throws ParseException {
        QueryWrapper<T> queryWrapper = searchQueryWrapper(currencySearch);
        Integer integer = baseService.count(queryWrapper);
        Map<String,Object> map = new HashMap<>();
        map.put("count",integer);
        return ResultVo.result(map,Code.OK_CODE,Message.OK);
    }


    @ApiOperation("多功能通用修改")
    @PutMapping("/currencyUpdate")
    public ResultVo currencyUpdate(@RequestBody CurrencyUpdateVo<T> updateVo) throws ParseException {
        UpdateWrapper<T> updateWrapper = updateWrapper(updateVo.getCurrencySearch());
        boolean update = baseService.update(updateVo.getEntity(), updateWrapper);
        return ResultVo.result(update ? "修改成功":"修改失败",Code.OK_CODE,Message.OK);
    }


    /**
     * 查询条件分页封装父接口
     * @param currencySearch
     * @return
     */
    protected QueryWrapper<T> searchPageQueryWrapper(CurrencySearch<T> currencySearch) throws ParseException {
        QueryWrapper<T> queryWrapper = searchQueryWrapper(currencySearch);
        //不传参数分页
        if (currencySearch == null || currencySearch.getPageOrder() == null || currencySearch.getPageOrder().generatePage() == null){
            currencySearch.setPageOrder(new PageRequest<T>());
        }
        //排序
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
        return queryWrapper;
    }


    /**
     * 查询条件封装父接口
     * @param currencySearch
     * @return
     * @throws ParseException
     */
    protected QueryWrapper<T> searchQueryWrapper(CurrencySearch<T> currencySearch) throws ParseException {
        if(currencySearch == null || ObjectUtils.isEmpty(currencySearch)){
            return null;
        }
        //等于条件
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        if(currencySearch.getQuery() != null && currencySearch.getQuery().size() > 0){
            currencySearch.getQuery().forEach((v)->{
                queryWrapper.eq(v.getField(),v.getValue());
            });
        }
        //创建in条件
        if(currencySearch.getIn() != null && currencySearch.getIn().size() > 0){
            currencySearch.getIn().forEach(v -> {
                queryWrapper.in(v.getField(),v.getValue());
            });
        }
        //模糊检索
        if(currencySearch.getLike() != null && currencySearch.getLike().size() > 0){
            currencySearch.getLike().forEach(v -> {
                queryWrapper.like(v.getField(),v.getValue());
            });
        }
        //查询范围条件
        if(currencySearch.getBetween() != null && currencySearch.getBetween().size() > 0){
            currencySearch.getBetween().forEach(v -> {
                if (v.getStart() != null){
                    queryWrapper.ge(v.getField(),v.getStart());
                }
                if (v.getEnd() != null){
                    queryWrapper.le(v.getField(),v.getEnd());
                }
            });
        }
        //时间范围查询
        if(currencySearch.getDateParam() != null && !currencySearch.getDateParam().yesNull()){
            DateParam dateParam = currencySearch.getDateParam();
            if(!StringUtils.isEmpty(dateParam.getStartDate())){
                queryWrapper.ge("create_time",dateParam.start());
            }
            if(!StringUtils.isEmpty(dateParam.getEndDate())){
                queryWrapper.le("create_time",dateParam.end());
            }
        }
        //Not条件
        if(currencySearch.getNot() != null && currencySearch.getNot().size() > 0){
            currencySearch.getNot().forEach(v -> {
                if(!StringUtils.isEmpty(v.getField())){
                    queryWrapper.ne(v.getField(),v.getValue());
                }
            });
        }
        //NotIn条件
        if(currencySearch.getNotIn() != null && currencySearch.getNotIn().size() > 0){
            currencySearch.getNotIn().forEach(v -> {
                queryWrapper.notIn(v.getField(),v.getValue());
            });
        }
        return queryWrapper;
    }

    protected UpdateWrapper<T> updateWrapper(CurrencySearch<T> currencySearch) throws ParseException {
        UpdateWrapper<T> updateWrapper = new UpdateWrapper<>();
        if(currencySearch == null){
            return null;
        }else{
            //修改条件in
            if(currencySearch.getIn() != null && currencySearch.getIn().size() > 0){
                currencySearch.getIn().forEach(v -> {
                    updateWrapper.in(v.getField(),v.getValue());
                });
            }

            //修改条件为字段等于
            if(currencySearch.getQuery() != null && currencySearch.getQuery().size() > 0){
                currencySearch.getQuery().forEach((v)->{
                    updateWrapper.eq(v.getField(),v.getValue());
                });
            }
            //修改条件like
            if(currencySearch.getLike() != null && currencySearch.getLike().size() > 0){
                currencySearch.getLike().forEach(v -> {
                    updateWrapper.like(v.getField(),v.getValue());
                });
            }
            //修改范围条件
            if(currencySearch.getBetween() != null && currencySearch.getBetween().size() > 0){
                currencySearch.getBetween().forEach(v -> {
                    if (v.getStart() != null){
                        updateWrapper.ge(v.getField(),v.getStart());
                    }
                    if (v.getEnd() != null){
                        updateWrapper.le(v.getField(),v.getEnd());
                    }
                });
            }
            //时间范围修改
            if(currencySearch.getDateParam() != null && !currencySearch.getDateParam().yesNull()){
                DateParam dateParam = currencySearch.getDateParam();
                if(!StringUtils.isEmpty(dateParam.getStartDate())){
                    updateWrapper.ge("create_time",dateParam.start());
                }
                if(!StringUtils.isEmpty(dateParam.getEndDate())){
                    updateWrapper.le("create_time",dateParam.end());
                }
            }
            //不等于修改
            if(currencySearch.getNot() != null && currencySearch.getNot().size() > 0){
                currencySearch.getNot().forEach(v -> {
                    if(!StringUtils.isEmpty(v.getField())){
                        updateWrapper.ne(v.getField(),v.getValue());
                    }
                });
            }
            //不等于多个条件
            if(currencySearch.getNotIn() != null && currencySearch.getNotIn().size() > 0){
                currencySearch.getNotIn().forEach(v -> {
                    updateWrapper.notIn(v.getField(),v.getValue());
                });
            }
        }
        return updateWrapper;
    }
}
