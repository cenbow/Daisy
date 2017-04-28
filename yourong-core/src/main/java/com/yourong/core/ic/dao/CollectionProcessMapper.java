package com.yourong.core.ic.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.yourong.core.ic.model.CollectionProcess;
@Repository
public interface CollectionProcessMapper {
	
    int insertSelective(CollectionProcess record);

    CollectionProcess selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(CollectionProcess record);
    
    CollectionProcess getCollectByRepayId(Long overdueRepayId);
    
	List<CollectionProcess> getCollectListByRepayId(@Param("overdueRepayId") Long overdueRepayId);
	
	List<CollectionProcess> getCollectList(@Param("overdueRepayId") Long overdueRepayId);
	
	int countCollectionByRepayId(@Param("overdueRepayId") Long overdueRepayId);
}