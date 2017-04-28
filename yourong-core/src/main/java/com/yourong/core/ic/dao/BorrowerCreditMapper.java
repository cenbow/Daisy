package com.yourong.core.ic.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.yourong.core.ic.model.BorrowerCredit;

/**
 * 
 * @Description 借款人授信额度mapper
 * @author luwenshan
 * @time 2016年11月23日 下午2:40:02
 */
public interface BorrowerCreditMapper {
	
	/**
	 * 
	 * @Description 插入借款人授信额度信息
	 * @param borrowerCredit
	 * @return
	 * @author luwenshan
	 * @time 2016年11月23日 下午2:41:09
	 */
    int insert(BorrowerCredit borrowerCredit);
    
    /**
     * 
     * @Description 修改借款人授信额度信息
     * @param borrowerCredit
     * @return
     * @author luwenshan
     * @time 2016年11月24日 上午11:36:26
     */
    int updateById(BorrowerCredit borrowerCredit);
    
    /**
     * 
     * @Description 根据借款人信息修改借款人授信额度信息
     * @param borrowerCredit
     * @return
     * @author luwenshan
     * @time 2016年11月24日 上午11:36:26
     */
    int updateByBorrower(BorrowerCredit borrowerCredit);
    
    /**
     * 
     * @Description 查询借款人授信额度信息
     * @param id
     * @return
     * @author luwenshan
     * @time 2016年11月24日 上午11:37:41
     */
    BorrowerCredit selectById(@Param("id") Long id);
    
    /**
     * 
     * @Description 根据借款人信息查询借款人授信额度信息
     * @param borrowerCredit
     * @return
     * @author luwenshan
     * @time 2016年11月24日 上午11:37:41
     */
    BorrowerCredit selectByBorrower(BorrowerCredit borrowerCredit);
    
    /**
     * 
     * @Description 查询借款人授信额度列表
     * @param map
     * @return
     * @author luwenshan
     * @time 2016年11月23日 下午4:55:53
     */
    List<BorrowerCredit> queryBorrowerCredit(@Param("map") Map<String, Object> map);
    
    /**
     * 查询借款人授信额度数量
     * @Description 
     * @param map
     * @return
     * @author luwenshan
     * @time 2016年11月23日 下午4:56:16
     */
    int queryBorrowerCreditCount(@Param("map") Map<String, Object> map);
    
    /**
     * 根据借款人信息汇总个人用户或企业用户的存续量信息
     * 
     * @param borrowerCredit
     * @return
     */
    List<BorrowerCredit> getMemberOrEnterpriseBorrowerCredit(BorrowerCredit borrowerCredit);
    
    /**
     * 根据借款人信息汇总渠道商用户的存续量信息
     * 
     * @param borrowerCredit
     * @return
     */
    List<BorrowerCredit> getChannelBorrowerCredit(BorrowerCredit borrowerCredit);
    
}