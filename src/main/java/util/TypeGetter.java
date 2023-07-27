package util;

import model.TransType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class TypeGetter {
    private HashMap<Integer, ArrayList<TransType>> typeList = new HashMap<>();
    private static final Random numR = new Random(100);
    private Integer transactionIndicatorNum;
    private Integer transactionTypeNum;
    public TypeGetter(Integer transactionTypeNum, Integer transactionIndicatorNum){
        this.transactionTypeNum = transactionTypeNum;
        this.transactionIndicatorNum = transactionIndicatorNum;
        for(int i=0; i < transactionTypeNum; i++){
            ArrayList<TransType> transactionTransTypeList = new ArrayList<>();
            for (int j =0; j < transactionIndicatorNum; j++){
                TransType t = new TransType();
                t.setTransactionType(i);
                t.setTransactionIndicator(j);
                t.setTransactionIndex(0L);
                transactionTransTypeList.add(t);
            }
            typeList.put(i, transactionTransTypeList);
        }
    }
    public TransType getRandomType(Integer typeId){
        TransType out = null;
        TransType output = new TransType();
        if(typeId-1 < transactionTypeNum){
            int num = numR.nextInt(transactionIndicatorNum);
            out = typeList.get(typeId-1).get(num);
            output.setTransactionType(out.getTransactionType()+1);
            output.setTransactionIndex(out.getTransactionIndex()+1);
            output.setTransactionIndicator(out.getTransactionIndicator());
            out.indexAdd();
        }
        return output;
    }
    public void PrintTypeNum(){
        for(Integer k: typeList.keySet()){
            ArrayList<TransType> arrayListT = typeList.get(k);
            for(TransType t: arrayListT){
                System.out.println("TransType "+ t.getTransactionType() +";Indicator "+t.getTransactionIndicator()+"; num"+ t.getTransactionIndex());
            }
        }
    }
}
