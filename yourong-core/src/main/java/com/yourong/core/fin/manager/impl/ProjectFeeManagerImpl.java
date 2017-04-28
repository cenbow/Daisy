package com.yourong.core.fin.manager.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import rop.thirdparty.com.google.common.collect.Lists;

import com.yourong.common.enums.ProjectEnum;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.common.util.Collections3;
import com.yourong.common.util.FormulaUtil;
import com.yourong.core.fin.dao.ProjectFeeMapper;
import com.yourong.core.fin.manager.ProjectFeeManager;
import com.yourong.core.fin.model.ProjectFee;
import com.yourong.core.ic.dao.ProjectMapper;
import com.yourong.core.ic.model.Project;
import com.yourong.core.uc.manager.MemberManager;
import com.yourong.core.uc.model.Member;

@Component
public class ProjectFeeManagerImpl implements ProjectFeeManager {
	@Autowired
	private ProjectFeeMapper projectFeeMapper;
	@Autowired 
	private ProjectMapper projectMapper;
	@Autowired
	private MemberManager memberManager;
	@Override
	public ProjectFee getManageMentFeeByProjectId(Long projectId) throws ManagerException {
		try {
			return projectFeeMapper.getManageMentFeeByProjectId(projectId);
		} catch (Exception ex) {
			throw new ManagerException(ex);
		}
	}
	@Override
	public int insertSelective(ProjectFee record) throws ManagerException {
		try {
			return projectFeeMapper.insertSelective(record);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	/**
	 * 
	 * @desc 生成管理费记录
	 * @param project
	 * @return
	 * @throws ManagerException
	 * @author chaisen
	 * @time 2016年1月12日 下午1:43:35
	 *
	 */
	@Override
	public int saveManagementFee(Project project) throws ManagerException {
		try {
			ProjectFee fee=new ProjectFee();
			fee.setProjectId(project.getId());
			fee.setDelFlag(1);
			fee.setFeeStatus(1);
			fee.setCreateTime(new Date());
			fee.setUpdateTime(new Date());
			//管理费
			fee.setAmount(FormulaUtil.getManagerAmount(project.getTotalAmount(), project.getManageFeeRate()));
			int i=this.insertSelective(fee);
			return i;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	
	/**
	 * 
	 * @desc 分页查询管理费
	 * @param pageRequest
	 * @param map
	 * @return
	 * @throws ManagerException
	 * @author zhanghao
	 * @time 2016年2月16日 下午4:09:40
	 *
	 */
	@Override
	public Page<ProjectFee> selectManageFeeForPagin(Page<ProjectFee> pageRequest, Map<String, Object> map) throws ManagerException {
		try {
			map.put("startRow", pageRequest.getiDisplayStart());
			map.put("pageSize", pageRequest.getiDisplayLength());
			List<ProjectFee> selectForPagin =Lists.newArrayList();
			int totalCount = projectFeeMapper.selectManageFeeForPaginTotalCount(map);
			if(totalCount>0){
				selectForPagin = projectFeeMapper.selectManageFeeForPagin(map);
				for(ProjectFee fee:selectForPagin){
						Member member=memberManager.selectByPrimaryKey(fee.getMemberId());
						if(member!=null){
							fee.setChargeMemberName(member.getTrueName());
						}
					}
			}
			pageRequest.setiTotalDisplayRecords(totalCount);
			pageRequest.setiTotalRecords(totalCount);
			pageRequest.setData(selectForPagin);
			return pageRequest;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	/**
	 * 
	 * @desc 根据projectId 更新服务费表
	 * @param projectId
	 * @param feeType
	 * @param feeStatus
	 * @return
	 * @throws ManagerException
	 * @author chaisen
	 * @time 2016年5月30日 上午9:25:51
	 *
	 */
	@Override
	public int updateProjectFeeByProjectId(Long projectId, int feeType, int feeStatus) throws ManagerException {
		try {
			return projectFeeMapper.updateProjectFeeByProjectId(projectId,feeType,feeStatus);
		} catch (Exception ex) {
			throw new ManagerException(ex);
		}
	}
	/**
	 * 
	 * @desc 插入服务费记录
	 * @param projectId
	 * @param feeType
	 * @return
	 * @throws ManagerException
	 * @author chaisen
	 * @time 2016年5月30日 上午9:41:44
	 *
	 */
	@Override
	public int insertProjectFee(Long projectId, int feeType ,Long memberId) throws ManagerException {
		Project project=projectMapper.selectByPrimaryKey(projectId);
		if(project!=null&&project.getInvestType()==ProjectEnum.PROJECT_TYPE_DIRECT.getType()){
			BigDecimal fee=BigDecimal.ZERO;
			//管理费
			if(TypeEnum.FEE_TYPE_MANAGE.getType()==feeType){
				fee=project.getManageFeeRate();
			}
			//风险金
			if(TypeEnum.FEE_TYPE_RISK.getType()==feeType){
				fee=project.getRiskFeeRate();
			}
			//保证金
			if(TypeEnum.FEE_TYPE_GUARANTEE.getType()==feeType){
				fee=project.getGuaranteeFeeRate();
			}
			//介绍费
			if(TypeEnum.FEE_TYPE_INTRODUCER.getType()==feeType){
				fee=project.getIntroducerFeeRate();
			}
			BigDecimal amount=FormulaUtil.getManagerAmount(project.getTotalAmount(), fee);
			
			ProjectFee projectFee=new ProjectFee();
			projectFee.setProjectId(projectId);
			projectFee.setAmount(amount);
			projectFee.setFeeType(feeType);
			projectFee.setMemberId(memberId);
			projectFee.setFeeStatus(1);
			projectFee.setDelFlag(1);
			projectFee.setCreateTime(new Date());
			int i=projectFeeMapper.insertSelective(projectFee);
			return i;
		}
		return 0;
	}
	@Override
	public ProjectFee getProjectFeeByProjectIdType(Long projectId, int feeType) throws ManagerException {
		try {
			return projectFeeMapper.getProjectFeeByProjectIdType(projectId,feeType);
		} catch (Exception ex) {
			throw new ManagerException(ex);
		}
	}
	
	
}
