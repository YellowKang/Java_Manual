# 引入依赖

```xml
				<!-- 引入测试包 -->
				<dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
        </dependency>

        <dependency>
            <groupId>org.junit.vintage</groupId>
            <artifactId>junit-vintage-engine</artifactId>
            <scope>test</scope>
            <exclusions>
                <exclusion>
                    <groupId>org.hamcrest</groupId>
                    <artifactId>hamcrest-core</artifactId>
                </exclusion>
            </exclusions>
        </dependency>

        <dependency>
            <groupId>org.jacoco</groupId>
            <artifactId>jacoco-maven-plugin</artifactId>
            <version>0.8.8</version>
        </dependency>
```

# 引入插件

```xml

            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-surefire-plugin</artifactId>
            </plugin>
            <plugin>
                <groupId>org.jacoco</groupId>
                <artifactId>jacoco-maven-plugin</artifactId>
                <version>0.8.8</version>
                <executions>
                    <execution>
                        <goals>
                            <goal>prepare-agent</goal>
                        </goals>
                    </execution>
                    <execution>
                        <id>report</id>
                        <phase>test</phase>
                        <goals>
                            <goal>report</goal>
                        </goals>

                        <configuration>
                            <!--定义输出的文件夹-->
                            <outputDirectory>target/jacoco-report</outputDirectory>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
```

# 测试用例

## 测试Controller

```java

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,properties = {""})
@AutoConfigureMockMvc
@Slf4j
public class SigrealXpApplicationTests {
  
    @Autowired
    private MockMvc mockMvc;
  
    @Autowired
    private WebApplicationContext webApplicationContext;
  
    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
        System.out.println("开始测试-------------");
        mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }
  
    @After
    public void after() {
        System.out.println("测试结束--------------");
    }

		@Test
    public void webMvcSignData() throws Exception {
        // 模拟报文
        String content = "{!data}";


        // 发送Post请求
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/syncData/signData")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(content))
                // 校验httpCode
                .andExpect(MockMvcResultMatchers.status().isOk())
                // 打印请求报文
                .andDo(MockMvcResultHandlers.print())
                // jsonPath工具校验响应报文
                .andExpect(MockMvcResultMatchers.jsonPath("$.ifSuccess").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.returnCode").isEmpty())
                .andReturn();

        // 获取响应报文
        String contentAsString = mvcResult.getResponse().getContentAsString();

        // 转换实体（忽略即可）
        VehicleDataVo vehicleDataVo = JSON.parseObject(contentAsString, VehicleDataVo.class);

        // 校验响应
        Assertions.assertEquals("Y", vehicleDataVo.getIfSuccess());
        Assertions.assertNotEquals("500", vehicleDataVo.getReturnCode());
        
        // 打印日志
        log.info(mvcResult.getResponse().getContentAsString());

    }
```

## 测试Service

​		直接注入Service即可

```java
				
				DataPushReqDto dataPushReqDto = new DataPushReqDto();

				// 调用Service
        VehicleDataVo vehicleDataVo = syncDataService.syncVehicleData(dataPushReqDto);

        Assertions.assertEquals("Y",vehicleDataVo.getIfSuccess());

```



## MockService

​		例如syncDataService调用了其他的Service进行了HTTP接口调用我们想要Mock，测试环境中并不进行调用，使用如下方法即可。

```java
    @Autowired
    private SyncDataService syncDataService;
    
    @MockBean
    private SendService sendService;

********
				// 集成测试
				syncDataService = Mockito.spy(syncDataService);
				// 模拟数据
        VehicleDataVo vehicleDataVo = new VehicleDataVo();
        vehicleDataVo.setIfSuccess("Y");
        vehicleDataVo.setSurveyFlag("0");
				
				// 所有调用sendMessage方法的参数，都返回固定的数据，并且不进行真实调用
        Mockito.when(sendService.sendMessage(Mockito.any())).thenReturn(vehicleDataVo);

```



# 执行单元测试

​		执行测试

```sh
mvn clean test
```

​		找到jacoco报告,打开index.html即可

```sh
cd target/jacoco-report
```

