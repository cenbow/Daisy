package com.yourong.core.msg.manager.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.yourong.common.domain.ResultDO;
import com.yourong.common.http.common.HttpRequest;
import com.yourong.common.http.common.HttpResponse;
import com.yourong.common.http.common.Method;
import com.yourong.common.util.PropertiesUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.msg.dao.CustomMessageMapper;
import com.yourong.core.msg.manager.CustomMessageManager;
import com.yourong.core.msg.model.CustomMessage;
import com.yourong.core.sys.dao.SysUserMapper;
import com.yourong.core.sys.model.SysUser;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

@Component
public class CustomMessageManagerImpl implements CustomMessageManager {
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private CustomMessageMapper customMessageMapper;
	@Autowired
	private SysUserMapper sysUserMapper ;

	@Override
	public int insert(CustomMessage customMessage) throws ManagerException {
		return customMessageMapper.insert(customMessage);
	}

	@Override
	public void update(CustomMessage customMessage) throws ManagerException {
		customMessageMapper.updateByPrimaryKeySelective(customMessage);
	}

	@Override
	public int delete(Long id) throws ManagerException {
		return customMessageMapper.deleteByPrimaryKey(id);
		
	}

	@Override
	public CustomMessage select(Long id) throws ManagerException {
		return customMessageMapper.selectByPrimaryKey(id);
	}
	
	@Override
	public int submitReview(Long id) {
		return customMessageMapper.submitReview(id);
	}

	@Override
	public int approval(Long id, Long auditId, String auditMessage) {
		return customMessageMapper.approval(id, auditId, auditMessage);
	}

	@Override
	public int disallowance(Long id, Long auditId, String auditMessage) {
		return customMessageMapper.disallowance(id, auditId, auditMessage);
	}

	@Override
	public Page<CustomMessage> findByPage(Page<CustomMessage> pageRequest,
			Map<String, Object> map) throws ManagerException {
		try {
			map.put("startRow", pageRequest.getiDisplayStart());
			map.put("pageSize", pageRequest.getiDisplayLength());
			int totalCount = customMessageMapper.selectForPaginTotalCount(map);
			pageRequest.setiTotalDisplayRecords(totalCount);
			pageRequest.setiTotalRecords(totalCount);
			List<CustomMessage> selectForPagin = Lists.newArrayList();
			if(totalCount > 0){
				selectForPagin = customMessageMapper.selectForPagin(map);
				for(CustomMessage customMessage : selectForPagin ){
					SysUser SysUser = sysUserMapper.selectByPrimaryKey(customMessage.getCreatorId());
					customMessage.setCreatorName(SysUser.getName());
				}
			}
			pageRequest.setData(selectForPagin);
			return pageRequest;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public List<CustomMessage> queryUnreadMessages(Long memberId) throws ManagerException {
		try {
			return customMessageMapper.queryUnreadMessages(memberId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	@Override
	public List<CustomMessage> queryUnreadMessagesByType(Long memberId,Integer msgType) throws ManagerException {
		try {
			return customMessageMapper.queryUnreadMessagesByType(memberId,msgType);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int updateMessageStatusTo4() throws ManagerException {
		try {
			return customMessageMapper.updateMessageStatusTo4();
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int cancel(Long id) throws ManagerException {
		try {
			return customMessageMapper.cancel(id);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public boolean sendMessage(String mobilestr,String content) {
		String url= PropertiesUtil.getProperties("sudun.api");
		Map<String,String> map=new HashMap<>();

		String userid=PropertiesUtil.getProperties("sudun.userid");
		String account=PropertiesUtil.getProperties("sudun.account");
		String password=PropertiesUtil.getProperties("sudun.password");
		map.put("userid",userid);
		map.put("account",account);
		map.put("password",password);
		map.put("mobile",mobilestr);
		map.put("content",content);
		map.put("sendTime","");
		map.put("action","send");
		map.put("extno","");
		HttpResponse res= requestSudun(url,map);
		String str= tostring(res.getInputStream());
		String returnstatus= getNodeValue(str,"returnstatus");
		String message= getNodeValue(str,"message");
		if ("Success".equals(returnstatus)&&"ok".equals(message)){
			//logger.info("发送短信提醒 渠道商:速盾  mobile:"+mobilestr+" message:"+content);
			return true;
		}else{
			logger.error("发送短信失败 渠道商:速盾  mobile:"+mobilestr+" message:"+content);
			return false;
		}
	}

	/**
	 * 请求速盾
	 * @param map
	 * @return
	 */
	private static HttpResponse requestSudun(String url, Map<String, String> map){
		HttpRequest httpRequest=new HttpRequest(url);
		if (!map.isEmpty()){
			for (String key:map.keySet()) {
				httpRequest.addParam(key,map.get(key));
			}
		}
		httpRequest.setMethod(Method.POST);
		HttpResponse httpResponse=null;
		try {
			httpResponse= httpRequest.exeute();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return httpResponse;
	}

	/**
	 * 解析InputStream
	 * @param stream
	 * @return
	 */
	private static String tostring(InputStream stream){
		StringBuffer bs = new StringBuffer();
		try {
			BufferedReader buffer = new BufferedReader(new InputStreamReader(stream,"utf-8"));
			String l = null;
			while((l=buffer.readLine())!=null){
				bs.append(l);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bs.toString();
	}

	/**
	 * 速盾xml解析获取节点数据
	 * @param xmlstr
	 * @param tagName
	 * @return
	 */
	private static String getNodeValue(String xmlstr,String tagName){
		String value="";
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = dbf.newDocumentBuilder();
			Document doc = builder.parse(new ByteArrayInputStream(xmlstr.getBytes("utf-8")));
			Element element= doc.getDocumentElement();
			NodeList nodeList = element.getElementsByTagName(tagName);
			Node node=nodeList.item(0);
			Node s= node.getFirstChild();
			value= s.getNodeValue();
		} catch (ParserConfigurationException e1) {
			e1.printStackTrace();
		} catch (SAXException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return value;
	}
}
