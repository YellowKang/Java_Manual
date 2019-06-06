package com.kang.shop.jpa.base;

import com.kang.shop.common.web.Code;
import com.kang.shop.common.web.Message;
import com.kang.shop.common.web.ResultVo;
import com.kang.shop.jpa.entity.Condition;
import com.kang.shop.jpa.entity.CurrencySearch;
import com.kang.shop.jpa.entity.InCondition;
import com.kang.shop.jpa.entity.PageRequest;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.xml.transform.Result;
import java.io.IOException;
import java.io.Serializable;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;


/**
 * @Author BigKang
 * @Date 2019/5/12 16:26
 * 通用Controller层，基于Jpa快速开发
 */
@NoRepositoryBean
public abstract class BaseJpaController<T extends BaseJpaEntity, PK extends Serializable, M extends BaseJpaService<T, PK>> {

    @Autowired
    protected M baseService;

    @ApiOperation(value = "添加实体类")
    @PostMapping(value = {"/"})
    public ResultVo create(@RequestBody T model) {
        try {
            Date date = new Date();
            model.setCreateTime(date);
            model.setUpdateTime(date);
            model = this.baseService.save(model);
            return ResultVo.result(model, Code.OK_CODE);
        } catch (Exception var4) {
            return ResultVo.result(Code.ERROR_CODE, Message.ERROR);
        }
    }

    @ApiOperation(value = "根据Id删除实体类")
    @DeleteMapping(value = {"/{id}"})
    public ResultVo delete(@PathVariable PK id) {
        try {
            this.baseService.delete(id);
            return ResultVo.result(Code.OK_CODE);
        } catch (Exception var3) {
            return ResultVo.result(Code.ERROR_CODE, Message.ERROR);
        }
    }


    @ApiOperation(value = "根据实体信息批量删除实体类")
    @DeleteMapping(value = {"/"})
    public ResultVo deleteInBatch(@RequestBody List<T> entities) throws IOException, IllegalAccessException, InstantiationException {
        try {
            this.baseService.deleteInBatch(entities);
            return ResultVo.result(Code.OK_CODE);
        } catch (Exception var3) {
            return ResultVo.result(Code.OK_CODE, Message.ERROR);
        }
    }

    @ApiOperation(value = "分页查询实体类")
    @PostMapping(value = {"/page"})
    public ResultVo page(@RequestBody PageRequest request) {
        Pageable pageable = request.pageable();
        return ResultVo.result(this.baseService.findAll(pageable), Code.OK_CODE);
    }

    @ApiOperation(value = "查询所有")
    @GetMapping(value = {"/getAll"})
    public ResultVo getAll() {
        return ResultVo.result(this.baseService.findAll(),Code.OK_CODE,Message.OK);
    }

    @ApiOperation(value = "根据Id获取实体类")
    @GetMapping(value = {"/{id}"})
    public ResultVo get(@PathVariable PK id) {
        try {
            Optional<T> optional = this.baseService.findById(id);
            return optional.isPresent() ? ResultVo.result(optional.get(), Code.OK_CODE) : ResultVo.result(Code.OK_CODE);
        } catch (Exception var3) {
            return ResultVo.result(Code.ERROR_CODE, Message.ERROR);
        }
    }

    @ApiOperation(value = "根据Id修改实体类")
    @PutMapping(value = {"/{id}"})
    public ResultVo update(@PathVariable PK id, @RequestBody T model) {
        try {
            model = this.baseService.update(id, model);
            return ResultVo.result(model, Code.OK_CODE);
        } catch (Exception var4) {
            return ResultVo.result(Code.ERROR_CODE, Message.ERROR);
        }
    }

    @ApiOperation(value = "多功能通用查询接口")
    @PostMapping(value = "/currencySearch")
    public ResultVo currencySearch(@RequestBody CurrencySearch<T> currencySearch) {
        Page<T> all = baseCurrencySearch(currencySearch);
        return ResultVo.result(all, Code.OK_CODE, Message.OK);
    }

    @ApiOperation(value = "多功能通用统计接口")
    @PostMapping(value = "/currencyCount")
    public ResultVo currencyCount(@RequestBody CurrencySearch<T> currencySearch) {
        Long count = baseCurrencyCount(currencySearch);
        return ResultVo.result(count, Code.OK_CODE, Message.OK);
    }

    /**
     * 多功能通用查询父接口
     * @param currencySearch
     * @return
     */
    protected  Page<T> baseCurrencySearch(CurrencySearch<T> currencySearch){
        Page<T> all;
        if (currencySearch == null || currencySearch.getLike() == null && currencySearch.getQuery() == null && currencySearch.getPageOrder() == null && currencySearch.getBetween() == null && currencySearch.getDateParam() == null && currencySearch.getNot() == null) {
            all = baseService.findAll(new PageRequest(1, 10).pageable());
        } else {
            if (currencySearch.getPageOrder() == null || currencySearch.getPageOrder().getLimit() == null || currencySearch.getPageOrder().getPage() == null) {
                currencySearch.setPageOrder(new PageRequest(1, 10));
            }
            Specification<T> specification;
                specification = ((root, query, builder) -> {
                    List<Predicate> predicateList = new ArrayList<Predicate>();
                    //查询条件eq
                    if(currencySearch.getQuery() != null && currencySearch.getQuery().size() > 0){
                        List<Condition> query1 = currencySearch.getQuery();
                        query1.forEach( v -> {
                            if(!StringUtils.isEmpty(v.getField()) && !"string".equals(v.getField())){
                                predicateList.add(builder.equal(root.get(v.getField()),v.getValue()));
                            }
                        });
                    }
                    //查询条件in
                    if (currencySearch.getIn() != null && currencySearch.getIn().size() > 0) {
                        List<InCondition> query1 = currencySearch.getIn();
                        query1.forEach(v -> {
                            if (v.getField() != null && v.getField() != "string") {
                                CriteriaBuilder.In<Object> in = builder.in(root.get(v.getField()));
                                v.getValue().forEach(z -> {
                                    in.value(z);
                                });
                                predicateList.add(builder.and(in));
                            }
                        });
                    }
                    //查询条件like
                    if (currencySearch.getLike() != null && currencySearch.getLike().size() > 0) {
                        currencySearch.getLike().forEach(v -> {
                            predicateList.add(builder.and(builder.like(root.get(v.getField()), "%" + (String) v.getValue() + "%")));
                        });
                    }
                    //查询条件not
                    if (currencySearch.getNot() != null && currencySearch.getNot().size() > 0) {
                        currencySearch.getNot().forEach(v -> {
                            predicateList.add(builder.notEqual(root.get(v.getField()), v.getValue()));
                        });
                    }
                    //查询条件between
                    if (currencySearch.getBetween() != null && currencySearch.getBetween().size() > 0) {
                        currencySearch.getBetween().forEach(v -> {
                            if (v.getStart() != null) {
                                predicateList.add(builder.ge(root.get(v.getField()), (Number) v.getStart()));
                            }
                            if (v.getEnd() != null) {
                                predicateList.add(builder.le(root.get(v.getField()), (Number) v.getEnd()));
                            }
                        });
                    }
                    //时间查询
                    if (currencySearch.getDateParam() != null) {
                        try {
                            predicateList.add(builder.between(root.get("createTime"), currencySearch.getDateParam().start(), currencySearch.getDateParam().end()));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    return builder.and(predicateList.toArray(new Predicate[predicateList.size()]));
                });
                all = baseService.findAll(specification, currencySearch.getPageOrder().pageable());
        }
        return all;
    }

    /**
     * base多功能通用统计接口
     * @param currencySearch
     * @return
     */
    protected Long baseCurrencyCount(CurrencySearch<T> currencySearch){
        Long count;
        if (currencySearch == null) {
            count = baseService.count();
        } else {
                Specification<T> specification = ((root, query, builder) -> {
                    List<Predicate> predicateList = new ArrayList<Predicate>();
                    //查询条件eq
                    if(currencySearch.getQuery() != null && currencySearch.getQuery().size() > 0){
                        List<Condition> query1 = currencySearch.getQuery();
                        query1.forEach( v -> {
                            if(!StringUtils.isEmpty(v.getField()) && !"string".equals(v.getField())){
                                predicateList.add(builder.equal(root.get(v.getField()),v.getValue()));
                            }
                        });
                    }
                    //循环in条件，将查询条件为in的参数作为查询条件
                    if (currencySearch.getIn() != null && currencySearch.getIn().size() > 0) {
                        List<InCondition> query1 = currencySearch.getIn();
                        query1.forEach(v -> {
                            if (v.getField() != null && v.getField() != "string") {
                                CriteriaBuilder.In<Object> in = builder.in(root.get(v.getField()));
                                v.getValue().forEach(z -> {
                                    in.value(z);
                                });
                                predicateList.add(builder.and(in));
                            }
                        });
                    }
                    //查询条件like
                    if (currencySearch.getLike() != null && currencySearch.getLike().size() > 0) {
                        currencySearch.getLike().forEach(v -> {
                            predicateList.add(builder.and(builder.like(root.get(v.getField()), "%" + (String) v.getValue() + "%")));
                        });
                    }
                    //查询条件not
                    if (currencySearch.getNot() != null && currencySearch.getNot().size() > 0) {
                        currencySearch.getNot().forEach(v -> {
                            predicateList.add(builder.notEqual(root.get(v.getField()), v.getValue()));

                        });
                    }
                    //查询条件between
                    if (currencySearch.getBetween() != null && currencySearch.getBetween().size() > 0) {
                        currencySearch.getBetween().forEach(v -> {
                            if (v.getStart() != null) {
                                predicateList.add(builder.ge(root.get(v.getField()), (Number) v.getStart()));
                            }
                            if (v.getEnd() != null) {
                                predicateList.add(builder.le(root.get(v.getField()), (Number) v.getEnd()));
                            }
                        });
                    }
                    //按照时间查询
                    if (currencySearch.getDateParam() != null) {
                        try {
                            predicateList.add(builder.between(root.get("createTime"), currencySearch.getDateParam().start(), currencySearch.getDateParam().end()));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    return builder.and(predicateList.toArray(new Predicate[predicateList.size()]));
                });
                count = baseService.count(specification);
        }
        return count;
    }
}
