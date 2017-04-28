package com.yourong.api.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yourong.api.service.HtmlTokenService;
import com.yourong.core.uc.manager.HtmlTokenManager;
import com.yourong.core.uc.model.MemberHtmlToken;

@Service
public class HtmlTokenServiceImpl implements HtmlTokenService{

	/**
     * 日志对象
     */
    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private HtmlTokenManager htmlTokenManager;
	
    @Override
    public  void  insertSelective (MemberHtmlToken token ) {
        try {
        	htmlTokenManager.insertSelective(token);
        } catch (Exception e) {
            logger.error("htmlToken:" + token.toString() , e);
        }

    }
    @Override
    public int deleteHtmlMemberTokenByMemberID(Long memberID) {
        try {
            return htmlTokenManager.deleteHtmlMemberTokenByMemberID(memberID);
        }catch(Exception e){
            logger.error("逻辑删除htmlToken，memberID:"+memberID,e);
        }
        return  0;
    }
    
    

    @Override
    public MemberHtmlToken selectByPrimaryKey(Long id) {
        try {
            return  htmlTokenManager.selectByPrimaryKey(id);
        } catch (Exception e) {
            logger.error("id:" + id , e);
        }
        return  null;
    }
	
    @Override
    public MemberHtmlToken selectByMemberId(Long memberId) {
        try {
            return  htmlTokenManager.selectByMemberId(memberId);
        } catch (Exception e) {
            logger.error("获取htmlToken，memberId:" + memberId , e);
        }
        return  null;
    }
    
}
