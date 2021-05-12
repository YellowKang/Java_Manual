# 首先Downloader项目

​		将项目Clone下来

```http
https://github.com/alibaba/Sentinel
```

# 然后打开项目切换版本分支到1.8（当前我自己使用版本）

​		打开sentinel-dashboard项目，就可以开始改造了。

![](https://blog-kang.oss-cn-beijing.aliyuncs.com/1619687042015.png)

# 首先开放Nacos注释

​		找到pom.xml，找到

```xml
        <!-- for Nacos rule publisher sample -->
        <dependency>
            <groupId>com.alibaba.csp</groupId>
            <artifactId>sentinel-datasource-nacos</artifactId>
            <scope>test</scope>
        </dependency>
```

​		修改为

```xml
        <!-- for Nacos rule publisher sample -->
        <dependency>
            <groupId>com.alibaba.csp</groupId>
            <artifactId>sentinel-datasource-nacos</artifactId>
        </dependency>
```

# 配置文件编写配置

```properties
# Nacos地址
nacos.server.ip=127.0.0.1
# Nacos命名空间（建议sentinel）
nacos.server.namespace=sentinel
# Nacos端口号
nacos.server.port=8848
# GROUP_ID自定义
nacos.server.groupId=DEFAULT_GROUP
# 是否开启Nacos存储
nacos.server.enable=true
```

# 新建配置类以及常量类

​		我们自定义Nacos地址以及开关，和推送的地址的文件，主要是自己进行Nacos的拓展，通过开关并且不影响原来代码。

​		新建包com.alibaba.csp.sentinel.dashboard.nacos

## 创建配置类存储配置

​		创建NacosProperties

```java
package com.alibaba.csp.sentinel.dashboard.nacos;

import com.alibaba.csp.sentinel.util.StringUtil;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/** @author zouwei */
@Component
@ConfigurationProperties(prefix = "nacos.server")
public class NacosProperties {
    private static final String NACOS_SERVER_ADDR = "NACOS_SERVER_ADDR";
    private static final String NACOS_GROUP_ID = "NACOS_GROUP_ID";
    private static final String NACOS_NAMESPACE = "NACOS_NAMESPACE";
    private static final String NACOS_PORT = "NACOS_PORT";
    private static final String NACOS_IP = "NACOS_IP";

    private String ip = "localhost";

    private String port = "8848";

    private String namespace;

    private String groupId = "DEFAULT_GROUP";

    private boolean enable = false;

    public boolean isOpen(){
        return enable;
    }

    public String getIp() {
        String nacosIp = getEnv(NACOS_IP);
        if (StringUtil.isNotBlank(nacosIp)) {
            return nacosIp;
        }
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPort() {
        String nacosPort = getEnv(NACOS_PORT);
        if (StringUtil.isNotBlank(nacosPort)) {
            return nacosPort;
        }
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getNamespace() {
        String nacosNamespace = getEnv(NACOS_NAMESPACE);
        if (StringUtil.isNotBlank(nacosNamespace)) {
            return nacosNamespace;
        }
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getGroupId() {
        String nacosGroupId = getEnv(NACOS_GROUP_ID);
        if (StringUtil.isNotBlank(nacosGroupId)) {
            return nacosGroupId;
        }
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getServerAddr() {
        String nacosServerAddr = getEnv(NACOS_SERVER_ADDR);
        if (StringUtil.isNotBlank(nacosServerAddr)) {
            return nacosServerAddr;
        }
        return this.getIp() + ":" + this.getPort();
    }

    private static String getEnv(String key) {
        return System.getenv(key);
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }
}

```

## 创建Config配置Bean

​		创建NacosConfig

```java
package com.alibaba.csp.sentinel.dashboard.nacos;

import com.alibaba.nacos.api.config.ConfigFactory;
import com.alibaba.nacos.api.config.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * @Author HuangKang
 * @Date 2021/4/29 下午5:10
 * @Summarize Nacos存储配置,如果没有开启则不装配ConfigServiceBean
 */
@Configuration
@ConditionalOnProperty(value = {"nacos.server.enable"}, havingValue = "true")
public class NacosConfig {

    private final NacosProperties nacosProperties;

    @Autowired
    public NacosConfig(NacosProperties nacosProperties) {
        this.nacosProperties = nacosProperties;
    }

    @Bean
    public ConfigService configService() throws Exception {
        Properties properties = new Properties();
        properties.put("serverAddr", nacosProperties.getServerAddr());
        String namespace = nacosProperties.getNamespace();
        if (namespace != null && !namespace.trim().isEmpty()) {
            properties.put("namespace", namespace);
        }
        ConfigService service = ConfigFactory.createConfigService(properties);
        return service;
    }

}

```

## 创建枚举

​		枚举用于对各种不同类型的路由存储，存储到那个文件后缀，存储规则 ${appName}-RuleTypeEnum.getSuffix()

```java
package com.alibaba.csp.sentinel.dashboard.nacos;


/**
 * @Author HuangKang
 * @Date 2021/4/29 下午5:17
 * @Summarize 路由类型枚举
 * 将新增的APP的规则，如果 ${spring.application.name}-flow-rules,文件写入Nacos，其他类型依旧如此
 */
public enum RuleTypeEnum {

    /**
     * 限流流控
     */
    FLOW("-flow-rules"),
    /**
     * 服务降级熔断
     */
    DEGRADE("-degrade-rules"),
    /**
     * 参数流控限流
     */
    PARAM("-param-rules"),
    /**
     * 认证规则
     */
    AUTHORITY("-authority-rules"),
    /**
     * 系统路由
     */
    SYSTEM("-system-rules"),

    /**
     * API接口路由
     */
    API("-api-rules"),

    /**
     * 网关路由
     */
    GATEWAY("-gateway-rules");

    RuleTypeEnum(String suffix) {
        this.suffix = suffix;
    }

    private String suffix;

    public String getSuffix() {
        return suffix;
    }
}

```

## 创建NacosUtil

```java
package com.alibaba.csp.sentinel.dashboard.nacos;

import com.alibaba.csp.sentinel.dashboard.client.SentinelApiClient;
import com.alibaba.csp.sentinel.dashboard.discovery.AppManagement;
import com.alibaba.csp.sentinel.dashboard.discovery.MachineInfo;
import com.alibaba.csp.sentinel.util.StringUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author HuangKang
 * @Date 2021/4/29 下午6:00
 * @Summarize Nacos工具类用于获取数据，以及生成ID以及推送数据到服务
 */
@Component
public class NacosUtil {

    private final NacosProperties nacosProperties;

    @Autowired(required = false)
    private ConfigService configService;

    @Autowired
    private SentinelApiClient sentinelApiClient;

    @Autowired
    private AppManagement appManagement;

    /**
     * NEXT下一个ID缓存
     */
    private static final ConcurrentHashMap<String, Long> NEXT_ID_CACHE = new ConcurrentHashMap<>();

    @Autowired
    public NacosUtil(NacosProperties nacosProperties) {
        this.nacosProperties = nacosProperties;
    }

    /**
     * 推送数据到服务节点中
     *
     * @param appName  SpringCloud注册的项目名
     * @param ruleType 路由类型
     * @param listData
     * @param <T>
     */
    public <T> void publishRules(String appName, RuleTypeEnum ruleType, List listData) {
        if (StringUtil.isBlank(appName)) {
            return;
        }
        if (appName == null) {
            return;
        }
        Set<MachineInfo> set = appManagement.getDetailApp(appName).getMachines();

        for (MachineInfo machine : set) {
            if (!machine.isHealthy()) {
                continue;
            }
            // 根据不同类型数据推送到节点中
            if (RuleTypeEnum.FLOW.equals(ruleType)) {
                sentinelApiClient.setFlowRuleOfMachine(appName, machine.getIp(), machine.getPort(), listData);
            }else if (RuleTypeEnum.DEGRADE.equals(ruleType)) {
                sentinelApiClient.setDegradeRuleOfMachine(appName, machine.getIp(), machine.getPort(), listData);
            }else if (RuleTypeEnum.PARAM.equals(ruleType)) {
                sentinelApiClient.setParamFlowRuleOfMachine(appName, machine.getIp(), machine.getPort(), listData);
            }else if (RuleTypeEnum.AUTHORITY.equals(ruleType)) {
                sentinelApiClient.setAuthorityRuleOfMachine(appName, machine.getIp(), machine.getPort(), listData);
            }else if (RuleTypeEnum.SYSTEM.equals(ruleType)) {
                sentinelApiClient.setSystemRuleOfMachine(appName, machine.getIp(), machine.getPort(), listData);
            }
        }
    }


    /**
     * 推送本地路由数据到Nacos
     *
     * @param appName  SpringCloud注册的项目名
     * @param ruleType 路由类型
     * @param listData
     * @param <T>
     */
    public <T> void publisNacos(String appName, RuleTypeEnum ruleType, List<T> listData) {
        try {
            configService.publishConfig(appName + ruleType.getSuffix(),
                    nacosProperties.getGroupId(), encoder(listData));
        } catch (NacosException e) {
            System.out.println("ERROR--推送Nacos同步数据失败!!!");
            e.printStackTrace();
        }
    }

    /**
     * 从Nacos查询数据
     * 根据APP名称，加上路由类型，以及返回数据集合，从Nacos获取数据并且返回对象集合
     *
     * @param appName  SpringCloud注册的项目名
     * @param ruleType 路由类型
     * @param classAs  实体类class
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> List<T> getRules(String appName, RuleTypeEnum ruleType, Class<T> classAs) throws Exception {
        // 从Nacos拉取数据，项目名+类型后缀
        String rules = configService.getConfig(appName + ruleType.getSuffix(),
                nacosProperties.getGroupId(), 3000);
        if (StringUtil.isEmpty(rules)) {
            return new ArrayList<>();
        }
        return decoder(rules, classAs);
    }

    /**
     * 数据序列化，将对象集合序列化为Json字符串
     *
     * @param listData 原数据
     * @param <T>
     * @return
     */
    public <T> String encoder(List<T> listData) {
        return JSON.toJSONString(listData);
    }

    /**
     * 数据序反列化，将Json字符串反序列化为对象集合
     *
     * @param jsonData Json字符串，从Nacos拉取
     * @param classAs  返回的对象类型
     * @param <T>
     * @return
     */
    public <T> List<T> decoder(String jsonData, Class<T> classAs) {
        return JSON.parseArray(jsonData, classAs);
    }

    /**
     * 是否开启Nacos
     *
     * @return
     */
    public boolean isOpen() {
        return nacosProperties.isOpen();
    }


    /**
     * 同步本地NextID到本地
     * @param ruleType
     */
    public void syncNextId(RuleTypeEnum ruleType){
        // 从缓存中获取nextId
        Long nextId = NEXT_ID_CACHE.get(ruleType.name());
        try {
            if(nextId == null){
                nextId = nextId(ruleType,false);
            }
            configService.publishConfig("a-custom" + ruleType.getSuffix() + "-next-id",
                    nacosProperties.getGroupId(), new Long(nextId).toString());
        } catch (NacosException e) {
            e.printStackTrace();
        }
    }

    /**
     * ID策略存储Nacos
     *
     * @return
     */
    public Long nextId(RuleTypeEnum ruleType,boolean saveAll) {
        synchronized (ruleType) {
            try {
                // 从缓存中获取nextId
                Long nextId = NEXT_ID_CACHE.get(ruleType.name());
                if (nextId == null) {
                    String nextIdStr = configService.getConfig("a-custom" + ruleType.getSuffix() + "-next-id",
                            nacosProperties.getGroupId(), 3000);
                    // 如果Nacos中也没有，则初始化
                    if (nextIdStr == null || nextIdStr.trim().isEmpty()) {
                        nextId = 1L;
                    }
                    // 如果有则进行序列化
                    else {
                        nextId = Long.valueOf(nextIdStr);
                    }
                }
                nextId += 1;
                NEXT_ID_CACHE.put(ruleType.name(), nextId);
                // 批量添加时不全局添加ID
                if(!saveAll){
                    // 更新NacosID
                    configService.publishConfig("a-custom" + ruleType.getSuffix() + "-next-id",
                            nacosProperties.getGroupId(), new Long(nextId).toString());
                }
                return nextId;
            } catch (NacosException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}

```

## 查看目录

​		创建后完成查看目录如下

![](https://blog-kang.oss-cn-beijing.aliyuncs.com/1619763102518.png)

# 修改sentinel源码

## 修改思路

​		修改源码我们需要参考方式，目前网上实现方式多种，我们采用直接修改sentinel数据存储，在添加删除的时候将数据保存到Nacos中，修改类为如下：

## ！！！！！！！！注意，修改后只根据APP进行数据控制，无法根据单节点ip以及端口进行限制，会同步所有节点

```java
// 所有数据存储的抽象类，流控数据以及降级等等都继承与他
InMemoryRuleRepositoryAdapter

// 下面都是实现类只做小部分修改即可
InMemAuthorityRuleStore
InMemDegradeRuleStore
InMemFlowRuleStore
InMemParamFlowRuleStore
InMemSystemRuleStore
InMemApiDefinitionStore
InMemGatewayFlowRuleStore
```

## 修改InMemoryRuleRepositoryAdapter类

```java
package com.alibaba.csp.sentinel.dashboard.repository.rule;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.RuleEntity;
import com.alibaba.csp.sentinel.dashboard.discovery.MachineInfo;
import com.alibaba.csp.sentinel.dashboard.nacos.NacosUtil;
import com.alibaba.csp.sentinel.dashboard.nacos.RuleTypeEnum;
import com.alibaba.csp.sentinel.util.AssertUtil;
import com.alibaba.fastjson.JSON;

/**
 * @author leyou
 */
public abstract class InMemoryRuleRepositoryAdapter<T extends RuleEntity> implements RuleRepository<T, Long> {

    /**
     * {@code <machine, <id, rule>>}
     */
    private Map<MachineInfo, Map<Long, T>> machineRules = new ConcurrentHashMap<>(16);
    private Map<Long, T> allRules = new ConcurrentHashMap<>(16);

    private Map<String, Map<Long, T>> appRules = new ConcurrentHashMap<>(16);

    private static final int MAX_RULES_SIZE = 10000;

    // 获取当前T的class
    ParameterizedType type = (ParameterizedType) this.getClass().getGenericSuperclass();
    Class<T> tClass = (Class) type.getActualTypeArguments()[0];

    /**
     * Nacos工具类子类通过构造
     */
    private NacosUtil nacosUtil;

    /**
     * 路由类型枚举
     */
    private RuleTypeEnum ruleTypeEnum;

    private static AtomicLong CUSTOM_ID = new AtomicLong(0);

    public InMemoryRuleRepositoryAdapter(NacosUtil nacosUtil, RuleTypeEnum ruleTypeEnum) {
        this.nacosUtil = nacosUtil;
        this.ruleTypeEnum = ruleTypeEnum;
    }

    /**
     * 更新数据到Nacos中，并且推送到服务中
     *
     * @param appName
     * @param
     */
    public void updateAppByNacos(String appName) {
        if (nacosUtil.isOpen()) {
            // 查询出新增的数据对应的APP的所有数据，存储到Nacos
            List<T> allByApp = findAllByApp(appName);
            System.out.println("发送数据到Nacos：" + JSON.toJSONString(allByApp));
            nacosUtil.publisNacos(appName, ruleTypeEnum, allByApp);
            nacosUtil.publishRules(appName, ruleTypeEnum, allByApp);
        }

    }

    public T saveAll(T entity, boolean saveAll) {
        // 启用Nacos则进行NacosID生成，没有启用则使用默认
        if (entity.getId() == null) {
            if (nacosUtil.isOpen()) {
                entity.setId(nacosUtil.nextId(this.ruleTypeEnum, saveAll));
            } else {
                entity.setId(nextId());
            }
        }
        T processedEntity = preProcess(entity);
        if (processedEntity != null) {
            allRules.put(processedEntity.getId(), processedEntity);
            machineRules.computeIfAbsent(MachineInfo.of(processedEntity.getApp(), processedEntity.getIp(),
                    processedEntity.getPort()), e -> new ConcurrentHashMap<>(32))
                    .put(processedEntity.getId(), processedEntity);
            appRules.computeIfAbsent(processedEntity.getApp(), v -> new ConcurrentHashMap<>(32))
                    .put(processedEntity.getId(), processedEntity);
            // 同步数据到Nacos
            updateAppByNacos(entity.getApp());
        }

        return processedEntity;
    }

    @Override
    public T save(T entity) {
        return saveAll(entity, false);
    }

    @Override
    public List<T> saveAll(List<T> rules) {
        // TODO: check here.
        allRules.clear();
        machineRules.clear();
        appRules.clear();

        if (rules == null) {
            return null;
        }
        List<T> savedRules = new ArrayList<>(rules.size());
        for (T rule : rules) {
            savedRules.add(saveAll(rule, true));
        }
        nacosUtil.syncNextId(this.ruleTypeEnum);
        return savedRules;
    }

    @Override
    public T delete(Long id) {
        T entity = allRules.remove(id);
        if (entity != null) {
            if (appRules.get(entity.getApp()) != null) {
                appRules.get(entity.getApp()).remove(id);
            }
            machineRules.get(MachineInfo.of(entity.getApp(), entity.getIp(), entity.getPort())).remove(id);
            // 同步数据到Nacos
            updateAppByNacos(entity.getApp());
        }
        return entity;
    }

    @Override
    public T findById(Long id) {
        return allRules.get(id);
    }

    @Override
    public List<T> findAllByMachine(MachineInfo machineInfo) {
        Map<Long, T> entities = machineRules.get(machineInfo);
        if (entities == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(entities.values());
    }

    @Override
    public List<T> findAllByApp(String appName) {
        AssertUtil.notEmpty(appName, "appName cannot be empty");
        Map<Long, T> entities = appRules.get(appName);
        List<T> listData = new ArrayList<>();
        if (entities == null) {
            if (nacosUtil.isOpen()) {
                try {
                    List<T> rules = nacosUtil.getRules(appName, this.ruleTypeEnum, tClass);
                    if (rules != null && rules.size() > 0) {
                        for (T rule : rules) {
                            listData.add(rule);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            listData = new ArrayList<>(entities.values());
        }
        return listData;
    }

    public void clearAll() {
        allRules.clear();
        machineRules.clear();
        appRules.clear();
    }

    protected T preProcess(T entity) {
        return entity;
    }

    /**
     * Get next unused id.
     *
     * @return next unused id
     */
    abstract protected long nextId();
}

```

## 改造存储类

```java
// 下面都是实现类只做小部分修改即可
InMemAuthorityRuleStore
InMemDegradeRuleStore
InMemFlowRuleStore
InMemParamFlowRuleStore
InMemSystemRuleStore
InMemApiDefinitionStore
InMemGatewayFlowRuleStore
```

## InMemAuthorityRuleStore

​		修改抽象类即可，只需要少量修改构造方法集合，下面方法只粘贴构造方法

```java
package com.alibaba.csp.sentinel.dashboard.repository.rule;

import java.util.concurrent.atomic.AtomicLong;

import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.AuthorityRuleEntity;

import com.alibaba.csp.sentinel.dashboard.nacos.NacosUtil;
import com.alibaba.csp.sentinel.dashboard.nacos.RuleTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * In-memory storage for authority rules.
 *
 * @author Eric Zhao
 * @since 0.2.1
 */
@Component
public class InMemAuthorityRuleStore extends InMemoryRuleRepositoryAdapter<AuthorityRuleEntity> {

    private static AtomicLong ids = new AtomicLong(0);

    @Autowired
    public InMemAuthorityRuleStore(NacosUtil nacosUtil) {
        super(nacosUtil, RuleTypeEnum.AUTHORITY);
    }

    @Override
    protected long nextId() {
        return ids.incrementAndGet();
    }
}
```

## InMemDegradeRuleStore

```java
    @Autowired
    public InMemDegradeRuleStore(NacosUtil nacosUtil) {
        super(nacosUtil, RuleTypeEnum.DEGRADE);
    }
```

## InMemFlowRuleStore

```java
    @Autowired
    public InMemFlowRuleStore(NacosUtil nacosUtil) {
        super(nacosUtil, RuleTypeEnum.FLOW);
    }
```

## InMemParamFlowRuleStore

```java
    @Autowired
    public InMemParamFlowRuleStore(NacosUtil nacosUtil) {
        super(nacosUtil, RuleTypeEnum.PARAM);
    }
```

## InMemSystemRuleStore

```java
    @Autowired
    public InMemSystemRuleStore(NacosUtil nacosUtil) {
        super(nacosUtil, RuleTypeEnum.SYSTEM);
    }
```

## InMemApiDefinitionStore

```java
    @Autowired
    public InMemApiDefinitionStore(NacosUtil nacosUtil) {
        super(nacosUtil, RuleTypeEnum.API);
    }
```

## InMemGatewayFlowRuleStore

```java
    @Autowired
    public InMemGatewayFlowRuleStore(NacosUtil nacosUtil) {
        super(nacosUtil, RuleTypeEnum.GATEWAY);
    }
```

## 修改HTML页面

​		找到如下html     src/main/webapp/resources/app/scripts/directives/sidebar/sidebar.html

​		将下面的数据

```html
       <li ui-sref-active="active" ng-if="!entry.isGateway">
            <a ui-sref="dashboard.flowV1({app: entry.app})">
              <i class="glyphicon glyphicon-filter"></i>&nbsp;&nbsp;流控规则</a>
       </li>
```

​		修改为

```html
          <li ui-sref-active="active" ng-if="!entry.isGateway">
            <a ui-sref="dashboard.flow({app: entry.app})">
              <i class="glyphicon glyphicon-filter"></i>&nbsp;&nbsp;流控规则V2</a>
          </li>
```

# 修改Controller

​		只针对修改几种存储数据的控制器

## FlowControllerV1

```java
/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.csp.sentinel.dashboard.controller;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import com.alibaba.csp.sentinel.dashboard.auth.AuthAction;
import com.alibaba.csp.sentinel.dashboard.auth.AuthService.PrivilegeType;
import com.alibaba.csp.sentinel.util.StringUtil;

import com.alibaba.csp.sentinel.dashboard.client.SentinelApiClient;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.FlowRuleEntity;
import com.alibaba.csp.sentinel.dashboard.discovery.MachineInfo;
import com.alibaba.csp.sentinel.dashboard.domain.Result;
import com.alibaba.csp.sentinel.dashboard.repository.rule.InMemoryRuleRepositoryAdapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Flow rule controller.
 *
 * @author leyou
 * @author Eric Zhao
 */
@RestController
@RequestMapping(value = "/v1/flow")
public class FlowControllerV1 {

    private final Logger logger = LoggerFactory.getLogger(FlowControllerV1.class);

    @Autowired
    private InMemoryRuleRepositoryAdapter<FlowRuleEntity> repository;

    @Autowired
    private SentinelApiClient sentinelApiClient;

    @GetMapping("/rules")
    @AuthAction(PrivilegeType.READ_RULE)
    public Result<List<FlowRuleEntity>> apiQueryMachineRules(@RequestParam String app,
                                                             @RequestParam String ip,
                                                             @RequestParam Integer port) {
        if (StringUtil.isEmpty(app)) {
            return Result.ofFail(-1, "app can't be null or empty");
        }
        try {
            List<FlowRuleEntity> rules = repository.findAllByApp(app);
            return Result.ofSuccess(rules);
        } catch (Throwable throwable) {
            logger.error("Error when querying flow rules", throwable);
            return Result.ofThrowable(-1, throwable);
        }
    }

    private <R> Result<R> checkEntityInternal(FlowRuleEntity entity) {
        if (StringUtil.isBlank(entity.getApp())) {
            return Result.ofFail(-1, "app can't be null or empty");
        }
        if (StringUtil.isBlank(entity.getIp())) {
            return Result.ofFail(-1, "ip can't be null or empty");
        }
        if (entity.getPort() == null) {
            return Result.ofFail(-1, "port can't be null");
        }
        if (StringUtil.isBlank(entity.getLimitApp())) {
            return Result.ofFail(-1, "limitApp can't be null or empty");
        }
        if (StringUtil.isBlank(entity.getResource())) {
            return Result.ofFail(-1, "resource can't be null or empty");
        }
        if (entity.getGrade() == null) {
            return Result.ofFail(-1, "grade can't be null");
        }
        if (entity.getGrade() != 0 && entity.getGrade() != 1) {
            return Result.ofFail(-1, "grade must be 0 or 1, but " + entity.getGrade() + " got");
        }
        if (entity.getCount() == null || entity.getCount() < 0) {
            return Result.ofFail(-1, "count should be at lease zero");
        }
        if (entity.getStrategy() == null) {
            return Result.ofFail(-1, "strategy can't be null");
        }
        if (entity.getStrategy() != 0 && StringUtil.isBlank(entity.getRefResource())) {
            return Result.ofFail(-1, "refResource can't be null or empty when strategy!=0");
        }
        if (entity.getControlBehavior() == null) {
            return Result.ofFail(-1, "controlBehavior can't be null");
        }
        int controlBehavior = entity.getControlBehavior();
        if (controlBehavior == 1 && entity.getWarmUpPeriodSec() == null) {
            return Result.ofFail(-1, "warmUpPeriodSec can't be null when controlBehavior==1");
        }
        if (controlBehavior == 2 && entity.getMaxQueueingTimeMs() == null) {
            return Result.ofFail(-1, "maxQueueingTimeMs can't be null when controlBehavior==2");
        }
        if (entity.isClusterMode() && entity.getClusterConfig() == null) {
            return Result.ofFail(-1, "cluster config should be valid");
        }
        return null;
    }

    @PostMapping("/rule")
    @AuthAction(PrivilegeType.WRITE_RULE)
    public Result<FlowRuleEntity> apiAddFlowRule(@RequestBody FlowRuleEntity entity) {
        Result<FlowRuleEntity> checkResult = checkEntityInternal(entity);
        if (checkResult != null) {
            return checkResult;
        }
        entity.setId(null);
        Date date = new Date();
        entity.setGmtCreate(date);
        entity.setGmtModified(date);
        entity.setLimitApp(entity.getLimitApp().trim());
        entity.setResource(entity.getResource().trim());
        try {
            entity = repository.save(entity);
            return Result.ofSuccess(entity);
        } catch (Throwable t) {
            Throwable e = t instanceof ExecutionException ? t.getCause() : t;
            logger.error("Failed to add new flow rule, app={}, ip={}", entity.getApp(), entity.getIp(), e);
            return Result.ofFail(-1, e.getMessage());
        }
    }

    @PutMapping("/save.json")
    @AuthAction(PrivilegeType.WRITE_RULE)
    public Result<FlowRuleEntity> apiUpdateFlowRule(Long id, String app,
                                                  String limitApp, String resource, Integer grade,
                                                  Double count, Integer strategy, String refResource,
                                                  Integer controlBehavior, Integer warmUpPeriodSec,
                                                  Integer maxQueueingTimeMs) {
        if (id == null) {
            return Result.ofFail(-1, "id can't be null");
        }
        FlowRuleEntity entity = repository.findById(id);
        if (entity == null) {
            return Result.ofFail(-1, "id " + id + " dose not exist");
        }
        if (StringUtil.isNotBlank(app)) {
            entity.setApp(app.trim());
        }
        if (StringUtil.isNotBlank(limitApp)) {
            entity.setLimitApp(limitApp.trim());
        }
        if (StringUtil.isNotBlank(resource)) {
            entity.setResource(resource.trim());
        }
        if (grade != null) {
            if (grade != 0 && grade != 1) {
                return Result.ofFail(-1, "grade must be 0 or 1, but " + grade + " got");
            }
            entity.setGrade(grade);
        }
        if (count != null) {
            entity.setCount(count);
        }
        if (strategy != null) {
            if (strategy != 0 && strategy != 1 && strategy != 2) {
                return Result.ofFail(-1, "strategy must be in [0, 1, 2], but " + strategy + " got");
            }
            entity.setStrategy(strategy);
            if (strategy != 0) {
                if (StringUtil.isBlank(refResource)) {
                    return Result.ofFail(-1, "refResource can't be null or empty when strategy!=0");
                }
                entity.setRefResource(refResource.trim());
            }
        }
        if (controlBehavior != null) {
            if (controlBehavior != 0 && controlBehavior != 1 && controlBehavior != 2) {
                return Result.ofFail(-1, "controlBehavior must be in [0, 1, 2], but " + controlBehavior + " got");
            }
            if (controlBehavior == 1 && warmUpPeriodSec == null) {
                return Result.ofFail(-1, "warmUpPeriodSec can't be null when controlBehavior==1");
            }
            if (controlBehavior == 2 && maxQueueingTimeMs == null) {
                return Result.ofFail(-1, "maxQueueingTimeMs can't be null when controlBehavior==2");
            }
            entity.setControlBehavior(controlBehavior);
            if (warmUpPeriodSec != null) {
                entity.setWarmUpPeriodSec(warmUpPeriodSec);
            }
            if (maxQueueingTimeMs != null) {
                entity.setMaxQueueingTimeMs(maxQueueingTimeMs);
            }
        }
        Date date = new Date();
        entity.setGmtModified(date);
        try {
            entity = repository.save(entity);
            if (entity == null) {
                return Result.ofFail(-1, "save entity fail: null");
            }
            return Result.ofSuccess(entity);
        } catch (Throwable t) {
            Throwable e = t instanceof ExecutionException ? t.getCause() : t;
            logger.error("Error when updating flow rules, app={}, ip={}, ruleId={}", entity.getApp(),
                entity.getIp(), id, e);
            return Result.ofFail(-1, e.getMessage());
        }
    }

    @DeleteMapping("/delete.json")
    @AuthAction(PrivilegeType.WRITE_RULE)
    public Result<Long> apiDeleteFlowRule(Long id) {

        if (id == null) {
            return Result.ofFail(-1, "id can't be null");
        }
        FlowRuleEntity oldEntity = repository.findById(id);
        if (oldEntity == null) {
            return Result.ofSuccess(null);
        }

        try {
            repository.delete(id);
        } catch (Exception e) {
            return Result.ofFail(-1, e.getMessage());
        }
        try {
            return Result.ofSuccess(id);
        } catch (Throwable t) {
            Throwable e = t instanceof ExecutionException ? t.getCause() : t;
            logger.error("Error when deleting flow rules, app={}, ip={}, id={}", oldEntity.getApp(),
                oldEntity.getIp(), id, e);
            return Result.ofFail(-1, e.getMessage());
        }
    }

    private CompletableFuture<Void> publishRules(String app, String ip, Integer port) {
        List<FlowRuleEntity> rules = repository.findAllByMachine(MachineInfo.of(app, ip, port));
        return sentinelApiClient.setFlowRuleOfMachineAsync(app, ip, port, rules);
    }
}

```

## FlowControllerV2

```java
/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.csp.sentinel.dashboard.controller.v2;

import java.util.Date;
import java.util.List;

import com.alibaba.csp.sentinel.dashboard.auth.AuthAction;
import com.alibaba.csp.sentinel.dashboard.auth.AuthService;
import com.alibaba.csp.sentinel.dashboard.auth.AuthService.PrivilegeType;
import com.alibaba.csp.sentinel.util.StringUtil;

import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.FlowRuleEntity;
import com.alibaba.csp.sentinel.dashboard.repository.rule.InMemoryRuleRepositoryAdapter;
import com.alibaba.csp.sentinel.dashboard.rule.DynamicRuleProvider;
import com.alibaba.csp.sentinel.dashboard.rule.DynamicRulePublisher;
import com.alibaba.csp.sentinel.dashboard.domain.Result;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Flow rule controller (v2).
 *
 * @author Eric Zhao
 * @since 1.4.0
 */
@RestController
@RequestMapping(value = "/v2/flow")
public class FlowControllerV2 {

    private final Logger logger = LoggerFactory.getLogger(FlowControllerV2.class);

    @Autowired
    private InMemoryRuleRepositoryAdapter<FlowRuleEntity> repository;

    @Autowired
    @Qualifier("flowRuleDefaultProvider")
    private DynamicRuleProvider<List<FlowRuleEntity>> ruleProvider;
    @Autowired
    @Qualifier("flowRuleDefaultPublisher")
    private DynamicRulePublisher<List<FlowRuleEntity>> rulePublisher;

    @GetMapping("/rules")
    @AuthAction(PrivilegeType.READ_RULE)
    public Result<List<FlowRuleEntity>> apiQueryMachineRules(@RequestParam String app) {

        if (StringUtil.isEmpty(app)) {
            return Result.ofFail(-1, "app can't be null or empty");
        }
        try {
            List<FlowRuleEntity> rules = repository.findAllByApp(app);
            if (rules != null && !rules.isEmpty()) {
                for (FlowRuleEntity entity : rules) {
                    entity.setApp(app);
                    if (entity.getClusterConfig() != null && entity.getClusterConfig().getFlowId() != null) {
                        entity.setId(entity.getClusterConfig().getFlowId());
                    }
                }
            }
            return Result.ofSuccess(rules);
        } catch (Throwable throwable) {
            logger.error("Error when querying flow rules", throwable);
            return Result.ofThrowable(-1, throwable);
        }
    }

    private <R> Result<R> checkEntityInternal(FlowRuleEntity entity) {
        if (entity == null) {
            return Result.ofFail(-1, "invalid body");
        }
        if (StringUtil.isBlank(entity.getApp())) {
            return Result.ofFail(-1, "app can't be null or empty");
        }
        if (StringUtil.isBlank(entity.getLimitApp())) {
            return Result.ofFail(-1, "limitApp can't be null or empty");
        }
        if (StringUtil.isBlank(entity.getResource())) {
            return Result.ofFail(-1, "resource can't be null or empty");
        }
        if (entity.getGrade() == null) {
            return Result.ofFail(-1, "grade can't be null");
        }
        if (entity.getGrade() != 0 && entity.getGrade() != 1) {
            return Result.ofFail(-1, "grade must be 0 or 1, but " + entity.getGrade() + " got");
        }
        if (entity.getCount() == null || entity.getCount() < 0) {
            return Result.ofFail(-1, "count should be at lease zero");
        }
        if (entity.getStrategy() == null) {
            return Result.ofFail(-1, "strategy can't be null");
        }
        if (entity.getStrategy() != 0 && StringUtil.isBlank(entity.getRefResource())) {
            return Result.ofFail(-1, "refResource can't be null or empty when strategy!=0");
        }
        if (entity.getControlBehavior() == null) {
            return Result.ofFail(-1, "controlBehavior can't be null");
        }
        int controlBehavior = entity.getControlBehavior();
        if (controlBehavior == 1 && entity.getWarmUpPeriodSec() == null) {
            return Result.ofFail(-1, "warmUpPeriodSec can't be null when controlBehavior==1");
        }
        if (controlBehavior == 2 && entity.getMaxQueueingTimeMs() == null) {
            return Result.ofFail(-1, "maxQueueingTimeMs can't be null when controlBehavior==2");
        }
        if (entity.isClusterMode() && entity.getClusterConfig() == null) {
            return Result.ofFail(-1, "cluster config should be valid");
        }
        return null;
    }

    @PostMapping("/rule")
    @AuthAction(value = AuthService.PrivilegeType.WRITE_RULE)
    public Result<FlowRuleEntity> apiAddFlowRule(@RequestBody FlowRuleEntity entity) {

        Result<FlowRuleEntity> checkResult = checkEntityInternal(entity);
        if (checkResult != null) {
            return checkResult;
        }
        entity.setId(null);
        Date date = new Date();
        entity.setGmtCreate(date);
        entity.setGmtModified(date);
        entity.setLimitApp(entity.getLimitApp().trim());
        entity.setResource(entity.getResource().trim());
        try {
            entity = repository.save(entity);
        } catch (Throwable throwable) {
            logger.error("Failed to add flow rule", throwable);
            return Result.ofThrowable(-1, throwable);
        }
        return Result.ofSuccess(entity);
    }

    @PutMapping("/rule/{id}")
    @AuthAction(AuthService.PrivilegeType.WRITE_RULE)

    public Result<FlowRuleEntity> apiUpdateFlowRule(@PathVariable("id") Long id,
                                                    @RequestBody FlowRuleEntity entity) {
        if (id == null || id <= 0) {
            return Result.ofFail(-1, "Invalid id");
        }
        FlowRuleEntity oldEntity = repository.findById(id);
        if (oldEntity == null) {
            return Result.ofFail(-1, "id " + id + " does not exist");
        }
        if (entity == null) {
            return Result.ofFail(-1, "invalid body");
        }

        entity.setApp(oldEntity.getApp());
        entity.setIp(oldEntity.getIp());
        entity.setPort(oldEntity.getPort());
        Result<FlowRuleEntity> checkResult = checkEntityInternal(entity);
        if (checkResult != null) {
            return checkResult;
        }

        entity.setId(id);
        Date date = new Date();
        entity.setGmtCreate(oldEntity.getGmtCreate());
        entity.setGmtModified(date);
        try {
            entity = repository.save(entity);
            if (entity == null) {
                return Result.ofFail(-1, "save entity fail");
            }
        } catch (Throwable throwable) {
            logger.error("Failed to update flow rule", throwable);
            return Result.ofThrowable(-1, throwable);
        }
        return Result.ofSuccess(entity);
    }

    @DeleteMapping("/rule/{id}")
    @AuthAction(PrivilegeType.DELETE_RULE)
    public Result<Long> apiDeleteRule(@PathVariable("id") Long id) {
        if (id == null || id <= 0) {
            return Result.ofFail(-1, "Invalid id");
        }
        FlowRuleEntity oldEntity = repository.findById(id);
        if (oldEntity == null) {
            return Result.ofSuccess(null);
        }

        try {
            repository.delete(id);
        } catch (Exception e) {
            return Result.ofFail(-1, e.getMessage());
        }
        return Result.ofSuccess(id);
    }

    private void publishRules(/*@NonNull*/ String app) throws Exception {
        List<FlowRuleEntity> rules = repository.findAllByApp(app);
        rulePublisher.publish(app, rules);
    }
}

```

## DegradeController

```java
/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.csp.sentinel.dashboard.controller;

import java.util.Date;
import java.util.List;

import com.alibaba.csp.sentinel.dashboard.auth.AuthAction;
import com.alibaba.csp.sentinel.dashboard.client.SentinelApiClient;
import com.alibaba.csp.sentinel.dashboard.discovery.MachineInfo;
import com.alibaba.csp.sentinel.dashboard.auth.AuthService.PrivilegeType;
import com.alibaba.csp.sentinel.dashboard.repository.rule.RuleRepository;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.degrade.circuitbreaker.CircuitBreakerStrategy;
import com.alibaba.csp.sentinel.util.StringUtil;

import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.DegradeRuleEntity;
import com.alibaba.csp.sentinel.dashboard.domain.Result;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller regarding APIs of degrade rules. Refactored since 1.8.0.
 *
 * @author Carpenter Lee
 * @author Eric Zhao
 */
@RestController
@RequestMapping("/degrade")
public class DegradeController {

    private final Logger logger = LoggerFactory.getLogger(DegradeController.class);

    @Autowired
    private RuleRepository<DegradeRuleEntity, Long> repository;
    @Autowired
    private SentinelApiClient sentinelApiClient;

    @GetMapping("/rules.json")
    @AuthAction(PrivilegeType.READ_RULE)
    public Result<List<DegradeRuleEntity>> apiQueryMachineRules(String app, String ip, Integer port) {
        if (StringUtil.isEmpty(app)) {
            return Result.ofFail(-1, "app can't be null or empty");
        }
        try {
            List<DegradeRuleEntity> rules = repository.findAllByApp(app);
            return Result.ofSuccess(rules);
        } catch (Throwable throwable) {
            logger.error("queryApps error:", throwable);
            return Result.ofThrowable(-1, throwable);
        }
    }

    @PostMapping("/rule")
    @AuthAction(PrivilegeType.WRITE_RULE)
    public Result<DegradeRuleEntity> apiAddRule(@RequestBody DegradeRuleEntity entity) {
        Result<DegradeRuleEntity> checkResult = checkEntityInternal(entity);
        if (checkResult != null) {
            return checkResult;
        }
        Date date = new Date();
        entity.setGmtCreate(date);
        entity.setGmtModified(date);
        try {
            entity = repository.save(entity);
        } catch (Throwable t) {
            logger.error("Failed to add new degrade rule, app={}, ip={}", entity.getApp(), entity.getIp(), t);
            return Result.ofThrowable(-1, t);
        }
        return Result.ofSuccess(entity);
    }

    @PutMapping("/rule/{id}")
    @AuthAction(PrivilegeType.WRITE_RULE)
    public Result<DegradeRuleEntity> apiUpdateRule(@PathVariable("id") Long id,
                                                     @RequestBody DegradeRuleEntity entity) {
        if (id == null || id <= 0) {
            return Result.ofFail(-1, "id can't be null or negative");
        }
        DegradeRuleEntity oldEntity = repository.findById(id);
        if (oldEntity == null) {
            return Result.ofFail(-1, "Degrade rule does not exist, id=" + id);
        }
        entity.setApp(oldEntity.getApp());
        entity.setIp(oldEntity.getIp());
        entity.setPort(oldEntity.getPort());
        entity.setId(oldEntity.getId());
        Result<DegradeRuleEntity> checkResult = checkEntityInternal(entity);
        if (checkResult != null) {
            return checkResult;
        }

        entity.setGmtCreate(oldEntity.getGmtCreate());
        entity.setGmtModified(new Date());
        try {
            entity = repository.save(entity);
        } catch (Throwable t) {
            logger.error("Failed to save degrade rule, id={}, rule={}", id, entity, t);
            return Result.ofThrowable(-1, t);
        }
        return Result.ofSuccess(entity);
    }

    @DeleteMapping("/rule/{id}")
    @AuthAction(PrivilegeType.DELETE_RULE)
    public Result<Long> delete(@PathVariable("id") Long id) {
        if (id == null) {
            return Result.ofFail(-1, "id can't be null");
        }

        DegradeRuleEntity oldEntity = repository.findById(id);
        if (oldEntity == null) {
            return Result.ofSuccess(null);
        }

        try {
            repository.delete(id);
        } catch (Throwable throwable) {
            logger.error("Failed to delete degrade rule, id={}", id, throwable);
            return Result.ofThrowable(-1, throwable);
        }
        return Result.ofSuccess(id);
    }

    private boolean publishRules(String app, String ip, Integer port) {
        List<DegradeRuleEntity> rules = repository.findAllByMachine(MachineInfo.of(app, ip, port));
        return sentinelApiClient.setDegradeRuleOfMachine(app, ip, port, rules);
    }

    private <R> Result<R> checkEntityInternal(DegradeRuleEntity entity) {
        if (StringUtil.isBlank(entity.getApp())) {
            return Result.ofFail(-1, "app can't be blank");
        }
        if (StringUtil.isBlank(entity.getIp())) {
            return Result.ofFail(-1, "ip can't be null or empty");
        }
        if (entity.getPort() == null || entity.getPort() <= 0) {
            return Result.ofFail(-1, "invalid port: " + entity.getPort());
        }
        if (StringUtil.isBlank(entity.getLimitApp())) {
            return Result.ofFail(-1, "limitApp can't be null or empty");
        }
        if (StringUtil.isBlank(entity.getResource())) {
            return Result.ofFail(-1, "resource can't be null or empty");
        }
        Double threshold = entity.getCount();
        if (threshold == null || threshold < 0) {
            return Result.ofFail(-1, "invalid threshold: " + threshold);
        }
        Integer recoveryTimeoutSec = entity.getTimeWindow();
        if (recoveryTimeoutSec == null || recoveryTimeoutSec <= 0) {
            return Result.ofFail(-1, "recoveryTimeout should be positive");
        }
        Integer strategy = entity.getGrade();
        if (strategy == null) {
            return Result.ofFail(-1, "circuit breaker strategy cannot be null");
        }
        if (strategy < CircuitBreakerStrategy.SLOW_REQUEST_RATIO.getType()
            || strategy > RuleConstant.DEGRADE_GRADE_EXCEPTION_COUNT) {
            return Result.ofFail(-1, "Invalid circuit breaker strategy: " + strategy);
        }
        if (entity.getMinRequestAmount()  == null || entity.getMinRequestAmount() <= 0) {
            return Result.ofFail(-1, "Invalid minRequestAmount");
        }
        if (entity.getStatIntervalMs() == null || entity.getStatIntervalMs() <= 0) {
            return Result.ofFail(-1, "Invalid statInterval");
        }
        if (strategy == RuleConstant.DEGRADE_GRADE_RT) {
            Double slowRatio = entity.getSlowRatioThreshold();
            if (slowRatio == null) {
                return Result.ofFail(-1, "SlowRatioThreshold is required for slow request ratio strategy");
            } else if (slowRatio < 0 || slowRatio > 1) {
                return Result.ofFail(-1, "SlowRatioThreshold should be in range: [0.0, 1.0]");
            }
        } else if (strategy == RuleConstant.DEGRADE_GRADE_EXCEPTION_RATIO) {
            if (threshold > 1) {
                return Result.ofFail(-1, "Ratio threshold should be in range: [0.0, 1.0]");
            }
        }
        return null;
    }
}

```

## ParamFlowRuleController

```java
package com.alibaba.csp.sentinel.dashboard.controller;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import com.alibaba.csp.sentinel.dashboard.auth.AuthAction;
import com.alibaba.csp.sentinel.dashboard.client.CommandNotFoundException;
import com.alibaba.csp.sentinel.dashboard.client.SentinelApiClient;
import com.alibaba.csp.sentinel.dashboard.discovery.AppManagement;
import com.alibaba.csp.sentinel.dashboard.discovery.MachineInfo;
import com.alibaba.csp.sentinel.dashboard.auth.AuthService;
import com.alibaba.csp.sentinel.dashboard.auth.AuthService.PrivilegeType;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.util.StringUtil;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.SentinelVersion;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.ParamFlowRuleEntity;
import com.alibaba.csp.sentinel.dashboard.domain.Result;
import com.alibaba.csp.sentinel.dashboard.repository.rule.RuleRepository;
import com.alibaba.csp.sentinel.dashboard.util.VersionUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Eric Zhao
 * @since 0.2.1
 */
@RestController
@RequestMapping(value = "/paramFlow")
public class ParamFlowRuleController {

    private final Logger logger = LoggerFactory.getLogger(ParamFlowRuleController.class);

    @Autowired
    private SentinelApiClient sentinelApiClient;
    @Autowired
    private AppManagement appManagement;
    @Autowired
    private RuleRepository<ParamFlowRuleEntity, Long> repository;

    @GetMapping("/rules")
    @AuthAction(PrivilegeType.READ_RULE)
    public Result<List<ParamFlowRuleEntity>> apiQueryAllRulesForMachine(@RequestParam String app,
                                                                        @RequestParam String ip,
                                                                        @RequestParam Integer port) {
        if (StringUtil.isEmpty(app)) {
            return Result.ofFail(-1, "app cannot be null or empty");
        }
        try {
            return Result.ofSuccess(repository.findAllByApp(app));
        } catch (Exception ex) {
            logger.error("Error when querying parameter flow rules", ex.getCause());
            if (isNotSupported(ex.getCause())) {
                return unsupportedVersion();
            } else {
                return Result.ofThrowable(-1, ex.getCause());
            }
        } catch (Throwable throwable) {
            logger.error("Error when querying parameter flow rules", throwable);
            return Result.ofFail(-1, throwable.getMessage());
        }
    }

    private boolean isNotSupported(Throwable ex) {
        return ex instanceof CommandNotFoundException;
    }

    @PostMapping("/rule")
    @AuthAction(AuthService.PrivilegeType.WRITE_RULE)
    public Result<ParamFlowRuleEntity> apiAddParamFlowRule(@RequestBody ParamFlowRuleEntity entity) {
        Result<ParamFlowRuleEntity> checkResult = checkEntityInternal(entity);
        if (checkResult != null) {
            return checkResult;
        }
        entity.setId(null);
        entity.getRule().setResource(entity.getResource().trim());
        Date date = new Date();
        entity.setGmtCreate(date);
        entity.setGmtModified(date);
        try {
            entity = repository.save(entity);
            return Result.ofSuccess(entity);
        } catch (Exception ex) {
            logger.error("Error when adding new parameter flow rules", ex.getCause());
            if (isNotSupported(ex.getCause())) {
                return unsupportedVersion();
            } else {
                return Result.ofThrowable(-1, ex.getCause());
            }
        } catch (Throwable throwable) {
            logger.error("Error when adding new parameter flow rules", throwable);
            return Result.ofFail(-1, throwable.getMessage());
        }
    }

    private <R> Result<R> checkEntityInternal(ParamFlowRuleEntity entity) {
        if (entity == null) {
            return Result.ofFail(-1, "bad rule body");
        }
        if (StringUtil.isBlank(entity.getApp())) {
            return Result.ofFail(-1, "app can't be null or empty");
        }
        if (StringUtil.isBlank(entity.getIp())) {
            return Result.ofFail(-1, "ip can't be null or empty");
        }
        if (entity.getPort() == null || entity.getPort() <= 0) {
            return Result.ofFail(-1, "port can't be null");
        }
        if (entity.getRule() == null) {
            return Result.ofFail(-1, "rule can't be null");
        }
        if (StringUtil.isBlank(entity.getResource())) {
            return Result.ofFail(-1, "resource name cannot be null or empty");
        }
        if (entity.getCount() < 0) {
            return Result.ofFail(-1, "count should be valid");
        }
        if (entity.getGrade() != RuleConstant.FLOW_GRADE_QPS) {
            return Result.ofFail(-1, "Unknown mode (blockGrade) for parameter flow control");
        }
        if (entity.getParamIdx() == null || entity.getParamIdx() < 0) {
            return Result.ofFail(-1, "paramIdx should be valid");
        }
        if (entity.getDurationInSec() <= 0) {
            return Result.ofFail(-1, "durationInSec should be valid");
        }
        if (entity.getControlBehavior() < 0) {
            return Result.ofFail(-1, "controlBehavior should be valid");
        }
        return null;
    }

    @PutMapping("/rule/{id}")
    @AuthAction(AuthService.PrivilegeType.WRITE_RULE)
    public Result<ParamFlowRuleEntity> apiUpdateParamFlowRule(@PathVariable("id") Long id,
                                                              @RequestBody ParamFlowRuleEntity entity) {
        if (id == null || id <= 0) {
            return Result.ofFail(-1, "Invalid id");
        }
        ParamFlowRuleEntity oldEntity = repository.findById(id);
        if (oldEntity == null) {
            return Result.ofFail(-1, "id " + id + " does not exist");
        }

        Result<ParamFlowRuleEntity> checkResult = checkEntityInternal(entity);
        if (checkResult != null) {
            return checkResult;
        }
        entity.setId(id);
        Date date = new Date();
        entity.setGmtCreate(oldEntity.getGmtCreate());
        entity.setGmtModified(date);
        try {
            entity = repository.save(entity);
            return Result.ofSuccess(entity);
        } catch (Exception ex) {
            logger.error("Error when updating parameter flow rules, id=" + id, ex.getCause());
            if (isNotSupported(ex.getCause())) {
                return unsupportedVersion();
            } else {
                return Result.ofThrowable(-1, ex.getCause());
            }
        } catch (Throwable throwable) {
            logger.error("Error when updating parameter flow rules, id=" + id, throwable);
            return Result.ofFail(-1, throwable.getMessage());
        }
    }

    @DeleteMapping("/rule/{id}")
    @AuthAction(PrivilegeType.DELETE_RULE)
    public Result<Long> apiDeleteRule(@PathVariable("id") Long id) {
        if (id == null) {
            return Result.ofFail(-1, "id cannot be null");
        }
        ParamFlowRuleEntity oldEntity = repository.findById(id);
        if (oldEntity == null) {
            return Result.ofSuccess(null);
        }

        try {
            repository.delete(id);
            return Result.ofSuccess(id);
        } catch (Exception ex) {
            logger.error("Error when deleting parameter flow rules", ex.getCause());
            if (isNotSupported(ex.getCause())) {
                return unsupportedVersion();
            } else {
                return Result.ofThrowable(-1, ex.getCause());
            }
        } catch (Throwable throwable) {
            logger.error("Error when deleting parameter flow rules", throwable);
            return Result.ofFail(-1, throwable.getMessage());
        }
    }

    private CompletableFuture<Void> publishRules(String app, String ip, Integer port) {
        List<ParamFlowRuleEntity> rules = repository.findAllByMachine(MachineInfo.of(app, ip, port));
        return sentinelApiClient.setParamFlowRuleOfMachine(app, ip, port, rules);
    }

    private <R> Result<R> unsupportedVersion() {
        return Result.ofFail(4041,
            "Sentinel client not supported for parameter flow control (unsupported version or dependency absent)");
    }

    private final SentinelVersion version020 = new SentinelVersion().setMinorVersion(2);
}

```

## SystemController

```java
package com.alibaba.csp.sentinel.dashboard.controller;

import java.util.Date;
import java.util.List;

import com.alibaba.csp.sentinel.dashboard.auth.AuthAction;
import com.alibaba.csp.sentinel.dashboard.auth.AuthService.PrivilegeType;
import com.alibaba.csp.sentinel.dashboard.repository.rule.RuleRepository;
import com.alibaba.csp.sentinel.util.StringUtil;

import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.SystemRuleEntity;
import com.alibaba.csp.sentinel.dashboard.discovery.MachineInfo;
import com.alibaba.csp.sentinel.dashboard.client.SentinelApiClient;
import com.alibaba.csp.sentinel.dashboard.domain.Result;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author leyou(lihao)
 */
@RestController
@RequestMapping("/system")
public class SystemController {

    private final Logger logger = LoggerFactory.getLogger(SystemController.class);

    @Autowired
    private RuleRepository<SystemRuleEntity, Long> repository;
    @Autowired
    private SentinelApiClient sentinelApiClient;

    private <R> Result<R> checkBasicParams(String app, String ip, Integer port) {
        if (StringUtil.isEmpty(app)) {
            return Result.ofFail(-1, "app can't be null or empty");
        }
        if (StringUtil.isEmpty(ip)) {
            return Result.ofFail(-1, "ip can't be null or empty");
        }
        if (port == null) {
            return Result.ofFail(-1, "port can't be null");
        }
        if (port <= 0 || port > 65535) {
            return Result.ofFail(-1, "port should be in (0, 65535)");
        }
        return null;
    }

    @GetMapping("/rules.json")
    @AuthAction(PrivilegeType.READ_RULE)
    public Result<List<SystemRuleEntity>> apiQueryMachineRules(String app, String ip,
                                                               Integer port) {
        Result<List<SystemRuleEntity>> checkResult = checkBasicParams(app, ip, port);
        if (checkResult != null) {
            return checkResult;
        }
        try {
            List<SystemRuleEntity> rules = repository.findAllByApp(app);
            return Result.ofSuccess(rules);
        } catch (Throwable throwable) {
            logger.error("Query machine system rules error", throwable);
            return Result.ofThrowable(-1, throwable);
        }
    }

    private int countNotNullAndNotNegative(Number... values) {
        int notNullCount = 0;
        for (int i = 0; i < values.length; i++) {
            if (values[i] != null && values[i].doubleValue() >= 0) {
                notNullCount++;
            }
        }
        return notNullCount;
    }

    @RequestMapping("/new.json")
    @AuthAction(PrivilegeType.WRITE_RULE)
    public Result<SystemRuleEntity> apiAdd(String app, String ip, Integer port,
                                           Double highestSystemLoad, Double highestCpuUsage, Long avgRt,
                                           Long maxThread, Double qps) {

        Result<SystemRuleEntity> checkResult = checkBasicParams(app, ip, port);
        if (checkResult != null) {
            return checkResult;
        }

        int notNullCount = countNotNullAndNotNegative(highestSystemLoad, avgRt, maxThread, qps, highestCpuUsage);
        if (notNullCount != 1) {
            return Result.ofFail(-1, "only one of [highestSystemLoad, avgRt, maxThread, qps,highestCpuUsage] "
                + "value must be set > 0, but " + notNullCount + " values get");
        }
        if (null != highestCpuUsage && highestCpuUsage > 1) {
            return Result.ofFail(-1, "highestCpuUsage must between [0.0, 1.0]");
        }
        SystemRuleEntity entity = new SystemRuleEntity();
        entity.setApp(app.trim());
        entity.setIp(ip.trim());
        entity.setPort(port);
        // -1 is a fake value
        if (null != highestSystemLoad) {
            entity.setHighestSystemLoad(highestSystemLoad);
        } else {
            entity.setHighestSystemLoad(-1D);
        }

        if (null != highestCpuUsage) {
            entity.setHighestCpuUsage(highestCpuUsage);
        } else {
            entity.setHighestCpuUsage(-1D);
        }

        if (avgRt != null) {
            entity.setAvgRt(avgRt);
        } else {
            entity.setAvgRt(-1L);
        }
        if (maxThread != null) {
            entity.setMaxThread(maxThread);
        } else {
            entity.setMaxThread(-1L);
        }
        if (qps != null) {
            entity.setQps(qps);
        } else {
            entity.setQps(-1D);
        }
        Date date = new Date();
        entity.setGmtCreate(date);
        entity.setGmtModified(date);
        try {
            entity = repository.save(entity);
        } catch (Throwable throwable) {
            logger.error("Add SystemRule error", throwable);
            return Result.ofThrowable(-1, throwable);
        }
        return Result.ofSuccess(entity);
    }

    @GetMapping("/save.json")
    @AuthAction(PrivilegeType.WRITE_RULE)
    public Result<SystemRuleEntity> apiUpdateIfNotNull(Long id, String app, Double highestSystemLoad,
            Double highestCpuUsage, Long avgRt, Long maxThread, Double qps) {
        if (id == null) {
            return Result.ofFail(-1, "id can't be null");
        }
        SystemRuleEntity entity = repository.findById(id);
        if (entity == null) {
            return Result.ofFail(-1, "id " + id + " dose not exist");
        }

        if (StringUtil.isNotBlank(app)) {
            entity.setApp(app.trim());
        }
        if (highestSystemLoad != null) {
            if (highestSystemLoad < 0) {
                return Result.ofFail(-1, "highestSystemLoad must >= 0");
            }
            entity.setHighestSystemLoad(highestSystemLoad);
        }
        if (highestCpuUsage != null) {
            if (highestCpuUsage < 0) {
                return Result.ofFail(-1, "highestCpuUsage must >= 0");
            }
            if (highestCpuUsage > 1) {
                return Result.ofFail(-1, "highestCpuUsage must <= 1");
            }
            entity.setHighestCpuUsage(highestCpuUsage);
        }
        if (avgRt != null) {
            if (avgRt < 0) {
                return Result.ofFail(-1, "avgRt must >= 0");
            }
            entity.setAvgRt(avgRt);
        }
        if (maxThread != null) {
            if (maxThread < 0) {
                return Result.ofFail(-1, "maxThread must >= 0");
            }
            entity.setMaxThread(maxThread);
        }
        if (qps != null) {
            if (qps < 0) {
                return Result.ofFail(-1, "qps must >= 0");
            }
            entity.setQps(qps);
        }
        Date date = new Date();
        entity.setGmtModified(date);
        try {
            entity = repository.save(entity);
        } catch (Throwable throwable) {
            logger.error("save error:", throwable);
            return Result.ofThrowable(-1, throwable);
        }
        return Result.ofSuccess(entity);
    }

    @RequestMapping("/delete.json")
    @AuthAction(PrivilegeType.DELETE_RULE)
    public Result<?> delete(Long id) {
        if (id == null) {
            return Result.ofFail(-1, "id can't be null");
        }
        SystemRuleEntity oldEntity = repository.findById(id);
        if (oldEntity == null) {
            return Result.ofSuccess(null);
        }
        try {
            repository.delete(id);
        } catch (Throwable throwable) {
            logger.error("delete error:", throwable);
            return Result.ofThrowable(-1, throwable);
        }
        return Result.ofSuccess(id);
    }

    private boolean publishRules(String app, String ip, Integer port) {
        List<SystemRuleEntity> rules = repository.findAllByMachine(MachineInfo.of(app, ip, port));
        return sentinelApiClient.setSystemRuleOfMachine(app, ip, port, rules);
    }
}

```

# 打包镜像并且部署

## 新增Docker启动文件

​		根目录下放入docker-entrypoint.sh文件

```sh
#!/bin/sh
#startup Server
RUN_CMD="java"

# 应用参数
RUN_CMD="$RUN_CMD -Dserver.port:\"$SERVER_PORT\""
RUN_CMD="$RUN_CMD -Dcsp.sentinel.dashboard.server=\"$DASHBOARD_SERVER\""
RUN_CMD="$RUN_CMD -Dproject.name=\"$PROJECT_NAME\""
RUN_CMD="$RUN_CMD -Dauth.username=\"$USERNAME\""
RUN_CMD="$RUN_CMD -Dauth.password=\"$PASSWORD\""

RUN_CMD="$RUN_CMD $JAVA_OPTS"
RUN_CMD="$RUN_CMD -jar"
RUN_CMD="$RUN_CMD sentinel-dashboard.jar"

RUN_CMD="$RUN_CMD --nacos.server.ip=$NACOS_IP"
RUN_CMD="$RUN_CMD --nacos.server.port=$NACOS_PORT"
RUN_CMD="$RUN_CMD --nacos.server.namespace=$NACOS_NAMESPACE"
RUN_CMD="$RUN_CMD --nacos.server.groupId=$NACOS_GROUP"
RUN_CMD="$RUN_CMD --nacos.server.enable=$NACOS_ENABLE"

RUN_CMD="$RUN_CMD $APP_OPTS"
echo $RUN_CMD
eval $RUN_CMD
```

## 新增Dockerfile

​		项目目录下放入Dockerfile文件

```dockerfile
FROM java:alpine
MAINTAINER bigkang "bigkangsix@qq.com"

# set environment
ENV BASE_DIR="/home/sentinel" \
    SERVER_PORT="9999" \
    DASHBOARD_SERVER="localhost:9999" \
    USERNAME="sentinel" \
    PASSWORD="sentinel" \
    PROJECT_NAME="sentinel-dashboard" \
    NACOS_IP="127.0.0.1" \
    NACOS_PORT="8848" \
    NACOS_NAMESPACE="sentinel" \
    NACOS_GROUP="DEFAULT_GROUP" \
    NACOS_ENABLE="false" \
    JAVA_OPTS="" \
    APP_OPTS="" \
    TIME_ZONE="Asia/Shanghai"

WORKDIR /$BASE_DIR

RUN set -x \
    && update-ca-certificates \
    && ln -snf /usr/share/zoneinfo/$TIME_ZONE /etc/localtime && echo  > /etc/timezone

ADD target/sentinel-dashboard.jar sentinel-dashboard.jar

ADD docker-entrypoint.sh bin/docker-entrypoint.sh

# set startup log dir
RUN mkdir -p logs && cd logs && touch start.out && ln -sf /dev/stdout start.out && ln -sf /dev/stderr start.out
RUN chmod +x bin/docker-entrypoint.sh

EXPOSE 9999
ENTRYPOINT ["bin/docker-entrypoint.sh"]
```

## 构建镜像

​		使用如下命令构建镜像

```sh
docker build -t bigkang/sentinel-nacos:1.8.1 .
```

## compose启动

​		！！！！！首先需要Nacos创建命名空间，否则控制台看不到配置，实际上对存储没有影响

​		创建Compose文件

```dockerfile
cat > ./docker-compose-sentinel-dashboard.yml << EOF
version: '3.4'
services:
  sentinel-dashboard:
    container_name: sentinel-dashboard       # 指定容器的名称
    image: bigkang/sentinel-nacos:1.8.1         # 指定镜像和版本
    restart: always  # 自动重启
    hostname: sentinel-dashboard
    ports:
      - 9999:9999
    environment:
      SERVER_PORT: 9999       		# sentinel端口号
      USERNAME: bigkang						# sentinel界面登录用户名
      PASSWORD: bigkang						# sentinel登录用户名密码
      NACOS_IP: 127.0.0.1					# 存储的Nacos的IP地址
      NACOS_PORT: 8848						# 存储的Nacos的PORT端口
      NACOS_NAMESPACE: sentinel		# 存储的Nacos的命名空间
      NACOS_GROUP: DEFAULT_GROUP	# 存储的Nacos的分组名
      NACOS_ENABLE: "true"				# 是否启用Nacos存储，设置false则全部还是使用内存存储
    privileged: true
EOF
```

​		启动Compose

```sh
docker-compose -f docker-compose-sentinel-dashboard.yml up -d
```

