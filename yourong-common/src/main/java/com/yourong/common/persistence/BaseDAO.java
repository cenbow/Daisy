package com.yourong.common.persistence;


import com.yourong.common.domain.AbstractBaseObject;

/**
 * Created by py on 15-1-15.
 */
public interface BaseDAO<T extends AbstractBaseObject> {

   // int deleteByPrimaryKey(Long id);
    int  insert(T t);
    int  insertSelective(T t);
    T findByKey(Long id) ;
    public  boolean isExisted(Long id);
    int  updateByPrimaryKeySelective(T t);
    int  updateByPrimaryKey(T t);


}
