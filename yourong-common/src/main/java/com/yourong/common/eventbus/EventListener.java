package com.yourong.common.eventbus;

import com.yourong.common.domain.AbstractBaseObject;

/**
 *  事件通知
 * Created by PY on 2015/7/7.
 */
public interface EventListener<T extends AbstractBaseObject> {
    public  void  handle(T t);
}
