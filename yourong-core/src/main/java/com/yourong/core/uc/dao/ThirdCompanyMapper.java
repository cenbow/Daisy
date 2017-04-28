package com.yourong.core.uc.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.yourong.core.uc.model.ThirdCompany;
@Repository
public interface ThirdCompanyMapper {
    int deleteByPrimaryKey(Long id);

    int insert(ThirdCompany record);

    int insertSelective(ThirdCompany record);

    ThirdCompany selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ThirdCompany record);

    int updateByPrimaryKey(ThirdCompany record);

	List<ThirdCompany> getThirdCompanyList();

	ThirdCompany getCompanyByMemberId(Long underwriteMemberId);
	/**
	 * 
	 * @Description:获取第三方垫资公司id
	 * @param memberId
	 * @return
	 * @author: chaisen
	 * @time:2016年2月1日 下午4:01:09
	 */
	ThirdCompany getThirdCompanyId(@Param("interestId") Long interestId,@Param("projectId") Long projectId);
	/**
	 * 
	 * @Description:是否是垫资人
	 * @param memberId
	 * @return
	 * @author: zhanghao
	 * @time:2016年5月6日 上午11:01:09
	 */
	Integer ifAdvancer(@Param("memberId") Long memberId);

}