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

# 创建文件夹

我们这里在boot中的resources创建了一个data目录

```java
 		File file = new File("src/main/resources/data");
        boolean mkdir = file.mkdir();
        System.out.println(mkdir ? "创建文件夹成功" : "创建文件失败，文件夹已经存在");
    
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

