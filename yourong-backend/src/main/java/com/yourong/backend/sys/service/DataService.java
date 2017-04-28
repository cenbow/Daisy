package com.yourong.backend.sys.service;

import com.yourong.core.data.model.AlreadyInvest;
import com.yourong.core.data.model.DouWan;
import com.yourong.core.data.model.LiCai;
import com.yourong.core.data.model.NoInvest;

import java.util.Date;
import java.util.List;

/**
 * Created by XR on 2016/11/28.
 * 数据频道service
 */
public interface DataService {
    List<LiCai> queryLiCai(Date datestart, Date datestop);

    List<NoInvest> queryNoInvest(Date datestart, Date datestop);

    List<AlreadyInvest> queryAlreadyInvest(String birthday);

	List<DouWan> queryDouWans(Date date,Date endDate);
}
