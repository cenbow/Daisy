package com.yourong.core.mc.manager.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.yourong.common.enums.DebtEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.common.util.BeanCopyUtil;
import com.yourong.common.util.Collections3;
import com.yourong.common.weixin.aes.Weixin;
import com.yourong.core.bsc.dao.BscAttachmentMapper;
import com.yourong.core.bsc.model.BscAttachment;
import com.yourong.core.ic.model.Debt;
import com.yourong.core.ic.model.DebtBiz;
import com.yourong.core.ic.model.DebtCollateral;
import com.yourong.core.ic.model.DebtInterest;
import com.yourong.core.ic.model.DebtPledge;
import com.yourong.core.mc.dao.WinxinTemplateMapper;
import com.yourong.core.mc.manager.WinxinTemplateManager;
import com.yourong.core.mc.model.WinxinTemplate;
import com.yourong.core.uc.model.Member;
import com.yourong.core.uc.model.MemberBaseBiz;
@Component
public class WinxinTemplateManagerImpl implements WinxinTemplateManager {

	@Autowired
	private WinxinTemplateMapper winxinMenuMapper;
	@Autowired
	private BscAttachmentMapper bscAttachmentMapper;
	@Override
	public int deleteByPrimaryKey(Long id) throws ManagerException {
		try {
			return winxinMenuMapper.deleteByPrimaryKey(id);
		} catch (Exception e) {
			throw new ManagerException(e);
		}

	}

	@Override
	public int insert(WinxinTemplate record) throws ManagerException {
		try {
			return winxinMenuMapper.insert(record);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public WinxinTemplate selectByPrimaryKey(Long id) throws ManagerException {
		try {
			return winxinMenuMapper.selectByPrimaryKey(id);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int updateByPrimaryKey(WinxinTemplate record) throws ManagerException {
		try {
			return winxinMenuMapper.deleteByPrimaryKey(record);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int updateByPrimaryKeySelective(WinxinTemplate record)
			throws ManagerException {
		try {
			return winxinMenuMapper.updateByPrimaryKeySelective(record);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int deleteBymenuId(Long id) throws ManagerException {
		try {
			return winxinMenuMapper.deleteBymenuId(id);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	public Page<WinxinTemplate> findByPage(Page<WinxinTemplate> pageRequest, Map<String, Object> map)throws ManagerException {
		try {
			return winxinMenuMapper.findByPage(pageRequest, map);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public List<WinxinTemplate> getAllWeixinMenu() {
		return winxinMenuMapper.getAllWeixinMenu();
	}

	@Override
	public String getParentMenu() throws ManagerException {/*
		List<Winxin> listParent=winxinMenuMapper.getParentMenu();
		ModelMap map = new ModelMap();
		List<Weixin> list = Lists.newArrayList();
		List<Weixin> listChild = Lists.newArrayList();
		if(listParent!=null&&listParent.size()>0){
			for(Winxin menu:listParent){
				Weixin bean=new Weixin();
				bean.setType(menu.getType());
				bean.setName(menu.getName());
				bean.setUrl(menu.getUrl());
				listChild=this.getWeixinMenu(menu.getId());
				bean.setSub_button(listChild);
				list.add(bean);
			}
		}
		map.put("button", list);
		String jsonString = JSONObject.toJSON(map).toString();
		return jsonString;
	*/
		return null;
		}
	/**
	 * 
	 * @param id
	 * @return
	 */
	public List<Weixin> getWeixinMenu(Long id) {
		List<WinxinTemplate> list=winxinMenuMapper.getChildMenu(id);
		List<Weixin> listMenu = Lists.newArrayList();
		if(list!=null&&list.size()>0){
			for(WinxinTemplate menu:list){
				Weixin bean=new Weixin();
				bean.setName(menu.getName());
				bean.setType(menu.getType());
				bean.setUrl(menu.getUrl());
				listMenu.add(bean);
			}
		}
		return listMenu;
	}

	@Override
	public Integer batchDelete(long[] ids) throws ManagerException {
		try {
			return winxinMenuMapper.batchDelete(ids);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public List<WinxinTemplate> getWeixininfo() throws ManagerException {
		try {
			return winxinMenuMapper.getWeixininfo();
		} catch (Exception e) {
			throw new ManagerException(e);
		}
		
	}

	@Override
	public int insertSelective(WinxinTemplate record) throws ManagerException {
		try {
			return winxinMenuMapper.insertSelective(record);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int updateWeixin() throws ManagerException {
		try {
			return winxinMenuMapper.updateWeixin();
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	@Override
	public WinxinTemplate queryInfobyId(long id) throws ManagerException {
		try {
			WinxinTemplate winxin = winxinMenuMapper.selectByPrimaryKey(id);
			if (winxin != null) {
				
				// 附件信息
				List<BscAttachment> bscAttachments = bscAttachmentMapper
						.findAttachmentsByKeyId(String.valueOf(winxin.getId()));
				if (!Collections3.isEmpty(bscAttachments)) {
					winxin.setBscAttachments(bscAttachments);
				}
				return winxin;
			}
			return null;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public List<WinxinTemplate> queryWeixinAtten() throws ManagerException {
		try {
			return winxinMenuMapper.queryWeixinAtten();
		} catch (Exception e) {
			throw new ManagerException(e);
		}
		
	}




}
