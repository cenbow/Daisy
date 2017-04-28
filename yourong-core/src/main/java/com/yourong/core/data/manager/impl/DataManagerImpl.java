package com.yourong.core.data.manager.impl;

import com.yourong.core.data.dao.DataMapper;
import com.yourong.core.data.manager.DataManager;
import com.yourong.core.data.model.AlreadyInvest;
import com.yourong.core.data.model.DouWan;
import com.yourong.core.data.model.LiCai;
import com.yourong.core.data.model.NoInvest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * Created by XR on 2016/11/28.
 */
@Component
public class DataManagerImpl implements DataManager {
    @Autowired
    private DataMapper dataMapper;
    @Override
    public List<LiCai> queryLiCai(Date datestart, Date datestop) {
        return dataMapper.queryLiCai(datestart,datestop);
    }

    @Override
    public List<NoInvest> queryNoInvest(Date datestart, Date datestop) {
        return dataMapper.queryNoInvest(datestart,datestop);
    }

    @Override
    public List<AlreadyInvest> queryAlreadyInvest(String birthday) {
        return dataMapper.queryAlreadyInvest(birthday);
    }

	@Override
	public List<DouWan> queryDouWans(Date startDate, Date endDate) {
        return dataMapper.queryDouWans(startDate,endDate);
        }
}
