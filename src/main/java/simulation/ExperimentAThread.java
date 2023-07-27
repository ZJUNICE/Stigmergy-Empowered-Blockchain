package simulation;

import model.Transaction;
import util.TransactionSearch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * @program: SEB-Framework
 * @description: Transaction Data Create Thread
 * @creator: Li DX
 */
public class ExperimentAThread implements Callable<ArrayList<ArrayList<Long>>> {
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
    public ArrayList<ArrayList<Long>> call() throws Exception {
        // data create
        SimulationDataCreate simulationDataCreate = new SimulationDataCreate();
        simulationDataCreate.run(transactionSize, VNodeNum, pairNum, transactionTypeNum);
        ArrayList<Transaction> transactions = simulationDataCreate.getTransactions();
        HashMap<Integer, ArrayList<ArrayList<String>>> pairs = simulationDataCreate.getPairs();
        // transaction attach
        SimulationTransactionAttach simulationTransactionAttach = new SimulationTransactionAttach();
        simulationTransactionAttach.run(transactionSize, VNodeNum, transactions);
        HashMap<String, Transaction> SEBGraphResult = simulationTransactionAttach.getSEBGraphResult();
        HashMap<String, Transaction> IOTAGraphResult = simulationTransactionAttach.getIOTAGraphResult();

        ArrayList<Long> SEBAvgResult = new ArrayList<>();
        ArrayList<Long> IOTAAvgResult = new ArrayList<>();
//                log.info("Search Start");
        for (Integer key : pairs.keySet()) {
            ArrayList<ArrayList<String>> tradePairs = pairs.get(key);
            int size = tradePairs.size();
            double sumSEB = 0d;
            double sumIOTA = 0d;
            for (int k = 0; k < size; k++) {
                ArrayList<String> tradePair = tradePairs.get(k);
                Transaction start = SEBGraphResult.get(tradePair.get(0));
                Transaction end = SEBGraphResult.get(tradePair.get(1));
                sumSEB += TransactionSearch.search(start, end, SEBGraphResult, false);
                start = IOTAGraphResult.get(tradePair.get(0));
                end = IOTAGraphResult.get(tradePair.get(1));
                sumIOTA += TransactionSearch.search(start, end, IOTAGraphResult, true);
            }
            SEBAvgResult.add(Math.round(sumSEB / size));
            IOTAAvgResult.add(Math.round(sumIOTA / size));
        }
        return new ArrayList<ArrayList<Long>>(){{add(SEBAvgResult); add(IOTAAvgResult);}};
    }
}
