package com.yourong.core.ic;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import com.yourong.common.enums.DebtEnum;
import com.yourong.common.enums.StatusEnum;
import com.yourong.common.util.DateUtils;
import com.yourong.common.util.FormulaUtil;
import com.yourong.core.BaseTest;
import com.yourong.core.ic.manager.DebtInterestManager;
import com.yourong.core.ic.manager.ProjectManager;
import com.yourong.core.ic.model.DebtInterest;
import com.yourong.core.ic.model.Project;
import com.yourong.core.tc.manager.TransactionInterestManager;

/**
 * Created by peng.yong on 2016/1/7.
 */
public class ProjectMangerTest   extends BaseTest {
    @Autowired
    private ProjectManager projectManager;
    @Autowired
    private TransactionInterestManager transactionInterestManager;
    @Autowired
    private DebtInterestManager debtInterestManager;

    //@Test
    @Rollback(false)
    public  void calculateInterestByDayAndReturAndMonthlyPaidBy1Test()throws  Exception{
//        Integer interestFrom = project.getInterestFrom();
//        //借款周期
//        Integer borrowPeriod = project.getBorrowPeriod();
//        //借款周期类型（1-日；2-月；3-年）
//        Integer borrowPeriodType = project.getBorrowPeriodType();
        Project project = new Project();
        //借款利率
        project.setAnnualizedRate(new BigDecimal(14));
        //借款金额
        project.setTotalAmount(new BigDecimal("100000"));
        //借款金额
        project.setProfitType(DebtEnum.RETURN_TYPE_DAY.getCode());
        //借款周期
        project.setBorrowPeriod(20);
        //借款周期类型（1-日；2-月；3-年）
        project.setBorrowPeriodType(1);
        project.setInterestFrom(0);
        Date  date = new Date();
        List<DebtInterest> debtInterests = projectManager.calculateInterest(project, date);
        for (DebtInterest debtInterest: debtInterests){
            System.out.println("-----"+ DateUtils.getTimeFrom(debtInterest.getStartDate())  +"----"+DateUtils.getTimeFrom(debtInterest.getEndDate()));
            System.out.println("-----"+ debtInterest.getPayablePrincipal().toPlainString()  +"----"+debtInterest.getPayableInterest().toPlainString());
        }
    }
    //@Test
    @Rollback(false)
    public  void calculateInterestByDayAndReturAndMonthlyPaidBy2Test()throws  Exception{
//        Integer interestFrom = project.getInterestFrom();
//        //借款周期
//        Integer borrowPeriod = project.getBorrowPeriod();
//        //借款周期类型（1-日；2-月；3-年）
//        Integer borrowPeriodType = project.getBorrowPeriodType();
        Project project = new Project();
        //借款利率
        project.setAnnualizedRate(new BigDecimal(14));
        //借款金额
        project.setTotalAmount(new BigDecimal("100000"));
        //借款金额
        project.setProfitType(DebtEnum.RETURN_TYPE_DAY.getCode());
        //借款周期
        project.setBorrowPeriod(4);
        //借款周期类型（1-日；2-月；3-年）
        project.setBorrowPeriodType(2);
        project.setInterestFrom(0);
        Date  date = new Date();
        List<DebtInterest> debtInterests = projectManager.calculateInterest(project, date);
        for (DebtInterest debtInterest: debtInterests){
            System.out.println("-----"+ DateUtils.getTimeFrom(debtInterest.getStartDate())  +"----"+DateUtils.getTimeFrom(debtInterest.getEndDate()));
            System.out.println("-----"+ debtInterest.getPayablePrincipal().toPlainString()  +"----"+debtInterest.getPayableInterest().toPlainString());
        }
    }

    //@Test
    @Rollback(false)
    public  void calculateInterestByDayAndReturAndMonthlyPaidBy3Test()throws  Exception{
//        Integer interestFrom = project.getInterestFrom();
//        //借款周期
//        Integer borrowPeriod = project.getBorrowPeriod();
//        //借款周期类型（1-日；2-月；3-年）
//        Integer borrowPeriodType = project.getBorrowPeriodType();
        Project project = new Project();
        //借款利率
        project.setAnnualizedRate(new BigDecimal(14));
        //借款金额
        project.setTotalAmount(new BigDecimal("100000"));
        //借款金额
        project.setProfitType(DebtEnum.RETURN_TYPE_DAY.getCode());
        //借款周期
        project.setBorrowPeriod(3);
        //借款周期类型（1-日；2-月；3-年）
        project.setBorrowPeriodType(3);
        project.setInterestFrom(0);
        Date  date = new Date();
        List<DebtInterest> debtInterests = projectManager.calculateInterest(project, date);
        for (DebtInterest debtInterest: debtInterests){
            System.out.println("-----"+ DateUtils.getTimeFrom(debtInterest.getStartDate())  +"----"+DateUtils.getTimeFrom(debtInterest.getEndDate()));
            System.out.println("-----"+ debtInterest.getPayablePrincipal().toPlainString()  +"----"+debtInterest.getPayableInterest().toPlainString());
        }
    }
    //@Test
    @Rollback(false)
    public  void calculateInterestByOnceBy1Test()throws  Exception{
//        Integer interestFrom = project.getInterestFrom();
//        //借款周期
//        Integer borrowPeriod = project.getBorrowPeriod();
//        //借款周期类型（1-日；2-月；3-年）
//        Integer borrowPeriodType = project.getBorrowPeriodType();
        Project project = new Project();
        //借款利率
        project.setAnnualizedRate(new BigDecimal(14));
        //借款金额
        project.setTotalAmount(new BigDecimal("100000"));
        //借款金额
        project.setProfitType(DebtEnum.RETURN_TYPE_ONCE.getCode());
        //借款周期
        project.setBorrowPeriod(52);
        //借款周期类型（1-日；2-月；3-年）
        project.setBorrowPeriodType(1);
        project.setInterestFrom(0);
        Date  date = new Date();
        List<DebtInterest> debtInterests = projectManager.calculateInterest(project, date);
        for (DebtInterest debtInterest: debtInterests){
            System.out.println("-----"+ DateUtils.getTimeFrom(debtInterest.getStartDate())  +"----"+DateUtils.getTimeFrom(debtInterest.getEndDate()));
            System.out.println("-----"+ debtInterest.getPayablePrincipal().toPlainString()  +"----"+debtInterest.getPayableInterest().toPlainString());
        }
    }

    //@Test
        @Rollback(false)
        public  void calculateInterestByOnceBy2Test()throws  Exception{
//        Integer interestFrom = project.getInterestFrom();
//        //借款周期
//        Integer borrowPeriod = project.getBorrowPeriod();
//        //借款周期类型（1-日；2-月；3-年）
//        Integer borrowPeriodType = project.getBorrowPeriodType();
        Project project = new Project();
        //借款利率
        project.setAnnualizedRate(new BigDecimal(14));
        //借款金额
        project.setTotalAmount(new BigDecimal("100000"));
        //借款金额
        project.setProfitType(DebtEnum.RETURN_TYPE_ONCE.getCode());
        //借款周期
        project.setBorrowPeriod(2);
        //借款周期类型（1-日；2-月；3-年）
        project.setBorrowPeriodType(2);
        project.setInterestFrom(0);
        Date  date = new Date();
        List<DebtInterest> debtInterests = projectManager.calculateInterest(project, date);
        for (DebtInterest debtInterest: debtInterests){
            System.out.println("-----"+ DateUtils.getTimeFrom(debtInterest.getStartDate())  +"----"+DateUtils.getTimeFrom(debtInterest.getEndDate()));
            System.out.println("-----"+ debtInterest.getPayablePrincipal().toPlainString()  +"----"+debtInterest.getPayableInterest().toPlainString());
        }
    }
    //@Test
    @Rollback(false)
    public  void calculateInterestByOnceBy3Test()throws  Exception{
//        Integer interestFrom = project.getInterestFrom();
//        //借款周期
//        Integer borrowPeriod = project.getBorrowPeriod();
//        //借款周期类型（1-日；2-月；3-年）
//        Integer borrowPeriodType = project.getBorrowPeriodType();
        Project project = new Project();
        //借款利率
        project.setAnnualizedRate(new BigDecimal(14));
        //借款金额
        project.setTotalAmount(new BigDecimal("100000"));
        //借款金额
        project.setProfitType(DebtEnum.RETURN_TYPE_ONCE.getCode());
        //借款周期
        project.setBorrowPeriod(1);
        //借款周期类型（1-日；2-月；3-年）
        project.setBorrowPeriodType(3);
        project.setInterestFrom(0);
        Date  date = new Date();
        List<DebtInterest> debtInterests = projectManager.calculateInterest(project, date);
        for (DebtInterest debtInterest: debtInterests){
            System.out.println("-----"+ DateUtils.getTimeFrom(debtInterest.getStartDate())  +"----"+DateUtils.getTimeFrom(debtInterest.getEndDate()));
            System.out.println("-----"+ debtInterest.getPayablePrincipal().toPlainString()  +"----"+debtInterest.getPayableInterest().toPlainString());
        }
    }



    //@Test
    @Rollback(false)
    public  void calculateInterestByAvgBy2Test()throws  Exception{
//        Integer interestFrom = project.getInterestFrom();
//        //借款周期
//        Integer borrowPeriod = project.getBorrowPeriod();
//        //借款周期类型（1-日；2-月；3-年）
//        Integer borrowPeriodType = project.getBorrowPeriodType();
        Project project = new Project();
        //借款利率
        project.setAnnualizedRate(new BigDecimal(14));
        //借款金额
        project.setTotalAmount(new BigDecimal("100000"));
        //借款金额
        project.setProfitType(DebtEnum.RETURN_TYPE_AVG_PRINCIPAL_INTEREST.getCode());
        //借款周期
        project.setBorrowPeriod(5);
        //借款周期类型（1-日；2-月；3-年）
        project.setBorrowPeriodType(2);
        project.setInterestFrom(0);
        Date  date = new Date();
        List<DebtInterest> debtInterests = projectManager.calculateInterest(project, date);
        for (DebtInterest debtInterest: debtInterests){
            System.out.println("-----"+ DateUtils.getTimeFrom(debtInterest.getStartDate())  +"----"+DateUtils.getTimeFrom(debtInterest.getEndDate()));
            System.out.println("-----"+ debtInterest.getPayablePrincipal().toPlainString()  +"----"+debtInterest.getPayableInterest().toPlainString());
        }
    }
    
    
    //@Test
    @Rollback(false)
    public  void loseProjectTest()throws  Exception{
    	projectManager.autoLoseProject();
    }
    
    @Test
    @Rollback(true)
    public void testCount(){
    	/*int count = transactionInterestManager.getCountUnReturnTransationInterestByProjectIdAndEndDate(
    			989800664L, DateUtils.format("2016-06-27"));
    	System.out.println("测试脚本："+count);*/
    	try {
			
//    		Project statusresult =  projectManager.selectByPrimaryKey(989800664L);
//    		System.out.println("更新状态方法："+FormulaUtil.getManagerAmount(statusresult.getTotalAmount(), statusresult.getGuaranteeFeeRate()));
    		Project pro = projectManager.selectByPrimaryKey(989800664L);
    		projectManager.createCollectTradeForGuaranteeFee(pro);
    	} catch (Exception e) {
    		logger.error(e);
    	}
    }
}
