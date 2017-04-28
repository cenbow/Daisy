package com.yourong.common.util;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.patchca.background.BackgroundFactory;
import org.patchca.color.ColorFactory;
import org.patchca.color.RandomColorFactory;
import org.patchca.filter.ConfigurableFilterFactory;
import org.patchca.filter.library.AbstractImageOp;
import org.patchca.filter.library.WobbleImageOp;
import org.patchca.filter.predefined.WobbleRippleFilterFactory;
import org.patchca.font.RandomFontFactory;
import org.patchca.service.ConfigurableCaptchaService;
import org.patchca.text.renderer.BestFitTextRenderer;
import org.patchca.text.renderer.TextRenderer;
import org.patchca.utils.encoder.EncoderHelper;
import org.patchca.word.RandomWordFactory;

/**
 * 验证码生成器类，可生成数字、大写、小写字母及三者混合类型的验证码。 支持自定义验证码字符数量； 支持自定义验证码图片的大小； 支持自定义需排除的特殊字符；
 * 支持自定义干扰线的数量； 支持自定义验证码图文颜色
 */
public final class ValidateCode {
	 private static ConfigurableCaptchaService configurableCaptchaService = null;  
	    private static ColorFactory colorFactory = null;  
	    private static RandomFontFactory fontFactory = null;  
	    private static RandomWordFactory wordFactory = null;  
	    private static TextRenderer textRenderer = null;	 
		
	private static Random random = new Random();
	static {
		
		class MyCustomBackgroundFactory implements BackgroundFactory { 
	       // private Random random = new Random();  
	  
	        public void fillBackground(BufferedImage image) {  
	            Graphics graphics = image.getGraphics();  
	              
	            // 验证码图片的宽高  
	            int imgWidth = image.getWidth();  
	            int imgHeight = image.getHeight();  
	              
	            // 填充为白色背景  
	            graphics.setColor(Color.white);  
	            graphics.fillRect(0, 0, imgWidth, imgHeight);  
	              
	            // 画100个噪点(颜色及位置随机)  
	            for(int i = 0; i < 100; i++) {  
	                // 随机颜色  
	                int rInt = random.nextInt(255);  
	                int gInt = random.nextInt(255);  
	                int bInt = random.nextInt(255);  
	                  
	                graphics.setColor(new Color(rInt, gInt, bInt));  
	                  
	                // 随机位置  
	                int xInt = random.nextInt(imgWidth - 3);  
	                int yInt = random.nextInt(imgHeight - 2);  
	                  
	                // 随机旋转角度  
	                int sAngleInt = random.nextInt(360);  
	                int eAngleInt = random.nextInt(360);  
	                  
	                // 随机大小  
	                int wInt = random.nextInt(6);  
	                int hInt = random.nextInt(6);  
	                  
	                graphics.fillArc(xInt, yInt, wInt, hInt, sAngleInt, eAngleInt);  
	                  
//	                // 画5条干扰线  
	                if (i % 20 == 0) {  
	                    int xInt2 = random.nextInt(imgWidth);  
	                    int yInt2 = random.nextInt(imgHeight);  
	                    graphics.drawLine(xInt, yInt, xInt2, yInt2);  
	                }  
	            }  
	        }  
	      }	
		configurableCaptchaService = new ConfigurableCaptchaService();  
        
        // 颜色创建工厂,使用一定范围内的随机色  
        colorFactory = new RandomColorFactory();  
        configurableCaptchaService.setColorFactory(colorFactory);  
          
        // 随机字体生成器  
        fontFactory = new RandomFontFactory();  
        fontFactory.setMaxSize(25);  
        fontFactory.setMinSize(25);  
        
        configurableCaptchaService.setFontFactory(fontFactory);  
          
        // 随机字符生成器,去除掉容易混淆的字母和数字,如o和0等  
        wordFactory = new RandomWordFactory();  
        wordFactory.setCharacters("ABCDEFGHKMNPQSTWXY2345678");  
        wordFactory.setMaxLength(4);  
        wordFactory.setMinLength(4);  
        configurableCaptchaService.setWordFactory(wordFactory);  
          
        // 自定义验证码图片背景  
//        MyCustomBackgroundFactory backgroundFactory = new MyCustomBackgroundFactory();  
//        configurableCaptchaService.setBackgroundFactory(backgroundFactory);  
          
        // 图片滤镜设置  
        ConfigurableFilterFactory filterFactory = new ConfigurableFilterFactory();  
          
        List<BufferedImageOp> filters = new ArrayList<BufferedImageOp>();  
        WobbleImageOp wobbleImageOp = new WobbleImageOp();  
        wobbleImageOp.setEdgeMode(AbstractImageOp.EDGE_MIRROR);  
        wobbleImageOp.setxAmplitude(2.0);  
        wobbleImageOp.setyAmplitude(1.0);  
        filters.add(wobbleImageOp);  
        
        WobbleRippleFilterFactory    wrff  = new WobbleRippleFilterFactory();
        filterFactory.setFilters(filters);  
          
        configurableCaptchaService.setFilterFactory(wrff);  
          
        // 文字渲染器设置  
        textRenderer = new BestFitTextRenderer();  
        textRenderer.setBottomMargin(3);  
        textRenderer.setTopMargin(3);         
        configurableCaptchaService.setTextRenderer(textRenderer);  
          
        // 验证码图片的大小  
        configurableCaptchaService.setWidth(90);  
        configurableCaptchaService.setHeight(30);  
		
	}

	public static String getChallangeAndWriteImage(String photoType,
			OutputStream output) throws Exception {
//		switch (random.nextInt(5)) {
//		case 0:
//			configurableCaptchaService.setFilterFactory(new CurvesRippleFilterFactory(configurableCaptchaService
//					.getColorFactory()));
//			break;
//		case 1:
//			configurableCaptchaService.setFilterFactory(new MarbleRippleFilterFactory());
//			break;
//		case 2:
//			configurableCaptchaService.setFilterFactory(new DoubleRippleFilterFactory());
//			break;
//		case 3:
//			configurableCaptchaService.setFilterFactory(new WobbleRippleFilterFactory());
//			break;
//		case 4:
//			configurableCaptchaService.setFilterFactory(new DiffuseRippleFilterFactory());
//			break;
//		}
		//configurableCaptchaService.setFilterFactory(new CurvesRippleFilterFactory(configurableCaptchaService.getColorFactory()));
	    	configurableCaptchaService.setFilterFactory(new WobbleRippleFilterFactory());

		String token = EncoderHelper.getChallangeAndWriteImage(configurableCaptchaService, photoType,output);
		return token;
	}
	
}