package com.yourong.backend.uc.service;

import java.util.List;
import java.util.Map;

import com.yourong.common.domain.ResultDO;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.common.thirdparty.sinapay.common.domain.ResultDto;
import com.yourong.core.bsc.model.BscAttachment;
import com.yourong.core.uc.model.Enterprise;
import com.yourong.core.uc.model.Member;
import com.yourong.core.uc.model.biz.MemberBiz;
import com.yourong.core.uc.model.biz.MemberInfoBiz;

public interface MemberService {

	/**
	 * 分页查询客户信息
	 * 
	 * @param pageRequest
	 * @param map
	 * @return
	 * @throws ManagerException
	 */
	public Page<MemberBiz> findByPage(Page<MemberBiz> pageRequest,
									  Map<String, Object> map);

	/**
	 * 批量删除客户
	 * 
	 * @param ids
	 * @return
	 */
	int batchDelete(long[] ids);

	/**
	 * 根据编号获得客户
	 * 
	 * @param id
	 * @return
	 */
	Member getMemberById(Long id);

	/**
	 * 根据会员类型和会员的姓名模糊查询会员和详情列表
	 * 
	 * @param trueName
	 * @return
	 */
	List<Member> getMemberInfoByTrueName(String trueName, String memberType);
	
	/**
	 * 根据会员类型和会员的姓名/身份号码模糊查询会员和详情列表
	 * 
	 * @param trueName
	 * @param identityNumber
	 * @param memberType
	 * 
	 * @return
	 */
	List<Member> getMemberInfoByNameIdentity(String trueName, String identityNumber, String memberType);

//	/**
//	 * 后台添加会员
//	 * @param member
//	 * @return
//	 * @throws Exception
//	 */
//	public ResultDO<Object> authIdentity(Member member) throws Exception;


	/**
	 * 异步初始化其他会员注册信息
	 * @param record
	 * author: pengyong
	 * 下午5:36:42
	 */
	public void  initOtherMemberData(Member  record);

	/**
	 * 注册会员
	 * @param memberDto
	 * @return
	 * @throws Exception
	 */
	public ResultDO<Object> register(Member memberDto) throws Exception;

	/**
	 * 更换手机号码
 	 * @param id 会员ID
	 * @param newMobile 会员新手机号码
	 * @return
	 * @throws Exception
	 */
	public ResultDO<Object> changeMobile(Long id, Long newMobile) throws  Exception;
	
	
	/**
	 * 用户查询
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public List<Member> selectMember(Map<String, Object> map);
	
	/**
	 * 注销会员
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public int cancelMember(Long id) throws Exception;
	
	
	/**
	 * 保存或者更新企业信息，包括企业授信额度
	 * @param enterprise
	 * @return
	 * @throws Exception
	 */   
	public ResultDO<Enterprise> saveMemberEnterprise(Enterprise enterprise)throws Exception;
	
	/**
	 * 根据企业名称搜索企业信息
	 * @param name
	 * @return
	 */
	public List<Enterprise> getEnterpriseByName(String name);   

	/**
	 * 添加推荐人
	 * @param referredId 被推荐人ID
	 * @param referralMobile 推荐人手机号
	 * @return
	 */
	public ResultDO<Object> addReferral(Long referredId, Long referralMobile);

	/**
	 * 根据企业法人名称搜索企业信息
	 * @param name
	 * @return
	 */
	public List<Enterprise> getEnterpriseByLegalName(String legalname);   
	
	/**
	 * 
	 * @Description:只绑定新浪认证信息
	 * @param memberId
	 * @param mobile
	 * @return
	 * @author: wangyanji
	 * @time:2016年2月3日 上午10:36:01
	 */
	public Object bindingVerifyOnly(Long memberId, Long mobile);

	public ResultDto<Object> cancellationMember(Long mobile) throws Exception;
	/**
	 * 
	 * @Description:保存会员信息
	 * @param memberInfoBiz
	 * @return
	 * @author: chaisen
	 * @throws ManagerException 
	 * @time:2016年4月5日 上午10:25:55
	 */
	public Object saveUcMemberInfo(MemberInfoBiz memberInfoBiz) throws ManagerException;
	
	public boolean synThirdPayEaring(Long memberId);
	
	public List<Member> selectListByMobile(Long mobile);
	
	public ResultDO<Object> enterpriseCa(Long enterpriseId);
	
	public ResultDO<Object> saveImage(Long enterpriseId,List<BscAttachment> stampAttachment,String appPath);
	
	public Enterprise caEnterprise(Long id);

	public void memberLevelUpHandle();
	/**
	 * 
	 * @Description:根据注册号搜索企业
	 * @param regisNo
	 * @return
	 * @author: chaisen
	 * @time:2016年12月20日 上午11:28:43
	 */
	public  List<Enterprise> getEnterpriseByRegisNo(String regisNo);
	
	public Member getMemberByMobile(Long mobile);
	
}
