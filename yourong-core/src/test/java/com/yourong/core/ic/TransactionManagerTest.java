package com.yourong.core.ic;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import com.yourong.common.enums.DebtEnum;
import com.yourong.common.util.DateUtils;
import com.yourong.core.BaseTest;
import com.yourong.core.ic.manager.ProjectManager;
import com.yourong.core.ic.model.DebtInterest;
import com.yourong.core.ic.model.Project;
import com.yourong.core.tc.manager.TransactionManager;
import com.yourong.core.tc.model.Transaction;
import com.yourong.core.tc.model.TransactionInterest;

/**
 * Created by peng.yong on 2016/1/11.
 */
public class TransactionManagerTest   extends BaseTest {
    @Autowired
    private ProjectManager projectManager;
    @Autowired
    private TransactionManager transactionManager;

    @Test
    @Rollback(false)
    public void  computeInvesterPrincipalAndInterestBy1DayTest() throws  Exception{
        Project project = new Project();
        //借款利率
        project.setAnnualizedRate(new BigDecimal(14));
        //借款金额
        project.setTotalAmount(new BigDecimal("100000"));
        //借款金额
        project.setProfitType(DebtEnum.RETURN_TYPE_AVG_PRINCIPAL_INTEREST.getCode());
        //借款周期
        project.setBorrowPeriod(2);
        //借款周期类型（1-日；2-月；3-年）
        project.setBorrowPeriodType(3);
        project.setInterestFrom(0);
        Date date = new Date();
        List<DebtInterest> debtInterests = projectManager.calculateInterest(project, date);
        BigDecimal  borowInver = BigDecimal.ZERO;
        BigDecimal  borowPay = BigDecimal.ZERO;
        for (DebtInterest debtInterest:debtInterests){
            borowInver = borowInver.add(debtInterest.getPayableInterest());
            borowPay = borowPay.add(debtInterest.getPayablePrincipal());
            System.out.println("--还款时间从"+DateUtils.getTimeFrom(debtInterest.getStartDate())+"到"+DateUtils.getTimeFrom(debtInterest.getEndDate())
                    +",应付本金："+debtInterest.getPayablePrincipal().toPlainString()+",应付利息："+debtInterest.getPayableInterest().toPlainString());
        }
        System.out.println("--借款本金："+ borowPay.toPlainString());
        System.out.println("--借款利息: " + borowInver.toPlainString());
        Transaction transaction = new Transaction();
        transaction.setId(1234566895L);
        transaction.setInstallmentNum(debtInterests.size());
        transaction.setAnnualizedRate(project.getAnnualizedRate());
        transaction.setInvestAmount(new BigDecimal("10000"));
        transaction.setMemberId(11808888L);
        BigDecimal totalInvert = BigDecimal.ZERO;
        BigDecimal totalPay = BigDecimal.ZERO;
        for ( int i =0;i<10;i++) {
            List<TransactionInterest> transactionInterests = transactionManager.computeInvesterPrincipalAndInterest(transaction, debtInterests, project);
            for (TransactionInterest transactionInterest : transactionInterests) {
                System.out.println("-----------------------:开始计息" + DateUtils.getTimeFrom(transactionInterest.getStartDate()) + "" +
                                "--结束日期:" + DateUtils.getTimeFrom(transactionInterest.getEndDate())
                                + "--应付利息:" + transactionInterest.getPayableInterest().toPlainString()
                                + "--应付本金:" + transactionInterest.getPayablePrincipal().toPlainString());
                totalInvert = totalInvert.add( transactionInterest.getPayableInterest());
                totalPay =  totalPay.add(transactionInterest.getPayablePrincipal());
            }
        }
        System.out.println( "---总本金："+totalPay.toPlainString()   );
        System.out.println( "---总利息："+totalInvert.toPlainString() );
    }


    //@Test
    @Rollback(false)
    public void  computeInvesterPrincipalAndInterestByAvgTest() throws  Exception{
        System.out.println("-----"+DebtEnum.RETURN_TYPE_AVG_PRINCIPAL_INTEREST.getDesc()+"-----");
        Project project = new Project();
        //借款利率
        project.setAnnualizedRate(new BigDecimal(14));
        //借款金额
        project.setTotalAmount(new BigDecimal("100000"));
        //借款类型
        project.setProfitType(DebtEnum.RETURN_TYPE_AVG_PRINCIPAL_INTEREST.getCode());
        //借款周期
        project.setBorrowPeriod(2);
        //借款周期类型（1-日；2-月；3-年）
        project.setBorrowPeriodType(2);
        project.setInterestFrom(0);
        Date date = new Date();
        List<DebtInterest> debtInterests = projectManager.calculateInterest(project, date);
        BigDecimal  borowInver = BigDecimal.ZERO;
        BigDecimal  borowPay = BigDecimal.ZERO;
        for (DebtInterest debtInterest:debtInterests){
            System.out.println("--还款时间从"+DateUtils.getTimeFrom(debtInterest.getStartDate())+"到"+DateUtils.getTimeFrom(debtInterest.getEndDate())
                    +",应付本金："+debtInterest.getPayablePrincipal().toPlainString()+",应付利息："+debtInterest.getPayableInterest().toPlainString());
            borowInver = borowInver.add(debtInterest.getPayableInterest());
            borowPay = borowPay.add(debtInterest.getPayablePrincipal());
        }
        System.out.println("--借款本金：" + borowPay.toPlainString());
        System.out.println("--借款利息: "+ borowInver.toPlainString());
        Transaction transaction = new Transaction();
        transaction.setId(1234566895L);
        transaction.setInstallmentNum(debtInterests.size());
        transaction.setAnnualizedRate(project.getAnnualizedRate());
        transaction.setInvestAmount(new BigDecimal("10000"));
        transaction.setMemberId(11808888L);
        BigDecimal totalInvert = BigDecimal.ZERO;
        BigDecimal totalPay = BigDecimal.ZERO;
        for ( int i =0;i<10;i++) {
            List<TransactionInterest> transactionInterests = transactionManager.computeInvesterPrincipalAndInterest(transaction, debtInterests, project);
            System.out.println("第"+(i+1)+"投资者，投资："+transaction.getInvestAmount().toPlainString());
            for (TransactionInterest transactionInterest : transactionInterests) {
                System.out.println("-----------:开始计息" + DateUtils.getTimeFrom(transactionInterest.getStartDate()) + "" +
                        "--结束日期:" + DateUtils.getTimeFrom(transactionInterest.getEndDate())
                        + "--应付利息:" + transactionInterest.getPayableInterest().toPlainString()
                        + "--应付本金:" + transactionInterest.getPayablePrincipal().toPlainString());
                totalInvert = totalInvert.add( transactionInterest.getPayableInterest());
                totalPay =  totalPay.add(transactionInterest.getPayablePrincipal());
            }
        }
        System.out.println( "---总本金："+totalPay.toPlainString()   );
        System.out.println( "---总利息："+totalInvert.toPlainString() );
    }
}
