package com.yourong.common.baidu.yun.callback;

import com.yourong.common.baidu.yun.event.YunHttpEvent;

import java.util.List;



public interface YunHttpObservable {
	
	public void addHttpCallback(YunHttpObserver callback);
	
	public void addBatchHttpCallBack(List<YunHttpObserver> callbacks);
	
	public void removeCallBack(YunHttpObserver callback);
	
	public void notifyAndCallback(YunHttpEvent event);

}
