package com.yourong.backend.ic.service;

import com.yourong.common.domain.ResultDO;
import com.yourong.common.pageable.Page;
import com.yourong.core.ic.model.BorrowerCreditGrade;
import com.yourong.core.ic.model.query.BorrowerCreditGradeQuery;

import java.util.Date;

/**
 * Created by alan.zheng on 2017/3/2.
 */
public interface BorrowerCreditGradeService {
    /**
     * 分页查询
     * @param query
     * @return
     */
    Page<BorrowerCreditGrade> queryPageBorrowerCreditGrade(BorrowerCreditGradeQuery query);

    /**
     * 查询信用等级
     * @param memberId
     * @return
     */
    ResultDO<BorrowerCreditGrade> queryBorrowerCredit(Long memberId);

    /**
     * 更新信用等级
     * @param creditLevel
     * @param borrowerId
     * @return
     */
    boolean updateCreditLevel(String creditLevel,Long borrowerId);

    /**
     * 保存信用等级信息
     * @param borrowerId
     * @return
     */
    boolean saveBorrowerCreditGrade(Long borrowerId);

    /**
     * 查询用户的信用等级
     * @param borrowerId
     * @return
     */
    BorrowerCreditGrade queryByBorrowerId(Long borrowerId);
}
