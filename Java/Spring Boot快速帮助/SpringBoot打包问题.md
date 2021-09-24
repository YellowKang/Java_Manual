# 问题起因

​			我们有时候在SpringBoot的resource下配置了部分的配置文件，有可能他是一个json，或者xml，或者其他格式的文件等等，那么我们在开发的时候能够获取到他，但是在打包成jar包之后就无法获取了，所以我们需要通过特定方式进行读取：

```java
				// 首先获取ClassPathResource
				ClassPathResource classPathResource = new ClassPathResource("ueditor/test.json");
        InputStreamReader inputStreamReader = new InputStreamReader(classPathResource.getInputStream());
        String str = FileCopyUtils.copyToString(inputStreamReader);
```

​			注意：这里虽然ClassPathResource有classPathResource.getFile()方法，但是我们千万不要这样去使用它，如果我们这样使用那么开发的时候可以读取得到，打包jar包之后我们就读取不到这个文件了

