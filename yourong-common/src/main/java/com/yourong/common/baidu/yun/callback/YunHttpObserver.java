package com.yourong.common.baidu.yun.callback;


import com.yourong.common.baidu.yun.event.YunHttpEvent;

public interface YunHttpObserver {
	
	public void onHandle(YunHttpEvent event);
	
}
