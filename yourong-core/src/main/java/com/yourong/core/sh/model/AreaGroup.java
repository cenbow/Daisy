package com.yourong.core.sh.model;

import java.util.List;

public class AreaGroup {
	private String parentCode;
	private List<Area> areas;
	
	public String getParentCode() {
		return parentCode;
	}
	public void setParentCode(String parentCode) {
		this.parentCode = parentCode;
	}
	public List<Area> getAreas() {
		return areas;
	}
	public void setAreas(List<Area> areas) {
		this.areas = areas;
	}
	@Override
	public String toString() {
		return "AreaGroup [parentCode=" + parentCode + ", areas=" + areas + "]";
	}

	
}
