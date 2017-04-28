package com.yourong.api;

import java.util.Date;

import org.junit.Test;

import com.yourong.common.util.DateUtils;

public class DatesutilTest extends  BaseWebControllerTest {
	   @Test
	    public void testIndex() throws  Exception{

		   Date f = DateUtils.getCurrentDate();
		   Date s = DateUtils.getCurrentDate();
		   //s = DateUtils.addDate(s, 15);
		   s = DateUtils.addSecond(s, 60*60*25);
		   String result = DateUtils.betweenTwoDays(f, s);
		   System.out.print(result);
	    }



	}
