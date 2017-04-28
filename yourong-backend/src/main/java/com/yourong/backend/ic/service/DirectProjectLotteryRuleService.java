package com.yourong.backend.ic.service;

import com.yourong.core.ic.model.DirectLotteryRuleForBackend;
import com.yourong.core.ic.model.Project;

public interface DirectProjectLotteryRuleService {

	public Object save(DirectLotteryRuleForBackend dir);
	
	public Object ajaxInit(DirectLotteryRuleForBackend dir);
	
	public void  catalyticActivity(Project pro);
	
	
}
