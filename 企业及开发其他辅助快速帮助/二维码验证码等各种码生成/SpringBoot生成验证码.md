# 验证码生成流程

​			如果使用Java后台来生成的话我们知道，验证码肯定是一张图片，那么这个图片就是我们的图片流，然后我们应该创建一个画板工具，在上面随机生成验证码字母，然后把它画到画板上，并且使用不同的颜色位置，以及干扰点和线条，生成了之后我们还需要将这个画板关闭，然后我们将画板数据转为图片流然后返回给前端进行展示，大体思路就是如下，我们还需要一个对象来接受存储这些数据，保存我们的流以及二维码的值。我们同时还要支持自定义验证码比如个数，图片宽高等等。

# 引入依赖

这里我们使用了lang3的工具包所以需要引入一下，也能使用其他工具

```xml
        <!--lang3工具类-->
        <dependency>
            <groupId>org.apache.commons</groupId>
            <artifactId>commons-lang3</artifactId>
            <version>3.8.1</version>
        </dependency>
```



# 编写工具类

### 编写接收实体

```java

import lombok.Data;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;

/**
 * @Author BigKang
 * @Date 2019/9/17 3:35 PM
 * @Summarize 验证码返回Vo
 */
@Data
public class VerificationCode implements Serializable {

    /**
     * 返回的验证码
     */
    private String code;

    /**
     * 返回的字节流
     */
    private byte[] bytes;

    /**
     * 返回的输出流
     */
    private InputStream inputStream;


    public VerificationCode(String code, byte[] bytes){
        this.code = code;
        this.bytes = bytes;
    }

    /**
     * getInputStream根据字节转换为输入流
     * @return
     */
    public InputStream getInputStream(){
        if(bytes == null || bytes.length <= 0){
            return null;
        }
        return new ByteArrayInputStream(bytes);
    }
}

```

### 编写实体类

```java
import com.kang.shop.common.util.entity.VerificationCode;
import org.apache.commons.lang3.StringUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;

/**
 * @Author BigKang
 * @Date 2019/9/17 3:45 PM
 * @Summarize 验证码生成工具类
 */
public class VerifyCodeUtil {

    /**
     * 默认宽度
     */
    private static final int DEFAULT_WIDTH = 500;

    /**
     * 默认高度
     */
    private static final int DEFAULT_HEIGHT = 300; 

    /**
     * 默认验证码数量
     */
    private static final int DEFAULT_CODE_COUNT = 5; 
  
    /**
     * 干扰线条数量
     */
    private static final int LINE_ROW = 20;

    /**
     * 图片类型
     */
    private static final String IMG_TYPE = "png";

    /**
     * 码表，随机文字
     */
    private static final char[] BASECODE = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h',
            'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u',
            'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H',
            'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U',
            'V', 'W', 'X', 'Y', 'Z', '0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9'};

    /**
     * 背景颜色
     */
    private static final String BACKGROUND_COLOR = "F8F8F8";

    /**
     * 生成验证码基础方法，指定验证码宽度，高度，数量，以及验证码值生成
     * @param width      宽度
     * @param height     高度
     * @param codeCount  编码数量
     * @param assignCode 指定验证码
     * @return
     */
    public static VerificationCode getVerifyImg(int width, int height, int codeCount, String assignCode) {
        Random random = new Random();

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics graphic = image.getGraphics();

        graphic.setColor(Color.getColor(BACKGROUND_COLOR));
        graphic.fillRect(0, 0, width, height);

        // 在 "画板"上生成干扰线条
        for (int i = 0; i < LINE_ROW; i++) {
            // 随机生成画笔颜色
            graphic.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
            final int x = random.nextInt(width);
            final int y = random.nextInt(height);
            final int w = random.nextInt(width);
            final int h = random.nextInt(height);
            graphic.drawLine(x, y, w, h);
        }

        StringBuilder code = new StringBuilder();

        // 在 "画板"上绘制字母
        graphic.setFont(new Font("Comic Sans MS", Font.BOLD, height / 2));

        if (StringUtils.isNotEmpty(assignCode)) {
            char[] chars = assignCode.toCharArray();
            for (int i = 0; i < assignCode.length(); i++) {
                code.append(String.valueOf(chars[i]).toLowerCase());
                graphic.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
                graphic.drawString(String.valueOf(chars[i]), i * (width / 6), height - (height / 3));
            }
        } else {
            for (int i = 0; i < codeCount; i++) {
                String s = String.valueOf(BASECODE[random.nextInt(BASECODE.length)]);
                code.append(s.toLowerCase());
                graphic.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
                graphic.drawString(s, i * (width / 6), height - (height / 3));
            }
        }


        // 设置边框
        graphic.setColor(Color.BLACK);
        graphic.drawRect(1, 1, width - 2, height - 2);
        graphic.dispose();

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, IMG_TYPE, os);
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte b[] = os.toByteArray();
        return new VerificationCode(code.toString(), b);
    }

    /**
     * 重载方法，指定宽度高度和验证码值生成
     * @param width      宽度
     * @param height     高度
     * @param assignCode 验证码
     * @return
     */
    public static VerificationCode getVerifyImg(int width, int height, String assignCode) {
        return getVerifyImg(width, height, DEFAULT_CODE_COUNT, assignCode);
    }

    /**
     * 重载方法，指定宽度高度以及验证码个数生成
     * @param width      宽度
     * @param height     高度
     * @return
     */
    public static VerificationCode getVerifyImg(int width, int height, int count) {
        return getVerifyImg(width, height,count,null);
    }

    /**
     * 重载方法，指定验证码生成
     * @param assignCode 验证码
     * @return
     */
    public static VerificationCode getVerifyImg(String assignCode) {
        return getVerifyImg(DEFAULT_WIDTH, DEFAULT_HEIGHT, DEFAULT_CODE_COUNT, assignCode);
    }

    /**
     * 重载方法，指定宽度高度生成
     * @param width  宽度
     * @param height 高度
     * @return
     */
    VerificationCode getVerifyImg(int width, int height) {
        return getVerifyImg(DEFAULT_WIDTH, DEFAULT_HEIGHT, null);
    }

    /**
     * 重载方法，指定验证码个数生成
     * @param count 验证码数量
     * @return
     */
    public static VerificationCode getVerifyImg(int count) {
        return getVerifyImg(DEFAULT_WIDTH, DEFAULT_HEIGHT,count);
    }

    /**
     * 重载方法，随机生成验证码
     * @return
     */
    public static VerificationCode getVerifyImg() {
        return getVerifyImg(DEFAULT_CODE_COUNT);
    }
}
```

# 编写使用控制器

这里使用swagger2进行接口文档测试，如果不会请参考swagger使用

http://bigkang.club/articles/2019/09/27/1569569892187.html

```java
@RestController
@RequestMapping("code")
@Api(tags = "各种码生成")
public class CodeUtilController {

    @ApiOperation("验证码生成")
    @GetMapping("authCode")
    public void authCode(HttpServletResponse response) throws IOException {
        byte[] bytes = VerifyCodeUtil.getVerifyImg().getBytes();
        response.setContentType(ContentType.IMAGE_PNG.getMimeType());
        response.getOutputStream().write(bytes);
    }

    @ApiOperation("指定验证码生成")
    @GetMapping("authCodeByCode")
    public void authCodeByCode(String code,HttpServletResponse response) throws IOException {
        byte[] bytes = VerifyCodeUtil.getVerifyImg(code).getBytes();
        response.setContentType(ContentType.IMAGE_PNG.getMimeType());
        response.getOutputStream().write(bytes);
    }

    @ApiOperation("指定验证码个数生成")
    @GetMapping("authCodeByCount")
    public void authCodeByCount(Integer count,HttpServletResponse response) throws IOException {
        byte[] bytes = VerifyCodeUtil.getVerifyImg(count).getBytes();
        response.setContentType(ContentType.IMAGE_PNG.getMimeType());
        response.getOutputStream().write(bytes);
    }

}
```

# 测试使用

### 随机生成验证码

我们打开Swagger,找到验证码生成

![](https://blog-kang.oss-cn-beijing.aliyuncs.com/UTOOLS1569573382039.png)

然后我们点击生成，即可看到相应的图片

![](https://blog-kang.oss-cn-beijing.aliyuncs.com/UTOOLS1569573411741.png)

### 指定验证码生成

我们输入指定验证码然后生成

![](https://blog-kang.oss-cn-beijing.aliyuncs.com/UTOOLS1569573487690.png)

然后点击执行

![](https://blog-kang.oss-cn-beijing.aliyuncs.com/UTOOLS1569573501392.png)

### 指定验证码个数

我们输入指定的验证码个数

![](https://blog-kang.oss-cn-beijing.aliyuncs.com/UTOOLS1569573554521.png)

然后点击执行

![](https://blog-kang.oss-cn-beijing.aliyuncs.com/UTOOLS1569573589539.png)