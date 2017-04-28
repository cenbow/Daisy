package com.yourong.backend.authority;

import java.util.ArrayList;
import java.util.List;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.PasswordService;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yourong.backend.sys.service.SysMenuService;
import com.yourong.backend.sys.service.SysRoleService;
import com.yourong.backend.sys.service.SysUserService;
import com.yourong.common.util.StringUtil;
import com.yourong.core.sys.model.SysMenu;
import com.yourong.core.sys.model.SysRole;
import com.yourong.core.sys.model.SysUser;
/**
 * 权限管理核心类
 * @author pengyong
 *
 */
@Service
public class MyShiro extends AuthorizingRealm {
	
	private static Logger logger = LoggerFactory.getLogger(MyShiro.class);
	@Autowired
	private SysUserService sysUserService;	
	//账号加密 	
	@Autowired
	private PasswordService  passwordService;
	
	//以后从缓存中获取 
	@Autowired 
	private SysMenuService sysMenuService;	
	@Autowired
	private SysRoleService sysRoleService;
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(
			PrincipalCollection principals) {
       String currentUsername = (String)super.getAvailablePrincipal(principals);  
      List<String> roleList = new ArrayList<String>();  
      List<String> permissionList = new ArrayList<String>();  
      //从数据库中获取当前登录用户的详细信息  
       SysUser user = sysUserService.selectSysUserRoleByLoginName(currentUsername);  
		if (null != user) {
			// 实体类User中包含有用户角色的实体类信息
			if (null != user.getRoles() && user.getRoles().size() > 0) {
				// 获取当前登录用户的角色
				for (SysRole role : user.getRoles()) {
					roleList.add(role.getName());
					SysRole sysRole = sysRoleService.selectRoleMenus(role.getId());						
					List<SysMenu> menus = sysRole.getMenus();
						for (SysMenu temp : menus) {							
							if (temp != null && StringUtil.isNotBlank(temp.getPermission())) {
								permissionList.add(temp.getPermission());
							}
						}

					}
				
			}
		} else {
			throw new AuthorizationException();
		}
//      //为当前用户设置角色和权限  
      SimpleAuthorizationInfo simpleAuthorInfo = new SimpleAuthorizationInfo();  
      simpleAuthorInfo.addRoles(roleList);  
      simpleAuthorInfo.addStringPermissions(permissionList);       
      return simpleAuthorInfo;
	}

	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(
			AuthenticationToken authcToken) throws AuthenticationException {
		
		UsernamePasswordToken token = (UsernamePasswordToken) authcToken;		
		SysUser user = sysUserService.selectByLoginName(token.getUsername());
		if (user != null) {		
			SimpleAuthenticationInfo simpleAuthenticationInfo = new SimpleAuthenticationInfo(user.getLoginName(),user.getPassword(), getName());
			return simpleAuthenticationInfo;			
		} else {
			// 没有返回登录用户名对应的SimpleAuthenticationInfo对象时,就会在LoginController中抛出UnknownAccountException异常
			return null;
		}
	}

}
