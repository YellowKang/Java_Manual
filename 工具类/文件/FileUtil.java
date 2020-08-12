package com.kang.liquibase;


import java.io.*;

/**
 * @Author BigKang
 * @Date 2020/7/14 3:52 下午
 * @Motto 仰天大笑撸码去, 我辈岂是蓬蒿人
 * @Summarize 文件工具类
 */
public class FileUtil {


    /**
     * 复制文件
     *
     * @param source
     * @param target
     * @return
     */
    public static boolean copyFile(String source, String target) throws IOException {
        return copyFile(new File(source), target, false);
    }

    /**
     * 复制文件
     *
     * @param source
     * @param target
     * @param append
     * @return
     */
    public static boolean copyFile(String source, String target, Boolean append) throws IOException {
        return copyFile(new File(source), target, append);
    }

    /**
     * 复制文件
     *
     * @param source
     * @param target
     * @param append
     * @return
     */
    public static boolean copyFile(File source, String target, Boolean append) throws IOException {
        return copyFile(source, new File(target), append);
    }

    /**
     * 复制文件
     *
     * @param source
     * @param target
     * @return
     */
    public static boolean copyFile(File source, File target, Boolean append) throws IOException {
        InputStream inputStream = checkFileGetInputStream(source);
        if (source.getPath().equals(target.getPath())) {
            try {
                throw new IOException("Copy file source and destination cannot be the same folder!");
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                inputStream.close();
            }
        }
        return copyFile(inputStream, target, append);
    }

    /**
     * 复制文件
     *
     * @param source
     * @param target
     * @return
     */
    public static boolean copyFile(InputStream source, File target) throws IOException {
        return copyFile(source, target, false);
    }

    /**
     * 复制文件
     *
     * @param source 源文件
     * @param target 目标文件
     * @param append 是否追加
     * @return
     */
    public static boolean copyFile(InputStream source, File target, Boolean append) throws IOException {
        // 不存在则创建
        if (target != null) {
            if (!target.exists()) {
                try {
                    target.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        FileOutputStream fos = null;
        fos = new FileOutputStream(target, append);
        // 字节数组存储字节数据
        byte[] bytes = new byte[1024 * 8];
        // 创建长度
        int len = 0;
        // 循环读取数据
        while ((len = source.read(bytes)) != -1) {
            fos.write(bytes, 0, len);
        }
        if (fos != null) {
            fos.close();
        }
        source.close();
        return true;
    }

    /**
     * Resource资源文件转String
     *
     * @param filePath
     * @return
     */
    public static String resourceConvertString(String filePath) {
        checkFile(filePath);
        InputStream inputStream = FileUtil.class.getClassLoader().getResourceAsStream(filePath);
        return inputStreamConvertString(inputStream);
    }

    /**
     * 文件转为String
     *
     * @param filePath
     * @return
     */
    public static String fileConvertString(String filePath) {
        File file = new File(filePath);
        checkFile(filePath);
        return fileConvertString(file);
    }

    /**
     * 将文件转为String
     *
     * @param file
     * @return
     */
    public static String fileConvertString(File file) {
        InputStream inputStream = checkFileGetInputStream(file);
        return inputStreamConvertString(inputStream);
    }

    /**
     * inputStream流转String
     *
     * @param is
     * @return
     */
    public static String inputStreamConvertString(InputStream is) {
        StringBuffer res = new StringBuffer();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader read = new BufferedReader(isr);
        try {
            String line;
            line = read.readLine();
            while (line != null) {
                res.append(line + "\n");
                line = read.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != isr) {
                    isr.close();
                    isr.close();
                }
                if (null != read) {
                    read.close();
                    read = null;
                }
                if (null != is) {
                    is.close();
                    is = null;
                }
            } catch (IOException e) {
            }
        }
        return res.toString();
    }

    /**
     * 检查并且返回输入流
     *
     * @param file
     * @return
     */
    public static InputStream checkFileGetInputStream(File file) {
        checkFile(file);
        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 检查文件
     *
     * @param filePath
     */
    public static void checkFile(String filePath) {
        File file = new File(filePath);
        checkFile(file);
    }

    /**
     * 检查文件
     *
     * @param file
     */
    public static void checkFile(File file) {
        Boolean exists = fileExists(file);
        if (!exists) {
            try {
                throw new FileNotFoundException("文件不存在");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 文件是否存在
     *
     * @param filePath
     * @return
     */
    public static Boolean fileExists(String filePath) {
        File file = new File(filePath);
        return fileExists(file);
    }

    /**
     * 文件是否存在
     *
     * @param file
     * @return
     */
    public static Boolean fileExists(File file) {
        boolean exists = file.exists();
        if (exists) {
            return true;
        } else {
            return false;
        }
    }
}
