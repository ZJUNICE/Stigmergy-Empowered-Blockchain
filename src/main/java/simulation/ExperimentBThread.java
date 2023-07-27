package simulation;

import model.Transaction;
import util.TransactionSearch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * @program: SEB-Framework
 * @description: Transaction Data Create Thread
 * @creator: Li DX
 */
public class ExperimentBThread implements Callable<ArrayList<Long>> {
    private Integer VNodeNum;
    private Integer transactionSize;
    private int pairNum;
    private Integer transactionTypeNum;

    public void setParameters(Integer VNodeNum, Integer transactionSize, Integer transactionTypeNum, int pairNum) throws Exception {
        this.VNodeNum = VNodeNum;
        this.transactionSize = transactionSize;
        this.pairNum = pairNum;
        this.transactionTypeNum = transactionTypeNum;
    }

    @Override
    public ArrayList<Long> call() throws Exception {
        // data create
        SimulationDataCreate simulationDataCreate = new SimulationDataCreate();
        simulationDataCreate.run(transactionSize, VNodeNum, pairNum, transactionTypeNum);
        ArrayList<Transaction> transactions = simulationDataCreate.getTransactions();
        // transaction attach
        SimulationTransactionAttach simulationTransactionAttach = new SimulationTransactionAttach();
        simulationTransactionAttach.run(transactionSize, VNodeNum, transactions);
        HashMap<String, Transaction> SEBGraphResult = simulationTransactionAttach.getSEBGraphResult();
        HashMap<String, Transaction> IOTAGraphResult = simulationTransactionAttach.getIOTAGraphResult();
        // get orphan height
        double SEBOrphanHeight = getSEBOrphanHeight(SEBGraphResult);
        double IOTAOrphanHeight = getIOTAOrphanHeight(IOTAGraphResult);
        return new ArrayList<Long>(){{add(Math.round(SEBOrphanHeight)); add(Math.round(IOTAOrphanHeight));}};
    }
    private static double getSEBOrphanHeight(HashMap<String, Transaction> TSPSGraphResult){
        double orphanHeightSum = 0d;
        double maxHeight = 0d;
        double minHeight = 10000d;
        int orphanSize = 0;
        for(Map.Entry<String, Transaction> entry: TSPSGraphResult.entrySet()){
            Transaction transaction = entry.getValue();
            if(transaction.getSecurityChildIDList().size() == 0 && transaction.getServiceChildIDList().size() == 0){
                orphanHeightSum += transaction.getSecurityHeight();
                orphanSize++;
                if(transaction.getSecurityHeight() > maxHeight){
                    maxHeight = transaction.getSecurityHeight();
                }
                if(transaction.getSecurityHeight() < minHeight){
                    minHeight = transaction.getSecurityHeight();
                }
            }
        }
        return orphanHeightSum/orphanSize;
    }

    private static double getIOTAOrphanHeight(HashMap<String, Transaction> MCMCGraphResult){
        double orphanHeightSum = 0d;
        double maxHeight = 0d;
        double minHeight = 100000000d;
        int orphanSize = 0;
        for(Map.Entry<String, Transaction> entry: MCMCGraphResult.entrySet()){
            Transaction transaction = entry.getValue();
            if(transaction.getChildIDList().size() == 0){
                orphanHeightSum += transaction.getHeight();
                orphanSize++;
                if(transaction.getHeight() > maxHeight){
                    maxHeight = transaction.getHeight();
                }
                if(transaction.getHeight() < minHeight){
                    minHeight = transaction.getHeight();
                }
            }
        }
        return orphanHeightSum/orphanSize;
    }
}
