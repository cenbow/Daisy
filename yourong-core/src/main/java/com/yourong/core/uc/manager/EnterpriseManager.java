package com.yourong.core.uc.manager;

import java.util.List;
import java.util.Map;

import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.uc.model.Enterprise;

/**
 * Created by Administrator on 2015/1/6.
 */
public interface EnterpriseManager {

    int updateByPrimaryKeySelective(Enterprise record);

    int insertSelective(Enterprise record);

    Enterprise selectByKey( Long id);

    Enterprise selectByMemberID( Long memberID);

    /**根据企业名称查询企业*/
    List<Enterprise> getEnterpriseByName(String name) throws ManagerException;
    
    /**根据企业法人名称查询企业*/
    List<Enterprise> getEnterpriseByLegalName(String legalname) throws ManagerException;

	Page<Enterprise> findByPage(Page<Enterprise> pageRequest, Map<String, Object> map)throws ManagerException;

	int deleteByPrimaryKey(Long enterpriseID)throws ManagerException;
	/**
	 * 
	 * @Description:企业是否使用中
	 * @param enterpriseID
	 * @return
	 * @author: chaisen
	 * @time:2016年4月28日 下午4:26:30
	 */
	boolean checkIfUse(Long enterpriseID);
	 /**根据查询有融信息*/
	public Enterprise selectYRW() ;
	/**
	 * 
	 * @Description:根据注册号查询企业
	 * @param regisNo
	 * @return
	 * @author: chaisen
	 * @throws ManagerException 
	 * @time:2016年12月20日 上午11:29:49
	 */
	List<Enterprise> getEnterpriseByRegisNo(String regisNo) throws ManagerException;
    
    

}
