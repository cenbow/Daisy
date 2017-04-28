package com.yourong.core.repayment.model;

import com.yourong.common.domain.AbstractBaseObject;
import com.yourong.core.ic.model.Project;
import com.yourong.core.tc.model.TransactionInterest;

/**
 * 待付成功后的处理时间
 * Created by  on 2015/7/29.
 */
public class HostingPayTradeFinishedListenerObject extends AbstractBaseObject {

    private TransactionInterest transactionInterest;
    private Project project;

    public TransactionInterest getTransactionInterest() {
        return transactionInterest;
    }

    public void setTransactionInterest(TransactionInterest transactionInterest) {
        this.transactionInterest = transactionInterest;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }
}
