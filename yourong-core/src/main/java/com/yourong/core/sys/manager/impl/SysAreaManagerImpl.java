package com.yourong.core.sys.manager.impl;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.sys.dao.SysAreaMapper;
import com.yourong.core.sys.manager.SysAreaManager;
import com.yourong.core.sys.model.SysArea;

@Service
public class SysAreaManagerImpl implements SysAreaManager {
	@Autowired
	private SysAreaMapper sysAreaMapper;

	public int deleteByPrimaryKey(Long id) throws ManagerException {
		try {
			int result = sysAreaMapper.deleteByPrimaryKey(id);
			return result;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	public int insert(SysArea sysArea) throws ManagerException {
		try {
			int result = sysAreaMapper.insert(sysArea);
			return result;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	public SysArea selectByPrimaryKey(Long id) throws ManagerException {
		try {
			return sysAreaMapper.selectByPrimaryKey(id);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	public int updateByPrimaryKey(SysArea sysArea) throws ManagerException {
		try {
			return sysAreaMapper.updateByPrimaryKey(sysArea);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	public int updateByPrimaryKeySelective(SysArea sysArea)
			throws ManagerException {
		try {
			return sysAreaMapper.updateByPrimaryKeySelective(sysArea);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	public int batchDelete(long[] ids) throws ManagerException {
		try {
			return sysAreaMapper.batchDelete(ids);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	public Page<SysArea> findByPage(Page<SysArea> pageRequest,
			Map<String, Object> map) throws ManagerException {
		try {
			return sysAreaMapper.findByPage(pageRequest, map);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public List<SysArea> getAllSysArea() throws ManagerException {
		try {
			return sysAreaMapper.selectAllSysArea();
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public List<SysArea> getSysAreasByType(Integer type)
			throws ManagerException {
		try {
			return sysAreaMapper.selectSysAreasByType(type);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public List<SysArea> getSysAreasByParentId(Long parentId) throws ManagerException {
		try {
			return sysAreaMapper.selectSysAreasByParentId(parentId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}


	@Override
	public Long getParentIdByCode(String code) throws ManagerException {
		try {
			return sysAreaMapper.getParentIdByCode(code);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public List<Long> getParentIdsByCode(Long code) throws ManagerException {
		List<Long> list = Lists.newArrayList();
		if(code==null){
			return list;
		}
		try {
			if(code == 1){
				list.add(code);
			}else{
				list.add(code);
				Long id = getParentIdByCode(code.toString());
				list.add(id);
				while(id != 1){
					id = getParentIdByCode(id.toString());
					list.add(id);
				}
			}
			Collections.reverse(list);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
		return list;
	}
}