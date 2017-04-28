package com.yourong.backend.uc.service.impl;

import java.text.NumberFormat;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yourong.backend.uc.service.MemberTransService;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.common.util.FormulaUtil;
import com.yourong.common.util.NumberFormatUtil;
import com.yourong.core.tc.manager.TransactionManager;
import com.yourong.core.uc.manager.MemberManager;
import com.yourong.core.uc.model.Member;
import com.yourong.core.uc.model.MemberTrans;

@Service
public class MemberTransServiceImpl implements MemberTransService {

    private static Logger logger = LoggerFactory.getLogger(MemberTransServiceImpl.class);
   
    @Autowired
    private MemberManager memberManager;
    
    @Autowired
    private TransactionManager transactionManager;

	@Override
	public MemberTrans getMemberTransInfo(Map<String, Object> map) throws Exception {
		MemberTrans m = new MemberTrans();
		if(map == null || map.size() == 0) {
			m.setSelectCode("0");
			m.setSelectMessage("请先输入用户ID或者手机号！");
			return m;
		} else {
			try {
				Page<Member> pageReq = new Page<Member>();
				pageReq.setiDisplayLength(2);
				Page<Member> page = memberManager.findByPage(pageReq, map);
				List<Member> memberList = page.getData();
				if(memberList != null && memberList.size() > 0) {
					if(memberList.size() == 1) {
						String memberText = null;
						if(map.containsKey("mobile")) {
							memberText = map.get("mobile").toString();
						} else {
							memberText = map.get("id").toString();
						}
						m.setId(memberList.get(0).getId());
						m.setSelectCode("1");
						StringBuffer selectSb = new StringBuffer();
						selectSb.append("用户ID：" + memberList.get(0).getId())
							.append("， 手机号：" + memberList.get(0).getMobile())
							.append("，投资" + transactionManager.getTransactionCountByMember(memberList.get(0).getId()))
							.append("笔，共计" + FormulaUtil.formatCurrency(transactionManager.countInvestmentAmount(memberList.get(0).getId())));
						m.setSelectMessage(selectSb.toString());
					} else {
						m.setSelectCode("2");
						m.setSelectMessage("无法定位到唯一用户！");
					}
				} else {
					m.setSelectCode("0");
					m.setSelectMessage("未查询到制定用户！");
				}
	        } catch (ManagerException e) {
	            logger.error("findByPage", e);
	            m.setSelectCode("-1");
				m.setSelectMessage("查询用户投资信息失败！");
	        }
	        return m;
		}
	}
}
