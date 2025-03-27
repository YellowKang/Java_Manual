# SpringBoot接收文件

​		SpringBoot中我们接收文件统一采用MultipartFile接口，MultipartFile是多部分请求中收到的上载文件的表示形式。文件内容要么存储在内存中，要么临时存储在磁盘上。在这两种情况下，用户都有责任根据需要将文件内容复制到会话级或持久性存储中。临时存储将在请求处理结束时清除。

​		SpringBoot为我们自动装配了MultipartFile，自动装配路径如下

```properties
org.springframework.boot.autoconfigure.web.servlet.MultipartAutoConfiguration
```

​		会交由StandardServletMultipartResolver进行解析，解析的条件为判断是否为Multipart上传，判断是否以multipart开始的请求头

```java
    public boolean isMultipart(HttpServletRequest request) {
        return StringUtils.startsWithIgnoreCase(request.getContentType(), "multipart/");
    }
```

​		MultipartFile是一个接口，下面有其他的实现类，那么我们先来关注这个接口有哪些方法吧

```java
public interface MultipartFile extends InputStreamSource {

	/**
	 * 以多部分形式返回参数名称。 @返回参数的名称（不要{@code null}或为空）
	 */
	String getName();

	/**
	 * 返回文件上传时候的原始文件名，通常不包含路径
	 * 返回客户端文件系统中的原始文件名。<p>根据所使用的浏览器，该文件名可能包含路径信息，但Opera除外。
	 * @返回原始文件名；如果未以多部分形式选择文件，则返回空字符串；如果未定义或不可用，则返回{@code null} 
	 * @see org.apache.commons.fileupload.FileItem＃getName（）
	 * @see org.springframework.web.multipart.commons.CommonsMultipartFile＃setPreserveFilename
	 */
	@Nullable
	String getOriginalFilename();

	/**
	 * 文件的内容类型
	 * 返回文件的内容类型。 @返回内容类型，如果未定义，则为{@code null}（或未以多部分形式选择文件）
	 */
	@Nullable
	String getContentType();

	/**
	 * 判断是否为空
	 * 返回上传的文件是否为空，即，是否没有以多部分形式选择文件，或者选择的文件没有内容。
	 */
	boolean isEmpty();

	/**
	 * 返回文件的大小（以字节为单位）。
	 * @返回文件的大小；如果为空，则返回0
	 */
	long getSize();

	/**
   * 以字节数组形式返回文件的内容。
   * @返回文件内容为字节，如果为空则返回一个空字节数组
   * @如果发生访问错误（如果临时存储失败），则抛出IOException
	 */
	byte[] getBytes() throws IOException;

	/**
	 * 返回一个InputStream来从中读取文件的内容。
   * <p>用户负责关闭返回的流。
   * @将文件内容作为流返回，如果为空则返回空流
   * @如果发生访问错误（如果临时存储失败），则抛出IOException
	 */
	@Override
	InputStream getInputStream() throws IOException;

	/**
   * 返回此MultipartFile的资源表示形式。可以用
   * 作为{@code RestTemplate}或{@code WebClient}的输入以公开
   * 内容长度和文件名以及InputStream。
   * @返回此MultipartFile以适应资源合同
   * @自5.1起
	 */
	default Resource getResource() {
		return new MultipartFileResource(this);
	}

	/**
   * 将接收的文件传输到给定的目标文件，简称文件复制
   * <p>这可以将文件移动到文件系统中，也可以将文件复制到
   * 文件系统，或将内存保存的内容保存到目标文件。如果目标文件已存在，将首先删除。
   * <p>如果目标文件已在文件系统中移动，则此操作之后无法再次调用。因此，只需调用一次此方法为了与任何存储机制一起使用。
   * <p> <b>注意：</ b>取决于基础提供程序，临时存储
   * 可能与容器有关，包括相对的基本目录在此指定的目的地（例如，使用Servlet 3.0多部分处理）。
   * 对于绝对目的地，目标文件可能会从其重命名/移动临时位置或新复制的，即使已经存在一个临时副本。
   * @param目标文件（通常是绝对文件）
   * @读取或写入错误时抛出IOException
   * @throws IllegalStateException如果文件已经被移动在文件系统中，不再可用于其他传输
   * @see org.apache.commons.fileupload.FileItem＃write（文件）
   * @see javax.servlet.http.Part＃write（String）
	 */
	void transferTo(File dest) throws IOException, IllegalStateException;

	/**
   * 将接收的文件传输到给定的目标文件。
   * <p>默认实现只是复制文件输入流。
   * @自5.1起
   * @see #getInputStream（）
   * @请参阅#transferTo（文件）
 	 */
	default void transferTo(Path dest) throws IOException, IllegalStateException {
		FileCopyUtils.copy(getInputStream(), Files.newOutputStream(dest));
	}

}
```



## 接收文件（上传文件）

​		我们新建一个控制器，使用MultipartFile接收

```java

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @Author BigKang
 * @Date 2020/11/9 10:04 上午
 * @Motto 仰天大笑撸码去, 我辈岂是蓬蒿人
 * @Summarize
 */
@RestController
@RequestMapping("testFile")
public class TestFileController {

    /**
     * 文件上传
     * @param multipartFiles
     * @throws IOException
     */
    @PostMapping("upload")
    public void upload(MultipartFile multipartFiles) throws IOException {
        // 获取文件名称
        System.out.println(multipartFiles.getOriginalFilename());
        // 获取文件大小
        System.out.println(multipartFiles.getSize());
        // 获取文件类型
        System.out.println(multipartFiles.getContentType());
        // 获取字节数组
        System.out.println(multipartFiles.getBytes());
        // 获取输入流
        System.out.println(multipartFiles.getInputStream());
        // 获取Resource资源
        System.out.println(multipartFiles.getResource());
    }

}
```

​		下面我们使用swagger方式进行测试

![](https://blog-kang.oss-cn-beijing.aliyuncs.com/1604899657995.png)

​		打印结果如下：

```
java开发手册-嵩山版.pdf
1580978
application/pdf
[B@77d56f35
java.io.ByteArrayInputStream@7a1128a0
MultipartFile resource [multipartFiles]
```

​		使用Postman测试,选择file类型数据同样可以上传

![](https://blog-kang.oss-cn-beijing.aliyuncs.com/1604899789224.png)

## 接收多文件

​		多文件采用数组方式进行上传，切记请勿使用@RequestBody进行接收

```java
    /**
     * 文件上传
     * @param multipartFiles
     * @throws IOException
     */
    @PostMapping("upload")
    public void upload(MultipartFile[] multipartFiles) throws IOException {
        for (MultipartFile multipartFile : multipartFiles) {
            // 获取文件名称
            System.out.println(multipartFile.getOriginalFilename());
            // 获取文件大小
            System.out.println(multipartFile.getSize());
            // 获取文件类型
            System.out.println(multipartFile.getContentType());
            // 获取字节数组
            System.out.println(multipartFile.getBytes());
            // 获取输入流
            System.out.println(multipartFile.getInputStream());
            // 获取Resource资源
            System.out.println(multipartFile.getResource());
        }
    }
```

​		上传时一次多选取文件即可

## 文件下载（返回文件）

​		我们使用如下

```java
    /**
     * 文件下载
     *
     * @param path 文件路径
     * @throws IOException
     */
    @GetMapping("download")
    public void download(String path, HttpServletResponse response) throws IOException {
        File file = new File(path);
        String name = file.getName();
        FileInputStream fileInputStream = new FileInputStream(file);
        boolean b = responseDownload(fileInputStream, name, response);
        System.out.println(b ? "下载成功":"下载失败");
        response.flushBuffer();
    }

    /**
     * responseDownload响应下载
     * @param inputStream
     * @param fileName
     * @param response
     * @return
     */
    public boolean responseDownload(InputStream inputStream, String fileName, HttpServletResponse response) {
        // 设置response响应类型
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        // 设置response header头，文件名称
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName));
        // 创建字节数组读写流
        byte[] buffer = new byte[1024];
        // 设置上传成功变量判断是否异常
        boolean uploadSuccess = false;
        // try块创建流自动关闭
        try(BufferedInputStream bis = new BufferedInputStream(inputStream);OutputStream os = response.getOutputStream()){
                int i = bis.read(buffer);
                // response输出写入流
                while (i != -1) {
                    os.write(buffer, 0, i);
                    i = bis.read(buffer);
                }
                // 循环结束没有异常写入成功
                uploadSuccess = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return uploadSuccess;
    }
```

## 多文件下载（打包方式）

​		我们先将所有文件打包ZIP然后返回		

```java
   /**
     * 文件下载
     *
     * @param paths 文件路径
     * @throws IOException
     */
    @GetMapping("downloadZip")
    public void downloadZip(String[] paths, HttpServletResponse response) {
        List<File> fileList = new ArrayList<>();
        // 判断文件是否存在
        if (paths != null) {
            for (String path : paths) {
                File file = new File(path);
                if (!file.exists() || !file.isFile()) {
                    System.out.println(file + "不存在");
                } else {
                    fileList.add(file);
                }
            }
        }
        // 文件大于等于一则开始
        if (fileList.size() > 0) {
            // 创建临时文件存储压缩文件
            String filePath = "/Users/bigkang/Documents/test/";
            String fileName = UUID.randomUUID().toString() + ".zip";
            try {
                // 创建zipFile文件
                File zipFile = new File(filePath + fileName);
                if (!zipFile.exists()) {
                    zipFile.createNewFile();
                }
                // 创建ZIP输入流
                ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile));
                // 字节数组
                byte[] buffer = new byte[1024];
                // 字节数组读取
                Integer i = 0;
                // 将所有的文件都写入ZIP压缩包
                for (File file : fileList) {
                    // PUT下一个ZIP节点，也就是一个文件节点
                    zos.putNextEntry(new ZipEntry(file.getName()));
                    // 将文件写入节点
                    try (BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file))) {
                        i = bufferedInputStream.read(buffer);
                        while (i != -1) {
                            zos.write(buffer, 0, i);
                            i = bufferedInputStream.read(buffer, 0, i);
                        }
                    }
                }
                // 关闭输出流
                zos.close();
                FileInputStream fileInputStream = new FileInputStream(zipFile);
                // 返回ZIP文件
                boolean b = responseDownload(fileInputStream, zipFile.getName(), response);
                // 关闭流
                fileInputStream.close();
                // 删除ZIP文件
                zipFile.delete();
                System.out.println(b ? "下载成功" : "下载失败");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("空文件");
        }
        try {
            response.flushBuffer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * responseDownload响应下载
     *
     * @param inputStream
     * @param fileName
     * @param response
     * @return
     */
    public boolean responseDownload(InputStream inputStream, String fileName, HttpServletResponse response) {
        // 设置response响应类型
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        // 设置response header头，文件名称
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName));
        // 创建字节数组读写流
        byte[] buffer = new byte[1024];
        // 设置上传成功变量判断是否异常
        boolean uploadSuccess = false;
        // try块创建流自动关闭
        try (BufferedInputStream bis = new BufferedInputStream(inputStream); OutputStream os = response.getOutputStream()) {
            int i = bis.read(buffer);
            // response输出写入流
            while (i != -1) {
                os.write(buffer, 0, i);
                i = bis.read(buffer);
            }
            // 循环结束没有异常写入成功
            uploadSuccess = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return uploadSuccess;
    }
```

## 多文件下载（Zip压缩+纯内存）



```java

        // Set response headers for file download
        response.setContentType("application/zip");
        response.setCharacterEncoding("utf-8");
        String repFileName = LocalDateTimeUtil.format(LocalDateTime.now(),"yyyy-MM-dd_HH_mm") + "文件材料";
        try {
            repFileName = URLEncoder.encode(repFileName + ".zip", String.valueOf(StandardCharsets.UTF_8));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        response.setHeader("Content-disposition", "attachment;filename=" + repFileName);
        response.setHeader("filename", repFileName);
        response.setHeader("Access-Control-Expose-Headers", "Content-Disposition, Content-Length");

        Map<String, byte[]> filesMap = new HashMap<>();


				// 指定zip文件路径 以及字节数组
        try (ZipOutputStream zipOutputStream = new ZipOutputStream(response.getOutputStream())) {
            for (Map.Entry<String, byte[]> fileEntry : filesMap.entrySet()) {
                String fileName = fileEntry.getKey();
                byte[] fileContent = fileEntry.getValue();

                if(fileContent == null){
                    fileContent = new byte[0];
                }
                // Create a new ZIP entry for each file
                ZipEntry zipEntry = new ZipEntry(fileName);
                zipOutputStream.putNextEntry(zipEntry);

                // Write the file content to the ZIP output stream
                zipOutputStream.write(fileContent);
                zipOutputStream.closeEntry();
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new CustomSystemException("下载失败");
        }
```



# SpringBoot配置MultipartFile

​		我们可以配置SpringBoot的MultipartFile上传属性，如下：

​		yaml格式

```properties
spring:
  servlet:
    multipart:
      # 是否启用分段上传支持。
      enabled: true
      # 最大文件大小，单个文件。
      max-file-size: 16MB
      # 最大请求大小，整个请求的所有文件最大大小。
      max-request-size: 30MB
      # 阈值，超过设置大小之后将文件写入磁盘location临时位置。
      file-size-threshold: 10MB
      # 是否在文件或参数访问时延迟解决多部分请求。
      resolve-lazily: true
      # 上载文件的中间位置，这里的文件上传位置是指临时文件目录完成后会删除
      location: /Users/bigkang/Documents/test/image
```

​		properties格式

```properties
# 是否启用分段上传支持。
spring.servlet.multipart.enabled=true
# 最大文件大小，单个文件。
spring.servlet.multipart.max-file-size=16MB
# 最大请求大小，整个请求的所有文件最大大小。
spring.servlet.multipart.max-request-size=30MB
 # 阈值，超过设置大小之后将文件写入磁盘location临时位置。
spring.servlet.multipart.file-size-threshold=10MB
# 是否在文件或参数访问时延迟解决多部分请求。
spring.servlet.multipart.resolve-lazily=true
# 上载文件的中间位置，这里的文件上传位置是指临时文件目录完成后会删除
spring.servlet.multipart.location=/Users/bigkang/Documents/test/image
```



# 创建文件

## 在磁盘绝对目录创建

我们再windows的E盘下创建一个test.txt

这里返回的结果如果是true代表创建成功，false表示有这个文件夹，异常则为路径错误

```java
        try {
            boolean newFile = new File("E:\\test.txt").createNewFile();
            System.out.println(newFile ? "创建成功" : "创建失败，文件已存在");
        } catch (IOException e) {
            e.printStackTrace();
        }
```

## 在项目中相对目录创建文件

我们在Springboot的resources下的data目录创建一个文件

代码示例如下：

```java
        try {
            boolean newFile = new File("src/main/resources/data/createFile.txt").createNewFile();
            System.out.println(newFile ? "创建成功" : "创建失败，文件已经存在");
        } catch (IOException e) {
            e.printStackTrace();
        }
```

# 读取resource资源文件

​		我们在SpringBoot读取文件的时候直接根据文件路径去new File或者根据Path找虽然能够找到，但是还是会出现读取不到的情况，因为这些资源文件已经被打成了jar包，我们使用ClassPathResource进行读取，然后直接获取输入流。

```java
				// 首先获取ClassPathResource
				ClassPathResource classPathResource = new ClassPathResource("ueditor/test.json");
        InputStreamReader inputStreamReader = new InputStreamReader(classPathResource.getInputStream());
        String str = FileCopyUtils.copyToString(inputStreamReader);
```

# 创建文件夹

我们这里在boot中的resources创建了一个data目录

```java
 		File file = new File("src/main/resources/data");
        boolean mkdir = file.mkdir();
        System.out.println(mkdir ? "创建文件夹成功" : "创建文件失败，文件夹已经存在");
    
```

# 新建文件并且创建文件夹

```java
       
				File target = new File("/User/bigkang/Document/test/2021/test.txt");
				// 首先判断文件夹是否存在
        File parentFile = target.getParentFile();
        if (parentFile.exists()) {
            if (parentFile.isFile()) {
                parentFile.delete();
                parentFile.mkdirs();
            }
        } else {
            parentFile.mkdirs();
        }

        // 文件不存在则创建
        if (target != null) {
            if (!target.exists()) {
                try {
                    target.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
```

# 检查文件是否存在

使用exists方法判断文件是否存在

```java
        boolean exists = new File("src/main/resources/data/createFile.txt").exists();
        System.out.println(exists ? "文件已经存在" : "文件不存在");
```

# 删除文件

## 删除单个文件

我们可以先判断是否存在再删除，也可以直接获取删除结果

```java
//        删除文件
        File file = new File("src/main/resources/data/createFile.txt");
//        if (file.exists()) {
            System.out.println(file.delete() ? "删除成功" : "删除失败没有文件");
//        }
```

## 删除整个目录以及下面子文件夹子文件

利用递归删除，获取文件夹下的文件，再次判断如果是文件夹继续删除文件，在这个文件夹中传入文件夹路径即可删除

```java
   public void delDir(String path) {
        File dir = new File(path);
        if (dir.exists()) {
            File[] tmp = dir.listFiles();
            for (int i = 0; i < tmp.length; i++) {
                if (tmp[i].isDirectory()) {
                    delDir(path + "/" + tmp[i].getName());
                } else {
                    tmp[i].delete();
                }
            }
            dir.delete();
        }
    }
```

# 修改文件名字

如果要修改的目标的文件名已经存在那么则修改失败，不进行修改

```java
        File file = new File("src/main/resources/data/test.txt");
		//文件不存在则创建
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        File file1 = new File("src/main/resources/data/testas.txt");
        boolean b = file.renameTo(file1);
        System.out.println(b ? "修改成功" : "修改失败");
```

# 向文件写入

## 使用BufferedWriter

这里的FileOutputStream后面的false参数表示不再增量写入，也就是说先清空文件，然后向内容输入111然后换行，如果为true，那么每次都在文件后面添加111然后换行，true表示增量

```java
		//创建变量
        FileOutputStream fos = null;
        OutputStreamWriter osw = null;
        BufferedWriter bw = null;
		//初始化变量
        try {
            fos = new FileOutputStream("src/main/resources/data/test.txt", true);
            osw = new OutputStreamWriter(fos, "utf-8");
            bw = new BufferedWriter(osw);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //循环写入并且换行
        for (int i = 0; i < 20; i++) {
            try {
                bw.write(String.valueOf(i));
                bw.newLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
		//关闭流
        try {
            bw.close();
            osw.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

```

# 从文件读取

## 使用BufferedReader

```java
		//创建变量
		FileInputStream fs = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
		//初始化变量
        try {
            fs = new FileInputStream("src/main/resources/data/test.txt");
            isr = new InputStreamReader(fs, "utf-8");
            br = new BufferedReader(isr);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
		//循环输出内容，然后关闭流
        try {
            String line = null;
            while ((line = br.readLine()) != null) {
                System.out.println("输出：" + line);
            }
            br.close();
            isr.close();
            fs.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
```



# SpringBoot整合文件预览

​		采用开源软件kkfile

​		官网地址：[点击进入](https://kkfileview.keking.cn/zh-cn/docs/production.html)

​		Docker 启动命令

```
docker run -itd \
--name kkfile \

-p 8012:8012 \
keking/kkfileview
```

​		使用方式：

​				通过Url即可访问。

```http
http://192.168.1.17:8012/onlinePreview?url=http://192.168.1.15:9000/test/java%E5%BC%80%E5%8F%91%E6%89%8B%E5%86%8C-%E5%B5%A9%E5%B1%B1%E7%89%88.pdf
&officePreviewType=pdf
```

