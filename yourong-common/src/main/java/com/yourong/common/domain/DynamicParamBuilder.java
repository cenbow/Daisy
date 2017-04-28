package com.yourong.common.domain;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Lists;
import com.yourong.common.util.DateUtils;

@SuppressWarnings({ "rawtypes", "unchecked", "serial" })
public class DynamicParamBuilder extends AbstractBaseObject {

	private List paramValueList;

	private Map<String, Object> paramMap;

	private Long memberId;

	private boolean memberFlag = false;

	public List getParamValueList() {
		return paramValueList;
	}

	public void setParamValueList(List paramValueList) {
		this.paramValueList = paramValueList;
	}

	public Map<String, Object> getParamMap() {
		return paramMap;
	}

	public void setParamMap(Map<String, Object> paramMap) {
		this.paramMap = paramMap;
	}

	public Long getMemberId() {
		return memberId;
	}

	public void setMemberId(Long memberId) {
		this.memberId = memberId;
		setMemberFlag(true);
	}

	public boolean isMemberFlag() {
		return memberFlag;
	}

	private void setMemberFlag(boolean memberFlag) {
		this.memberFlag = memberFlag;
	}

	public List buildObjects(Map map) {
		Set set = map.entrySet();
		Iterator iterator = set.iterator();
		List list = Lists.newArrayList();
		while (iterator.hasNext()) {
			Map.Entry next = (Map.Entry) iterator.next();
			Object value = next.getValue();
			list.add(value);
		}
		return list;
	}

	public Object conventStringToTargetClass(String s, String value) {
		Object convertValue = null;
		switch (s) {
		case "string":
			convertValue = value;
			break;
		case "long":
			convertValue = Long.valueOf(value);
			break;
		case "integer":
			convertValue = Integer.valueOf(value);
			break;
		case "double":
			convertValue = Double.valueOf(value);
			break;
		case "date":
			convertValue = DateUtils.getDateFromString(value);
			break;
		case "time":
			convertValue = DateUtils.getDateTimeFromString(value);
			break;
		case "boolean":
			convertValue = Boolean.valueOf(value);
			break;
		default:
			convertValue = null;
		}
		return convertValue;
	}
}
