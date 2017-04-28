package com.yourong.core.ic.dao;

import com.yourong.core.ic.model.BorrowerCreditGrade;
import com.yourong.core.ic.model.query.BorrowerCreditGradeQuery;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * Created by alan.zheng on 2017/3/1.
 */
public interface BorrowerCreditGradeMapper {
    List<BorrowerCreditGrade> queryPageBorrowerCredit(@Param("query") BorrowerCreditGradeQuery query);

    BorrowerCreditGrade queryByBorrowerId(@Param("borrowId") Long borrowId);

    int queryPageCountBorrowerCredit(@Param("query") BorrowerCreditGradeQuery query);

    int updateBlackInfo(@Param("borrowerCreditGrade") BorrowerCreditGrade borrowerCreditGrade);

    int updateCreditLevel(@Param("creditLevel") String creditLevel, @Param("borrowerId") Long borrowerId,@Param("creditLevelDes") String creditLevelDes,
                          @Param("updateTime")Date updateTime);

    int saveBorrowerCreditGrade(@Param("borrowerCreditGrade") BorrowerCreditGrade borrowerCreditGrade);
}
