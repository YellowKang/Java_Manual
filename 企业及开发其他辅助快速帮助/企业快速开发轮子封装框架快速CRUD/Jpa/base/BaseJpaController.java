package com.kang.shop.jpa.base;

import com.kang.shop.common.web.Code;
import com.kang.shop.common.web.Message;
import com.kang.shop.common.web.ResultVo;
import com.kang.shop.jpa.entity.PageRequest;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@NoRepositoryBean
public abstract class BaseJpaController <T extends BaseJpaEntity, PK extends Serializable, M extends BaseJpaService<T, PK>> {

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
            return ResultVo.result(Code.ERROR_CODE,Message.ERROR);
        }
    }

    @ApiOperation(value = "根据Id删除实体类")
    @DeleteMapping(value = {"/{id}"})
    public ResultVo delete(@PathVariable PK id) {
        try {
            this.baseService.delete(id);
            return ResultVo.result(Code.OK_CODE);
        } catch (Exception var3) {
            return ResultVo.result(Code.ERROR_CODE,Message.ERROR);
        }
    }


    @ApiOperation(value = "根据实体信息批量删除实体类")
    @DeleteMapping(value = {"/"})
    public ResultVo deleteInBatch(@RequestBody List<T> entities) throws IOException, IllegalAccessException, InstantiationException {
        try {
            this.baseService.deleteInBatch(entities);
            return ResultVo.result(Code.OK_CODE);
        } catch (Exception var3) {
            return ResultVo.result(Code.OK_CODE,Message.ERROR);
        }
    }

    @ApiOperation(value = "分页查询实体类")
    @PostMapping(value = {"/page"})
    public ResultVo page(@RequestBody PageRequest request) {
        try {
            Pageable pageable = request.pageable();
            return ResultVo.result(this.baseService.findAll(pageable), Code.OK_CODE);
        } catch (Exception var8) {
            return ResultVo.result(Code.OK_CODE,Message.ERROR);
        }
    }

    @ApiOperation(value = "根据Id获取实体类")
    @GetMapping(value = {"/{id}"})
    public ResultVo get(@PathVariable PK id) {
        try {
            Optional<T> optional = this.baseService.findById(id);
            return optional.isPresent() ? ResultVo.result(optional.get(), Code.OK_CODE) : ResultVo.result(Code.OK_CODE);
        } catch (Exception var3) {
            return ResultVo.result(Code.ERROR_CODE,Message.ERROR);
        }
    }

    @ApiOperation(value = "根据Id修改实体类")
    @PutMapping(value = {"/{id}"})
    public ResultVo update(@PathVariable PK id, @RequestBody T model) {
        try {
                model = this.baseService.update(id,model);
                return ResultVo.result(model, Code.OK_CODE);
        } catch (Exception var4) {
            return ResultVo.result(Code.ERROR_CODE,Message.ERROR);
        }
    }
}
