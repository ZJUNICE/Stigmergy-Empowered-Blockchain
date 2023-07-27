package model;

import java.io.Serializable;

public class TransType implements Cloneable, Serializable {
    public Integer TransactionType;
    public Integer TransactionIndicator;
    public Long TransactionIndex;

    public Integer getTransactionType() {
        return TransactionType;
    }

    public void setTransactionType(Integer TransactionType) {
        this.TransactionType = TransactionType;
    }

    public Integer getTransactionIndicator() {
        return TransactionIndicator;
    }

    public void setTransactionIndicator(Integer TransactionIndicator) {
        this.TransactionIndicator = TransactionIndicator;
    }

    public Long getTransactionIndex() {
        return TransactionIndex;
    }

    public void setTransactionIndex(Long TransactionIndex) {
        this.TransactionIndex = TransactionIndex;
    }

    public void indexAdd(){
        TransactionIndex ++;
    }
}
