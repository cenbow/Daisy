package com.yourong.core.ic.manager.impl;

import com.yourong.common.pageable.Page;
import com.yourong.core.ic.dao.BorrowerCreditGradeMapper;
import com.yourong.core.ic.manager.BorrowerCreditGradeManager;
import com.yourong.core.ic.model.BorrowerCreditGrade;
import com.yourong.core.ic.model.query.BorrowerCreditGradeQuery;
import com.yourong.core.sys.dao.SysDictMapper;
import com.yourong.core.sys.model.SysDict;
import com.yourong.core.uc.dao.MemberMapper;
import com.yourong.core.uc.model.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by alan.zheng on 2017/3/2.
 */
@Component
public class BorrowerCreditGradeManagerImpl implements BorrowerCreditGradeManager {
    @Autowired
    private BorrowerCreditGradeMapper borrowerCreditGradeMapper;

    @Autowired
    private SysDictMapper sysDictMapper;

    @Autowired
    private MemberMapper memberMapper;

    @Override
    public Page<BorrowerCreditGrade> queryPageBorrowerCreditGrade(BorrowerCreditGradeQuery query) {
        Page<BorrowerCreditGrade> page=new Page<>();
        List<BorrowerCreditGrade> list=new ArrayList<>();
        int totalCount = borrowerCreditGradeMapper.queryPageCountBorrowerCredit(query);
        if (totalCount>0){
            list= borrowerCreditGradeMapper.queryPageBorrowerCredit(query);
        }
        page.setData(list);
        page.setiTotalDisplayRecords(totalCount);
        page.setiTotalRecords(totalCount);
        page.setPageNo(query.getCurrentPage());
        page.setiDisplayLength(query.getPageSize());
        return page;
    }

    @Override
    public boolean updateBlackInfo(BorrowerCreditGrade borrowerCreditGrade) {
        if (borrowerCreditGradeMapper.updateBlackInfo(borrowerCreditGrade)>0){
            return true;
        }
        return false;
    }

    @Override
    public BorrowerCreditGrade queryByBorrowerId(Long borrowerId) {
        return borrowerCreditGradeMapper.queryByBorrowerId(borrowerId);
    }

    @Override
    public boolean updateCreditLevel(String creditLevel, Long borrowerId, Date updateTime) {
        Map<String,Object> map=new HashMap<>();
        map.put("groupName","credit_grade");
        map.put("key",creditLevel);
        SysDict sysDict= sysDictMapper.findByGroupNameAndKey(map);
        if (sysDict!=null){
            if (borrowerCreditGradeMapper.updateCreditLevel(creditLevel,borrowerId,sysDict.getValue(),updateTime)>0){
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean saveBorrowerCreditGrade(Long borrowerId) {
        Member member= memberMapper.selectByPrimaryKey(borrowerId);
        if (member==null){
            return false;
        }
        BorrowerCreditGrade borrowerCreditGrade=new BorrowerCreditGrade();
        borrowerCreditGrade.setBorrowerId(borrowerId);
        borrowerCreditGrade.setBorrowerTrueName(member.getTrueName());
        borrowerCreditGrade.setIdentityNumber(member.getIdentityNumber());
        borrowerCreditGrade.setBorrowerMobile(member.getMobile());
        borrowerCreditGrade.setCreateTime(new Date());
        borrowerCreditGrade.setDelFlag(1);
        if (borrowerCreditGradeMapper.saveBorrowerCreditGrade(borrowerCreditGrade)>0){
            return true;
        }
        return false;
    }
}
