package fun.werfamily.transaction.framework.executor.impl;

import fun.werfamily.transaction.framework.commons.AbstractPrintable;
import fun.werfamily.transaction.framework.dao.TransactionTaskLogDO;
import fun.werfamily.transaction.framework.enums.TransactionStatusEnum;
import fun.werfamily.transaction.framework.task.AbstractReversibleTask;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 事务上下文
 *
 * @AuthorMr.WenMing
 * @date 2022年03月12日
 */
public class TransactionContext extends AbstractPrintable {

    private static final long serialVersionUID = 1L;

    private TransactionStatusEnum transactionStatus = TransactionStatusEnum.COMMIT;

    private List<AbstractReversibleTask> transactionTaskList = new ArrayList<AbstractReversibleTask>();

    private List<TransactionTaskLogDO> transactionLogDOList = new ArrayList<TransactionTaskLogDO>();

    public TransactionStatusEnum getTransactionStatus() {
        return transactionStatus;
    }

    public void setTransactionStatus(TransactionStatusEnum transactionStatus) {
        this.transactionStatus = transactionStatus;
    }

    public List<AbstractReversibleTask> getTransactionTaskList() {
        return transactionTaskList;
    }

    public List<TransactionTaskLogDO> getTransactionLogDOList() {
        return transactionLogDOList;
    }

    public void addTransactionTask(AbstractReversibleTask task, TransactionTaskLogDO transactionLog) {
        transactionTaskList.add(task);
        transactionLogDOList.add(transactionLog);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(
                transactionLogDOList.stream().map(TransactionTaskLogDO::getId).collect(Collectors.toList()),
                ToStringStyle.JSON_STYLE);
    }

}
