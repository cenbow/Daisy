/**
 * 
 */
package com.yourong.core.sys.model;

import java.util.List;

/**
 * @desc TODO
 * @author chaisen
 * 2015年11月30日下午1:24:18
 */
public class Menu {
    private Long id;

    private Long pid;
    
    private String text;

    
    private List<Menu> children;
    

	public List<Menu> getChildren() {
		return children;
	}

	public void setChildren(List<Menu> children) {
		this.children = children;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getPid() {
		return pid;
	}

	public void setPid(Long pid) {
		this.pid = pid;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
    
    
}
