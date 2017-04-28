package com.yourong.core.sh.manager;

import com.yourong.common.exception.ManagerException;
import com.yourong.core.sh.model.Area;

import java.util.List;

/**
 * 查询人气值兑换商品发货地址的省、市、区
 */
public interface GoodsAddrManager {
	
    List<Area> queryAreasByParentCode(Long id) throws ManagerException;
    
}
