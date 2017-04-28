package com.yourong.core.sh.manager.impl;

import java.util.List;

import com.yourong.common.exception.ManagerException;
import com.yourong.core.sh.dao.GoodsAddrMapper;
import com.yourong.core.sh.manager.GoodsAddrManager;
import com.yourong.core.sh.model.Area;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * 
 */
@Component
public class GoodsAddrManagerImpl implements GoodsAddrManager {
    @Autowired
    private GoodsAddrMapper goodsAddrMapper;

	@Override
	public List<Area> queryAreasByParentCode(Long id) throws ManagerException{
		try {
			return goodsAddrMapper.queryAreasByParentCode(id);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
   
	}
}
