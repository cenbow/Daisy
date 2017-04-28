package com.yourong.web.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.yourong.common.cache.RedisPlatformClient;
import com.yourong.common.util.StringUtil;
import com.yourong.web.utils.SysServiceUtils;

/**
 * 下载controller
 * 
 * @author wangyanji
 *
 */
@Controller
@RequestMapping("download")
public class DownLoadController extends BaseController {

	/**
	 * 下载有融安卓app
	 * 
	 * @param req
	 * @param resp
	 */
	@RequestMapping("/apk")
	public void download(HttpServletRequest req, HttpServletResponse resp) {
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		InputStream is = null;
		URL httpurl = null;
		try {

			req.setCharacterEncoding("UTF-8");

			String url = SysServiceUtils.getDictValue("apk_url", "yourong_app", "");
			if (StringUtil.isNotBlank(url)) {
				httpurl = new URL(url);
				HttpURLConnection conn = (HttpURLConnection) httpurl.openConnection();// 利用HttpURLConnection对象,我们可以从网络中获取网页数据.
				conn.setDoInput(true);
				conn.connect();
				is = conn.getInputStream();

				// 设置文件输出类型
				resp.setContentType("application/octet-stream");
				resp.setHeader("Content-disposition", "attachment; filename="
						+ new String("yourongwang.apk".getBytes("utf-8"), "ISO8859-1"));
				resp.setContentLength(conn.getContentLength());//文件大小

				// 获取输入流
				bis = new BufferedInputStream(is);
				// 输出流
				bos = new BufferedOutputStream(resp.getOutputStream());
				byte[] buff = new byte[2048];
				int bytesRead;
				while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
					bos.write(buff, 0, bytesRead);
				}
				// 下载成功记录redis
				String userAgentInfo = req.getHeader("User-Agent");
				if (StringUtil.isNotBlank(userAgentInfo)) {
					RedisPlatformClient.addAppDownLoadCount(userAgentInfo, 1);
				}
			}
		} catch (Exception e) {
			// logger.error("apk下载失败", e);
		} finally {
			// 关闭流
			try {
				httpurl = null;
				if (is != null)
					is.close();
				if (bis != null)
					bis.close();
				if (bos != null)
					bos.close();
			} catch (Exception e) {
				is = null;
				bis = null;
				bos = null;
				// logger.error("apk下载报错,关闭流失败！", e);
			}
		}
	}

}
