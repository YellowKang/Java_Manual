# 1、为什么需要注意？

	因为在我们启动项目的时候如果直接
	
		java -jar XXX.jar 	这样运行的话如果我们退出了远程服务器的bash窗口他就会自动关闭掉程序
	
	所以我们需要改动一下
	
		nohup java -jar Xxx.jar > 日志文件名.log （这里可以随便写到时候会生成一个文件在启动的目录下可以查看启动日志） &
	
		然后jar包启动起来之后就Ctrl加z返回然后直接退出远程的服务器就行了




    @Delete("delete from config where id=#{id}")
    @Options(useGeneratedKeys = true)
    Long deleteById(Long id);
    
    @Insert("insert into config(cron) values(#{cron})")
    @Options(useGeneratedKeys = true)
    Long save(Config config);
    
    //批量增加
    @InsertProvider(type = ConfigDaoProvider.class, method = "saveAll")
    void saveAll(@Param("list") Collection<Config> configs);
    
    public String saveAll(Map map) {
        List<Config> configs = (List<Config>) map.get("list");
        StringBuilder sb = new StringBuilder();
        sb.append("insert into config");
        sb.append("(cron)");
        sb.append("values");
        MessageFormat mf = new MessageFormat("#'{'list[{0}].cron'}'");
        for (int i = 0; i < configs.size(); i++) {
            sb.append("(");
            sb.append(mf.format(new Object[]{i}));
            sb.append(")");
    
            if (i < configs.size() - 1) {
                sb.append(",");
            }
        }
    
        return sb.toString();
    }




Mybatis动态注解主要是通过org.apache.ibatis.jdbc.SQL这个类实现，通过return new SQL(){}进行返回，同时要把
拼接好的SQL语句通过toString()转换成String类型，例子：
查询：
public class UserProvider {
	public String SelectWithSQL(final Map<String,Object> param){
		return new SQL(){
		   {
			SELECT("*");
			FROM("tb_user");
			if(param.get("tb_id")!=null){
				WHERE("tb_id=#{tb_id}");
			  }
			if(param.get("tb_name")!=null){
				WHERE("tb_name=#{tb_name}");
			  }
			if(param.get("tb_sex")!=null){
				WHERE("tb_sex=#{tb_sex}");
			  }
			if(param.get("tb_age")!=null){
				WHERE("tb_age=#{tb_age}");
			  }
	       }  
		}.toString();
	}
}
上述代码中首先通过new SQL(){}进行返回一个动态的SQL语句，之后通过{}进行动态SQL的拼接，其中SELECT()、FROM()
这些都是该类中的常用方法，通过传入参数取值进行判断后返回相应的where语句，最终合成一个完整的SQL语句。

插入：
public String InsertWithSQL(final User user){
		return new SQL(){
		   {
			INSERT_INTO("tb_user");
			if(user.getId()!=null){
				VALUES("tb_id","#{id}");
			  }
			if(user.getName()!=null){
				VALUES("tb_name","#{name}");
			  }
			if(user.getSex()!=null){
				VALUES("tb_sex","#{sex}");
			  }
			if(user.getAge()!=null){
				VALUES("tb_age","#{age}");
			  }
	       }  
		}.toString();
	}
插入语句的动态SQL注解是通过多次VALUES进行操作的，根据传入的属性进行判断后才进行插入。

更新：
public String UpdateWithSQL(final User user){
		return new SQL(){
		   {
			UPDATE("tb_user");
			if(user.getId()!=null){
				SET("tb_id=#{id}");
			  }
			if(user.getName()!=null){
				SET("tb_name=#{name}");
			  }
			if(user.getSex()!=null){
				SET("tb_sex=#{sex}");
			  }
			if(user.getAge()!=null){
				SET("tb_age=#{age}");
			  }
			WHERE("tb_id=#{id}");
	       }  
		}.toString();
	}
和插入没什么太大的区别，主要是通过SET元素进行修改。

删除：
public String DeleteWithSQL(final User user){
		return new SQL(){
		   {
			DELETE_FROM("tb_user");
			if(user.getId()!=null){
				WHERE("tb_id=#{id}");
			  }
			if(user.getName()!=null){
				WHERE("tb_name=#{name}");
			  }
			if(user.getSex()!=null){
				WHERE("tb_sex=#{sex}");
			  }
			if(user.getAge()!=null){
				WHERE("tb_age=#{age}");
			  }
			if(user.getId()!=null){
			    WHERE("tb_id=#{id}");
			}
	       }  
		}.toString();
	}