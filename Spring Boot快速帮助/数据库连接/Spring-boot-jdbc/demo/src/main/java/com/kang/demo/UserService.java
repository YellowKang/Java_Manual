package com.kang.demo;

import com.kang.demo.entity.Admin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.util.List;

//表示这是一个service业务层需要将他注入spring容器
@Service
public class UserService {

//    注入jdbcTemplate获取到数据库操作对象
    @Autowired
    private JdbcTemplate jdbcTemplate;

//    添加方法，设置boolean成功返回true失败返回false
    public boolean createAdmin(Integer integer,String name){
        return  jdbcTemplate.update("insert into admin values(?,?)",integer,name) == 1;
    }

//    单个根据id查询，这里需要指定类型转换，返回一个Admin对象
    public Admin selectAdmin(Integer id) {
//        设置一个Mapper类型，用于接受结果的转换
        RowMapper<Admin> rowMapper=new BeanPropertyRowMapper<>(Admin.class);
        return jdbcTemplate.queryForObject("select  * from admin where id =?",rowMapper,id);
    }

//    根据id来删对象
    public int deleteAdmin(int id) {
        return jdbcTemplate.update("delete from admin where id = ?",id);
    }

//    根据对象修改对象
    public int updateAdmin(Admin admin){
        return jdbcTemplate.update("update admin set name = ? where id = ?",admin.getName(),admin.getId());
    }

//    返回所有信息
    public List<Admin> getAll(){
        RowMapper<Admin> rowMapper=new BeanPropertyRowMapper<>(Admin.class);
        return jdbcTemplate.query("select * from admin",rowMapper);
    }
}
