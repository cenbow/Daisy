package com.yourong.core.uc.model;

import java.util.List;

import com.yourong.common.domain.AbstractBaseObject;
import com.yourong.common.util.Collections3;

public class MemberBaseBiz extends AbstractBaseObject {
   
    private static final long serialVersionUID = 5746379109990570174L;
   
    private Long id;
    /**
     * 会员
     */
    private Member member;    

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    /** 会员详细信息 */
    private MemberInfo memberInfo;
    
    /***企业信息**/
    private List<Enterprise> enterprises;
    
    
    public List<Enterprise> getEnterprises() {
        return enterprises;
    }

    public void setEnterprises(List<Enterprise> enterprises) {
        this.enterprises = enterprises;
    }

    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
    }

    

    public MemberInfo getMemberInfo() {
	return memberInfo;
    }

    public void setMemberInfo(MemberInfo memberInfo) {
	this.memberInfo = memberInfo;
    }
    
    /**
     * 获得企业信息
     * @return
     */
    public Enterprise getEnterprise() {
        if(Collections3.isNotEmpty(getEnterprises())){
        	return getEnterprises().get(0);
        }
        return null;
    }
    
}