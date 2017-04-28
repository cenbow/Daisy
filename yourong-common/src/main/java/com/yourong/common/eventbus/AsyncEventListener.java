package com.yourong.common.eventbus;

import com.yourong.common.domain.AbstractBaseObject;

/**
 * 异步事件通知
 * Created by py on 2015/7/22.
 */
public interface AsyncEventListener<T extends AbstractBaseObject> {
    public  void  handle(T t);
}
