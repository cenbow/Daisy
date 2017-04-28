package com.yourong.backend.sys.service.impl;

import com.yourong.backend.sys.service.DataService;
import com.yourong.core.data.manager.DataManager;
import com.yourong.core.data.model.AlreadyInvest;
import com.yourong.core.data.model.DouWan;
import com.yourong.core.data.model.LiCai;
import com.yourong.core.data.model.NoInvest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Created by XR on 2016/11/28.
 */
@Service
public class DataServiceImpl implements DataService {
    @Autowired
    private DataManager dataManager;
    @Override
    public List<LiCai> queryLiCai(Date datestart, Date datestop) {
        return dataManager.queryLiCai(datestart,datestop);
    }

    @Override
    public List<NoInvest> queryNoInvest(Date datestart, Date datestop) {
        return dataManager.queryNoInvest(datestart,datestop);
    }

    @Override
    public List<AlreadyInvest> queryAlreadyInvest(String birthday) {
        return dataManager.queryAlreadyInvest(birthday);
    }

	@Override
	public List<DouWan> queryDouWans(Date startDate, Date endDate) {
		 return dataManager.queryDouWans(startDate,endDate);
	}
}
