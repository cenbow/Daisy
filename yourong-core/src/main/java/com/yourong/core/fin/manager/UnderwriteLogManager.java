package com.yourong.core.fin.manager;

import com.yourong.common.exception.ManagerException;
import com.yourong.core.fin.model.UnderwriteLog;
import com.yourong.core.ic.model.ProjectInterestBiz;


public interface UnderwriteLogManager {
	
	int insertSelective(UnderwriteLog record) throws ManagerException;
	
	public  UnderwriteLog  getUnderwriteLogByInterestId(Long interestId) throws ManagerException;
	/**
	 * 
	 * @Description:保存垫资
	 * @param project
	 * @return
	 * @throws ManagerException
	 * @author: chaisen
	 * @time:2016年1月29日 下午2:21:01
	 */
	int saveUnderWriteLog(ProjectInterestBiz project) throws ManagerException;
	
	/**
	 * 
	 * @Description:根据项目本息id更新垫资表
	 * @param interestId
	 * @return
	 * @throws ManagerException
	 * @author: chaisen
	 * @time:2016年2月22日 下午5:09:10
	 */
	int updateUnderWriteLogByInterestId(Long interestId)throws ManagerException;

	/**
	 * @Description:更新垫资记录状态 根据垫资记录id
	 * @param status
	 * @param status2
	 * @param underwriteId
	 * @author: fuyili
	 * @time:2016年2月29日 下午2:29:01
	 */
	int updateForOverdueRepaySuccess(int beforeStatus, int afterStatus, String remarks,Long underwriteId)throws ManagerException;
	/**
	 * 
	 * @Description:更新垫资记录
	 * @param interestId
	 * @return
	 * @throws ManagerException
	 * @author: chaisen
	 * @time:2016年3月10日 上午11:16:33
	 */
	int updateUnderWriteByInterestId(Long interestId) throws ManagerException;

	/**删除垫资记录
	 * @param interestId
	 * @return
	 */
	int deleteByInterestId(Long interestId)throws ManagerException;
	
}
