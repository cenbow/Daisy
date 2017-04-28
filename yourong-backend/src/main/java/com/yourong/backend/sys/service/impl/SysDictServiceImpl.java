package com.yourong.backend.sys.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yourong.backend.sys.service.SysDictService;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.common.thirdparty.sinapay.common.domain.ResultDto;
import com.yourong.common.thirdparty.sms.SmsMobileSend;
import com.yourong.core.mc.model.CouponTemplate;
import com.yourong.core.sys.manager.SysDictManager;
import com.yourong.core.sys.model.SysDict;

@Service
public class SysDictServiceImpl  implements SysDictService {
	
	
	private static Logger logger = LoggerFactory.getLogger(SysDictServiceImpl.class);

	
    @Autowired
    private SysDictManager sysDictManager;
    
    @Autowired
	private SmsMobileSend smsMobileSend;

    public Integer deleteByPrimaryKey(Long id) {
		try {
			return sysDictManager.deleteByPrimaryKey(id);
		} catch (ManagerException e) {
			logger.error("删除数据字典失败,id=" + id, e);
		}
        return null;
    }

    public Integer insert(SysDict sysDict) {
    	try {
    	        return sysDictManager.insert( sysDict );
		} catch (ManagerException e) {
			logger.error("插入数据字典失败,sysDict=" + sysDict, e);
		}
        return null;
        
    }

    public SysDict selectByPrimaryKey(Long id) {
    	try {
    		 return sysDictManager.selectByPrimaryKey(id);
		} catch (ManagerException e) {
			logger.error("通过id查询数据字典失败,id=" + id, e);
		}
        return null;
        
    }

    public Integer updateByPrimaryKey(SysDict sysDict) {
    	try {
    		 return sysDictManager.updateByPrimaryKey(sysDict );
		} catch (ManagerException e) {
			logger.error("更新数据字典失败,sysDict=" + sysDict, e);
		}
        return null;
        
    }

    public Integer updateByPrimaryKeySelective(SysDict sysDict) {
    	try {
    		 return sysDictManager.updateByPrimaryKeySelective(sysDict );
		} catch (ManagerException e) {
			logger.error("更新数据字典失败,sysDict=" + sysDict, e);
		}
        return null;
        
    }

    public Integer batchDelete(long[] ids) {
    	try {
    		return sysDictManager.batchDelete(ids);
		} catch (ManagerException e) {
			logger.error("批量删除数据字典失败,ids=" + ids, e);
		}
        return null;
         
    }

    public Page<SysDict> findByPage(Page<SysDict> pageRequest, Map<String, Object> map) {
    	try {
    		 return sysDictManager.findByPage(pageRequest,map); 
		} catch (ManagerException e) {
			logger.error("分页查询数据字典失败", e);
		}
        return null;
       
    }

	@Override
	public List<SysDict> findByGroupName(String groupName) {
		try {
   		 return sysDictManager.findByGroupName(groupName);
		} catch (ManagerException e) {
			logger.error("分组查询数据字典失败", e);
		}
       return null;
	}

	@Override
	public Integer insertSelective(SysDict sysDict) throws ManagerException {
		try {
			return sysDictManager.insertSelective(sysDict);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public ResultDO<SysDict> deleteId(Long id) {
		ResultDO<SysDict> resultDO = new ResultDO<SysDict>();
		try {
			int result = sysDictManager.deleteByPrimaryKey(id);
			if (result <= 0) {
				resultDO.setSuccess(false);
			}
		} catch (Exception e) {
			logger.error("删除短信预警设置失败", e);
		}
		return resultDO;
	}

	@Override
	public ResultDO<BigDecimal> queryBalance(Long id) {
		ResultDO<BigDecimal> resultDO = new ResultDO<BigDecimal>();
		try {
			SysDict dict=sysDictManager.selectByPrimaryKey(id);
			if(dict!=null){
				resultDO=smsMobileSend.queryBalanceSMS(dict.getValue());
				if(resultDO.isSuccess()){
					SysDict sysDict=new SysDict();
					sysDict.setId(id);
					sysDict.setDescription(resultDO.getResult().toString());
					sysDictManager.updateByPrimaryKeySelective(sysDict);
				}
			}
		} catch (ManagerException e) {
			logger.error("查询短信余额失败", id);
		}
		
		return resultDO;
	}

	@Override
	public SysDict selectByKey(String key) {
		try {
	   		 return sysDictManager.selectByKey(key);
			} catch (ManagerException e) {
				logger.error("查询失败", e);
			}
	       return null;
		}

	@Override
	public SysDict findByGroupNameAndKey(String groupName, String key) {
		SysDict sysDict=new SysDict();
		try {
			sysDict= sysDictManager.findByGroupNameAndKey(groupName,key);
			return sysDict;
		} catch (ManagerException e) {
			logger.error("查询失败",e);
		}
		return sysDict;
	}
}