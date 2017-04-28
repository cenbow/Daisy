package com.yourong.common.persistence;


import com.yourong.common.domain.AbstractBaseObject;
import org.apache.commons.lang3.ObjectUtils;
import org.mybatis.spring.support.SqlSessionDaoSupport;

import java.util.List;

/**
 *  基类DAO,crud， 分页，批量增加。
 * Created by py on 15-1-15.
 */
abstract class AbstractBaseDAO<T extends AbstractBaseObject > extends SqlSessionDaoSupport implements BaseDAO {

    protected String getNameSpace() {
        return this.getClass().getInterfaces()[0].getName();
    }

    @SuppressWarnings("unchecked")
    public T findByKey(Long id) {
        Object o = this.getSqlSession().selectOne(getNameSpace() + ".findByKey", id);
        return (T) o;
    }
    public List<T> findAll() {
        return this.getSqlSession().selectList(getNameSpace() + ".findAll");
    }

    public boolean isExisted(Long id) {
        T t = findByKey(id);
        return t == null ? false : true;
    }

    @Override
    public int insertSelective(AbstractBaseObject abstractBaseObject) {
      return   this.getSqlSession().insert(getNameSpace()+".insertSelective",abstractBaseObject);
    }

    public AbstractBaseObject selectByPrimaryKey(Long id) {
        return  this.getSqlSession().selectOne(getNameSpace() + ".selectByPrimaryKey", id);
    }

    @Override
    public int updateByPrimaryKeySelective(AbstractBaseObject abstractBaseObject) {
       return  getSqlSession().update(getNameSpace() + ".updateByPrimaryKeySelective", abstractBaseObject);
    }

    @Override
    public int updateByPrimaryKey(AbstractBaseObject abstractBaseObject) {
       return  getSqlSession().update(getNameSpace() + ".updateByPrimaryKey", abstractBaseObject);
    }


}
