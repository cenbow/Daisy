package com.yourong.api.cache;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yourong.api.service.SysAreaService;
import com.yourong.api.service.SysDictService;
import com.yourong.common.cache.RedisManager;
import com.yourong.core.sys.model.SysArea;
import com.yourong.core.sys.model.SysDict;

@Service
public class MyCacheManager {	
	
	protected Logger logger = LoggerFactory.getLogger(MyCacheManager.class);

	@Autowired
	private SysDictService sysDictService;

	@Autowired
	private SysAreaService sysAreaService;

//	@Autowired
//	private RedisTemplate<Object, Object> redisTemplate;

	private static String redisCode = "utf-8";

	/**
	 * 获取指定数据字典
	 * 
	 * @param groupName
	 * @return
	 */
	public List<SysDict> getListSysDictByGroupName(final String groupName) {
		List<SysDict> object2 = null;
				//(List<SysDict>) RedisManager.getObject(groupName);
		if (object2 == null) {
			object2 = sysDictService.findByGroupName(groupName);
			//RedisManager.putObject(groupName, object2);
		}
		return object2;
	}

	/**
	 * 获取所有区域
	 * 
	 * @return
	 */
	public List<SysArea> getAllArea() {
		List<SysArea> object;
//		ValueOperations<Object, Object> forValue = redisTemplate.opsForValue();
//		object = (List<SysArea>) forValue.get("all-SysArea");
//		if (object == null) {
			object = sysAreaService.getAllSysArea();
//			forValue.set("all-SysArea", object);
//		}
		return object;
	}

	public void cleanCache(String key) {
		RedisManager.removeObject(key);
	}
	/**
	 * 将对象放进redis
	 * @param key
	 * @param value
	 */
	public void  putToRedis(String key ,Object value){
		RedisManager.putObject(key, value);
	}
	/**
	 *  根据key获取对象
	 * @param key
	 * @return
	 */
	public Object getValueFormRedis(String key ){
		return RedisManager.getObject(key);
	}
	




}
