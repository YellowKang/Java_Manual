# 引入依赖

我们首先引入maven依赖，引入ZXing多格式的1D / 2D条码图像处理库

```xml
            <!--二维码生成-->
            <dependency>
                <groupId>com.google.zxing</groupId>
                <artifactId>core</artifactId>
                <version>${zxing.version}</version>
            </dependency>
```

# 编写工具类

首先我们编写工具类

```java
/**
 * @Author BigKang
 * @Date 2019/9/25 5:42 PM
 * @Summarize 二维码生成工具类
 *
 *
 * <dependency>
 * <groupId>com.google.zxing</groupId>
 * <artifactId>core</artifactId>
 * <version>${zxing.version}</version>
 * </dependency>
 */
public class QRCodeUtil {

    /**
     * 黑色
     */
    private static final int BLACK = 0xFF000000;

    /**
     * 白色
     */
    private static final int WHITE = 0xFFFFFFFF;

    /**
     * 默认长度
     */
    private static final int DEFAULT_WIDTH = 400;

    /**
     * 默认高度
     */
    private static final int DEFAULT_HEIGHT = 400;

    /**
     * 默认边距
     */
    private static final int DEFAULT_MARGIN = 1;

    /**
     * 默认编码格式
     */
    private static final String DEFAULT_CHARSET = "utf-8";

    /**
     * 默认图片类型
     */
    private static final String DEFAULT_IMG_TYPE = "png";

    /**
     * LOGO图片的默认比例按照几分之1
     */
    private static final int LOGO_PROPORTION = 4;


    /**
     * 根据url生成二维码并且返回字节数组
     *
     * @param url
     * @param width
     * @param height
     * @return
     */
    public static byte[] generateCode(String url, Integer width, Integer height, Integer margin) {
        HashMap hints = new HashMap<>();
        // 设置字符集
        hints.put(EncodeHintType.CHARACTER_SET, DEFAULT_CHARSET);
        // 设置容错等级；因为有了容错，在一定范围内可以把二维码p成你喜欢的样式,容错越高，二维码越密集
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        // 设置外边距;(即白色区域)
        hints.put(EncodeHintType.MARGIN, margin);

        BitMatrix bitMatrix = null;
        try {
            bitMatrix = new MultiFormatWriter().encode(url, BarcodeFormat.QR_CODE, width, height, hints);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        BufferedImage image = toBufferedImage(bitMatrix);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, DEFAULT_IMG_TYPE, os);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return os.toByteArray();
    }

    /**
     * 重载方法
     *
     * @param url
     * @param width
     * @param height
     * @return
     */
    public static byte[] generateCode(String url, Integer width, Integer height) {
        return generateCode(url, width, height, DEFAULT_MARGIN);
    }

    /**
     * 重载方法
     *
     * @param url
     * @return
     */
    public static byte[] generateCode(String url) {
        return generateCode(url, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    /**
     * 生成二维码并且添加Logo
     * @param url
     * @return
     */
    public static byte[] generateCodeSaveLogo(String url, Integer width, Integer height, Integer margin, InputStream logo) {
        // 生成二维码
        byte[] bytes = generateCode(url, width, height, margin);
        // 获取二维码输入流
        ByteArrayInputStream code = new ByteArrayInputStream(bytes);
        try {
            // 创建二维码图片流
            BufferedImage codeImg = ImageIO.read(code);
            // 创建Logo图片流
            BufferedImage logoImg = ImageIO.read(logo);
            // 创建画板工具
            Graphics2D image = codeImg.createGraphics();
            // 设置比例，以及xy轴
            int widthLogo = codeImg.getWidth() / LOGO_PROPORTION;
            int heightLogo = codeImg.getHeight() / LOGO_PROPORTION;
            int x = (codeImg.getWidth() - widthLogo) / 2;
            int y = (codeImg.getHeight() - heightLogo) / 2;

            // 设置颜色以及Logo放置位置
            image.drawImage(logoImg, x, y, widthLogo, heightLogo, null);
            image.drawRoundRect(x, y, widthLogo, heightLogo, 10, 10);
            image.setStroke(new BasicStroke(1));
            image.setColor(Color.WHITE);
            image.drawRect(x, y, widthLogo, heightLogo);
            image.dispose();
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(codeImg, DEFAULT_IMG_TYPE, os);
            return os.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 重载方法
     * @param url
     * @param width
     * @param height
     * @param logo
     * @return
     */
    public static byte[] generateCodeSaveLogo(String url, Integer width, Integer height, InputStream logo) {
        return generateCodeSaveLogo(url, width, height, DEFAULT_MARGIN, logo);
    }

    /**
     * 重载方法
     * @param url
     * @param logo
     * @return
     */
    public static byte[] generateCodeSaveLogo(String url, InputStream logo) {
        return generateCodeSaveLogo(url, DEFAULT_WIDTH, DEFAULT_HEIGHT, logo);
    }


    /**
     * 根据BitMatrix生成图片流
     *
     * @param matrix
     * @return
     */
    private static BufferedImage toBufferedImage(BitMatrix matrix) {
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, matrix.get(x, y) ? BLACK : WHITE);
            }
        }
        return image;
    }

}
```

# 使用controller生成

编写Controller控制器代码，这里我们采用Swagger进行测试，如果不会请参考swagger使用

http://bigkang.club/articles/2019/09/27/1569569892187.html

```java
@RestController
@RequestMapping("code")
@Api(tags = "各种码生成")
public class CodeUtilController {


    @ApiOperation("根据地址生成二维码")
    @GetMapping("qcCode")
    public void qcCode(String url, HttpServletResponse response) throws IOException{
        byte[] bytes = QRCodeUtil.generateCode(url);
        response.setContentType(ContentType.IMAGE_PNG.getMimeType());
        response.getOutputStream().write(bytes);
    }


    @ApiOperation("根据地址加上logo图片生成二维码")
    @PostMapping("qcCodeByLogo")
    public void qcCodeByLogo(String url, MultipartFile logo, HttpServletResponse response) throws IOException{
        byte[] bytes = QRCodeUtil.generateCodeSaveLogo(url, logo.getInputStream());
        response.setContentType(ContentType.IMAGE_PNG.getMimeType());
        response.getOutputStream().write(bytes);
    }

}
```

### 地址

我们访问接口文档输入我们二维码的url地址

![](https://blog-kang.oss-cn-beijing.aliyuncs.com/UTOOLS1569570491996.png)

然后查看生成的二维码

![](https://blog-kang.oss-cn-beijing.aliyuncs.com/UTOOLS1569570538542.png)



### 地址+Logo

然后我们访问接口并且进行生成,选择文件然后输入地址进行生成

![](https://blog-kang.oss-cn-beijing.aliyuncs.com/UTOOLS1569570084753.png)

然后点击执行，就能看到如下

![](https://blog-kang.oss-cn-beijing.aliyuncs.com/UTOOLS1569570134500.png)

