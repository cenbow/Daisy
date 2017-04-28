package com.yourong.core.fin.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.yourong.core.fin.model.UnderwriteLog;
@Repository
public interface UnderwriteLogMapper {
	
    int insertSelective(UnderwriteLog record);

    UnderwriteLog selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UnderwriteLog record);

	UnderwriteLog getUnderwriteLogByInterestId(@Param("interestId")Long interestId);
	
	/**
	 * 
	 * @Description:根据本息id 获取垫资记录
	 * @param interestId
	 * @return
	 * @author: chaisen
	 * @time:2016年2月22日 下午5:17:01
	 */
	int updateUnderWriteLogByInterestId(@Param("interestId")Long interestId);
	
	/**
	 * @Description:垫资还款成功之后更新垫资记录 根据垫资记录id
	 * @param status
	 * @param status2
	 * @param underwriteId
	 * @author: fuyili
	 * @time:2016年2月29日 下午2:29:01
	 */
	int updateForOverdueRepaySuccess(@Param("beforeStatus") int beforeStatus, @Param("afterStatus")int afterStatus, @Param("remarks")String remarks,@Param("underwriteId")Long underwriteId);
	/**
	 * 
	 * @Description:更新垫资记录
	 * @param interestId
	 * @return
	 * @author: chaisen
	 * @time:2016年3月10日 上午11:16:57
	 */
	int updateUnderWriteByInterestId(@Param("interestId")Long interestId);
	
	/**
	 * @Description:查找已垫资未添加逾期记录的垫资记录
	 * @return
	 * @author: fuyili
	 * @time:2016年6月1日 下午8:03:06
	 */
	List<UnderwriteLog> findUnderwriteNoOverdue();
	/**
	 * 删除垫资记录
	 * @param interestId
	 * @return
	 */
	int deleteByInterestId(@Param("interestId")Long interestId);

}