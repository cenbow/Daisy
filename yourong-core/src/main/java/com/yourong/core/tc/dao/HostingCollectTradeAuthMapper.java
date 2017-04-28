package com.yourong.core.tc.dao;

import java.util.List;

import com.yourong.core.tc.model.HostingCollectTradeAuth;

public interface HostingCollectTradeAuthMapper {

    HostingCollectTradeAuth selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(HostingCollectTradeAuth record);

	int batchInsert(List<HostingCollectTradeAuth> record);
	
}