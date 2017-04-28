package com.yourong.backend;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Lists;
import com.yourong.backend.ic.service.DebtService;
import com.yourong.backend.mc.service.CouponService;
import com.yourong.backend.sys.service.SysMenuService;
import com.yourong.common.thirdparty.sinapay.SinaPayClient;
import com.yourong.core.bsc.model.BscAttachment;
import com.yourong.core.ic.model.DebtBiz;
import com.yourong.core.sys.model.SysMenu;

public class MyControllerTest extends BaseWebControllerTest {
	
//	public static Logger logger = Logger.getLogger(MyControllerTest.class);

	@Autowired
	private SysMenuService sysMenuService;
	@Autowired
	private DebtService debtService;
	
	@Autowired
	private  SinaPayClient sinaPayClient;
	

	@Test
	public void testList() {
		List<SysMenu> allSysmenu = sysMenuService.getAllSysmenu();
		System.out.println(allSysmenu.size());
	}

	@Test
	public void debtTest() {
		DebtBiz debtBiz = debtService
				.getFullDebtInfoBySerialNumber("YRDE9999000001");
		System.out.println(debtBiz.toString());
	}

//	@Test
	public void debtSaveAttachment() {
		try {

			List<BscAttachment> bscAttachments = Lists.newArrayList();
			BscAttachment bscAttachment = new BscAttachment();
			bscAttachment.setFileName("uJ3ygImryo.jpg");
			bscAttachment.setFileUrl("static/upload/debt_lender/2014/9/uJ3ygImryo.jpg");
			bscAttachments.add(bscAttachment);
//			debtService.saveToAliyun(Long.valueOf("999900001"), bscAttachments,"D:\\java_workspace\\tools\\apache-tomcat-7.0.54\\wtpwebapps\\yourong-backend\\");
		} catch (Exception e) {
			
		}
	}

	@Autowired
	private CouponService couponService;


	//@Test
	public void receiveCoupon() {
//		Coupon result = couponService.receiveCoupon(110800000024L,1L);
//		System.out.println(result);
	}
}
