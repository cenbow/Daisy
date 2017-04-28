package com.yourong.core.data.dao;

import com.yourong.core.data.model.AlreadyInvest;
import com.yourong.core.data.model.DouWan;
import com.yourong.core.data.model.LiCai;
import com.yourong.core.data.model.NoInvest;

import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * Created by XR on 2016/11/28.
 */
public interface DataMapper {
    List<LiCai> queryLiCai(@Param("datestart")Date datestart, @Param("datestop")Date datestop);

    List<NoInvest> queryNoInvest(@Param("datestart")Date datestart, @Param("datestop")Date datestop);

    List<AlreadyInvest> queryAlreadyInvest(@Param("birthday") String birthday);

	List<DouWan> queryDouWans(@Param("startDate")Date startDate, @Param("endDate")Date endDate);
}
