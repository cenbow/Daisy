package com.yourong.core.uc.dao;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.yourong.core.uc.model.AutoInvestSet;
import com.yourong.core.uc.model.ThirdCompany;
import com.yourong.core.uc.model.biz.AutoInvestMember;
@Repository
public interface AutoInvestSetMapper {
    int deleteByPrimaryKey(Long id);

    int insert(ThirdCompany record);

    int insertSelective(AutoInvestSet record);
    
    int updateByPrimaryMemberIdSelective(AutoInvestSet record);

    AutoInvestSet selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(AutoInvestSet record);

    int updateByPrimaryKey(ThirdCompany record);

	List<ThirdCompany> getThirdCompanyList();

	ThirdCompany getCompanyByMemberId(Long underwriteMemberId);

	AutoInvestSet selectByMemberId(Long memberId);
	
	AutoInvestSet countMemberIdSort(Long memberId);
	
	/**
	 * 
	 * @Description 根据自动投标开启状态和有效期时间查询有效用户
	 * @param investFlag
	 * @param startTime
	 * @param endTime
	 * @return
	 * @author luwenshan
	 * @time 2016年8月17日 下午10:26:01
	 */
	List<AutoInvestMember> selectValidMember(@Param("investFlag")Integer investFlag, @Param("startTime")Date startTime, @Param("endTime")Date endTime);
	
}