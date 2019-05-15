package com.kang.demo;

import com.kang.demo.entity.Admin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;


//控制层，在boot中不需要配置mvc文件
@Controller
public class Hello {

//    @GetMapping 和@RequestMapping是一样的只是直接访问get请求
//    @ResponseBody 表示返回一串json格式的数据

//    将userService业务层从容其中获取到，这就是注入
    @Autowired
    private UserService userService;

//    访问localhost/addAdmin?id=1&name=康哥后面跟上参数，这是一个添加url
    @GetMapping(value = "/addAdmin")
    @ResponseBody
    public String createAdmin(Integer id,String name) {
//        调用业务层的添加方法
        return userService.createAdmin(id,name) == true ? "添加成功！" : "添加失败！";
    }


//     访问localhost/getAdmin?id=1后面跟上参数，这是一个单个查询url
    @GetMapping("/getAdmin")
    @ResponseBody
    public Admin selectAdmin(Integer id) {
        return userService.selectAdmin(id);
    }

//     访问localhost/updateAdmin?id=1&name=Big康 后面跟上参数，这是一个修改url
    @GetMapping("/updateAdmin")
    @ResponseBody
    public String updateAdmin(Integer id,String name){
        Admin admin = new Admin(id,name);
        return userService.updateAdmin(admin) == 1 ? "修改成功！" : "修改失败！";
    }


//    访问localhost/deleteAdmin?id=1 后面跟上参数，这是一个删除url
    @GetMapping("/deleteAdmin")
    @ResponseBody
    public String deleteAdmin(Integer id){
        return userService.deleteAdmin(id) == 1 ? "删除成功！" : "删除失败！";
    }

//    访问localhost/allAdmin  这是一个获取所有对象的url
    @GetMapping("/allAdmin")
    @ResponseBody
    public List<Admin> allAdmin(){
        return userService.getAll();
    }
}
