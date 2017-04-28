package com.yourong.backend.ic.service;

import java.util.Map;

import com.yourong.common.pageable.Page;
import com.yourong.core.ic.model.BorrowerCredit;

/**
 * 
 * @Description 借款人授信额度信息
 * @author luwenshan
 * @time 2016年11月23日 下午3:57:23
 */
public interface BorrowerCreditService {
	
	/**
	 * 
	 * @Description 保存借款人授信额度信息
	 * @param borrowerCredit
	 * @return
	 * @author luwenshan
	 * @time 2016年11月24日 上午11:57:43
	 */
    public int saveBorrower(BorrowerCredit borrowerCredit);
    
    /**
     * 
     * @Description 根据借款人信息修改借款人授信额度信息
     * @param borrowerCredit
     * @return
     * @author luwenshan
     * @time 2016年11月24日 上午11:57:55
     */
    public int updateByBorrower(BorrowerCredit borrowerCredit);
    
    /**
     * 
     * @Description 修改借款人授信额度信息
     * @param borrowerCredit
     * @return
     * @author luwenshan
     * @time 2016年11月24日 上午11:57:55
     */
    public int updateById(BorrowerCredit borrowerCredit);
    
    /**
     * 
     * @Description 查询借款人授信额度信息
     * @param id
     * @return
     * @author luwenshan
     * @time 2016年11月24日 上午11:59:24
     */
    public BorrowerCredit selectById(Long id);
    
    /**
     * 
     * @Description 根据借款人信息查询借款人授信额度信息
     * @param borrowerId 借款人ID
     * @param borrowerType 借款人类型
     * @param openPlatformKey 渠道商类型
     * @return
     * @author luwenshan
     * @time 2016年11月24日 上午11:59:24
     */
    public BorrowerCredit selectByBorrower(Long borrowerId, Integer borrowerType, String openPlatformKey, Integer investType);
	
	/**
	 * 
	 * @Description 分页查询借款人授信额度
	 * @param pageRequest
	 * @param map
	 * @return
	 * @author luwenshan
	 * @time 2016年11月23日 下午3:58:05
	 */
	public Page<BorrowerCredit> queryBorrowerCreditByPage(Page<BorrowerCredit> pageRequest, Map<String, Object> map);
	    
}
