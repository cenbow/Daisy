package com.yourong.common.util;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.renderable.ParameterBlock;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.HashMap;



public class ImageUtil {
//	private static float alpha = 0.8f;
//	private static RenderingHints hints = new RenderingHints(new HashMap());
//	private static float encodeQuality = 0.95f;
//
//	static {
//		//用于设置处理图片的参数，可提升图片的质量
//		hints.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//		hints.put(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
//		hints.put(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
//		hints.put(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
//		hints.put(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
//		hints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
//		System.setProperty("com.sun.media.jai.disableMediaLib", "true");
//	}
//
//	/**
//	 * 使用默认的图片水印
//	 * @param srcImage 图片对象
//	 * @return
//	 * @throws IOException
//	 */
//	public static BufferedImage addImageWatermark(BufferedImage srcImage) throws IOException {
//		BufferedImage watermarkImage = ImageIO.read(new File(getDefaultWatermarkImagePath()));
//		Graphics2D g = srcImage.createGraphics();
//		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
//		g.drawImage(watermarkImage, (srcImage.getWidth()-watermarkImage.getWidth()), (srcImage.getHeight() - watermarkImage.getHeight()), null);
//		g.dispose();
//		return srcImage;
//	}
//
//	/**
//	 * 自定义图片水印
//	 * @param srcImage
//	 * @param watermarkImagePath
//	 * @return
//	 * @throws IOException
//	 */
//	public static BufferedImage addImageWatermark(BufferedImage srcImage, String watermarkImagePath) throws IOException {
//		BufferedImage watermarkImage = ImageIO.read(new File(watermarkImagePath));
//		Graphics2D g = srcImage.createGraphics();
//		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
//		g.drawImage(watermarkImage, (srcImage.getWidth()-watermarkImage.getWidth()), (srcImage.getHeight() - watermarkImage.getHeight()), null);
//		g.dispose();
//		return srcImage;
//	}
//
//
//	/**
//	 * 添加图片水印
//	 * @param imagePath 图片路径
//	 * @return
//	 * @throws IOException
//	 */
//	public static BufferedImage addImageWatermark(String imagePath) throws IOException {
//		return addImageWatermark(ImageIO.read(new File(imagePath)));
//	}
//
//	/**
//	 * 裁剪图片
//	 * @param srcFile 原文件
//	 * @param newFile 新文件
//	 * @param requiredWidth 宽度
//	 * @param requiredHeight 高度
//	 */
//	public static void scaleImage(String srcFile, String newFile, int requiredWidth, int requiredHeight) {
//		OutputStream os = null;
//		FileSeekableStream stream = null;
//		try {
//
//		   stream = new FileSeekableStream(srcFile);
//           PlanarImage srcImage = JAI.create("stream", stream);
//
//			// 2.计算宽、高、ScaleFactor（缩放比率）等
//			int originalWidth = srcImage.getWidth();
//			int originalHeight = srcImage.getHeight();
//			BigDecimal xScaleFactor = BigDecimal.valueOf(1L);
//			BigDecimal yScaleFactor = BigDecimal.valueOf(1L);
//			if (requiredWidth != 0 && requiredWidth < originalWidth) {
//				xScaleFactor = (new BigDecimal(requiredWidth)).divide(new BigDecimal(originalWidth), 10, 0);
//			}
//			if (requiredHeight != 0 && requiredHeight < originalHeight) {
//				yScaleFactor = (new BigDecimal(requiredHeight)).divide(new BigDecimal(originalHeight), 10, 0);
//			}
//
//			// 3.对图片进行缩放
//			PlanarImage scaledImage = null;
//			if (xScaleFactor.compareTo(yScaleFactor) == -1) {
//				scaledImage = doImageScaling(srcImage, xScaleFactor.doubleValue());
//			} else if (xScaleFactor.compareTo(yScaleFactor) == 1) {
//				scaledImage = doImageScaling(srcImage, yScaleFactor.doubleValue());
//			} else if (xScaleFactor.compareTo(yScaleFactor) == 0 && xScaleFactor.compareTo(new BigDecimal("0")) == 1) {
//				scaledImage = doImageScaling(srcImage, yScaleFactor.doubleValue());
//			} else {
//				scaledImage = srcImage;
//			}
//
//			// 4.保存已缩放的图片,格式由后缀自动决定，但注意不能支持GIF
//			String imageType = getImageType(newFile);
//			os = new FileOutputStream(newFile);
//			ImageEncoder encoder = ImageCodec.createImageEncoder(imageType, os,getEncodeParams(imageType));
//			encoder.encode(scaledImage);
//			srcImage.getAsBufferedImage().flush();
//			scaledImage.getAsBufferedImage().flush();
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			IOUtils.closeQuietly(stream);
//			IOUtils.closeQuietly(os);
//		}
//	}
//
//	/**
//	 * 获得图片的编辑参数
//	 * @param imageType
//	 * @return
//	 */
//	private static ImageEncodeParam getEncodeParams(String imageType) {
//		if ("jpeg".equals(imageType)) {
//			JPEGEncodeParam _encodeParam = new JPEGEncodeParam();
//			_encodeParam.setQuality(encodeQuality);
//			return _encodeParam;
//		}
//		return null;
//	}
//
//	/**
//	 * 获得图片类型
//	 * @param imagePath
//	 * @return
//	 */
//	protected static String getImageType(String imagePath) {
//		String imageType = FilenameUtils.getExtension(imagePath).toLowerCase();
//		if ("jpg".equals(imageType)) {
//			imageType = "jpeg";
//		}
//		return imageType;
//	}
//
//	/**
//	 * 裁剪图片
//	 * @param srcImage
//	 * @param scaleFactor
//	 * @return
//	 */
//	private static PlanarImage doImageScaling(PlanarImage srcImage, double scaleFactor) {
//		ParameterBlock params = new ParameterBlock();
//		params.addSource(srcImage);
//		params.add(scaleFactor);
//		return JAI.create("SubsampleAverage", params, hints);
//	}
//
//	/**
//	 * 获得默认图片水印地址
//	 * @return
//	 */
//	public static String getDefaultWatermarkImagePath() {
//		return SpringContextHolder.getRootRealPath()+File.separator+PropertiesUtil.getProperties("defaultWatermarkImagePath");
//	}
//
//	/**
//	 * 根据坐标裁剪图片
//	 * @param filePath 图片路径
//	 * @param newFilePath 裁剪后的新图片路径
//	 * @param x
//	 * @param y
//	 * @param width
//	 * @param height
//	 * @return
//	 * @throws IOException
//	 */
//	public static void scaleImage(String filePath, String newFilePath, int x, int y, int width, int height) throws IOException{
//		ImageFilter cropFilter;
//		BufferedImage src = ImageIO.read(new File(filePath));
//		cropFilter = new CropImageFilter(x, y, width, height);
//		Image  img = Toolkit.getDefaultToolkit().createImage(new FilteredImageSource(src.getSource(), cropFilter));
//		BufferedImage bufImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
//        Graphics g = bufImage.getGraphics();
//        g.drawImage(img, 0, 0, null);
//        g.dispose();
//        File newFile = new File(newFilePath);
//        ImageIO.write(bufImage, "JPEG",newFile);
//	}
//
}
