package com.yourong.core.ic.manager;

import com.yourong.common.pageable.Page;
import com.yourong.core.ic.model.BorrowerCreditGrade;
import com.yourong.core.ic.model.query.BorrowerCreditGradeQuery;

import java.util.Date;

/**
 * Created by alan.zheng on 2017/3/2.
 */
public interface BorrowerCreditGradeManager {
    /**
     * 分页查询
     * @param query
     * @return
     */
    Page<BorrowerCreditGrade> queryPageBorrowerCreditGrade(BorrowerCreditGradeQuery query);

    /**
     * 更新黑名单信息
     * @param borrowerCreditGrade
     * @return
     */
    boolean updateBlackInfo(BorrowerCreditGrade borrowerCreditGrade);

    /**
     * 查询评级信息
     * @param borrowerId
     * @return
     */
    BorrowerCreditGrade queryByBorrowerId(Long borrowerId);

    /**
     * 更新信用等级
     * @param creditLevel
     * @param borrowerId
     * @param updateTime
     * @return
     */
    boolean updateCreditLevel(String creditLevel,Long borrowerId, Date updateTime);

    /**
     * 保存信用信息
     * @param borrowerId
     * @return
     */
    boolean saveBorrowerCreditGrade(Long borrowerId);
}
