package simulation;

import com.alibaba.fastjson.JSONObject;
import model.Transaction;
import util.Config;
import util.DataFile;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @program: SEB-Framework
 * @description: Simulation Data Create
 * @creator: Li DX
 */
@lombok.extern.slf4j.Slf4j
public class SimulationDataCreate {
    private ArrayList<Transaction> transactions = new ArrayList<>();
    private HashMap<Integer, ArrayList<ArrayList<String>>> pairs;
    private Integer transactionSize;
    private Integer VNodeNum;
    private int pairNum;
    public void run(Integer transactionSize, Integer VNodeNum, int pairNum, Integer transactionTypeNum) throws Exception {
        this.transactionSize = transactionSize;
        this.VNodeNum = VNodeNum;
        this.pairNum = pairNum;
        long t1 = System.currentTimeMillis();
        int seed = Config.seed;
        ArrayList<Transaction> out = new ArrayList<>();
        // Create Data
        TransactionCreate transactionCreate = new TransactionCreate(VNodeNum, transactionTypeNum, seed);
        if(Config.distributionType.equals("Gaussian")){
            transactionCreate.setGaussianLocation();
        }else if(Config.distributionType.equals("Uniform")){
            transactionCreate.setUniformLocation();
        }

        while(out.size() < transactionSize){
            out.addAll(transactionCreate.nextRound());
        }
        transactions = new ArrayList<>(out.subList(0, this.transactionSize));
        transactions.sort(Comparator.comparing(Transaction::getCreateTime));
        pairs = transactionCreate.getRandomPair(transactions, pairNum);
        long t2 = System.currentTimeMillis();
//        log.info("VNodeNum Size: " + VNodeNum + "; Transaction Size: " + transactionSize + "; Transaction Create Time cost: " + (t2 - t1));

    }

    public ArrayList<Transaction> getTransactions() {
        return transactions;
    }

    public HashMap<Integer, ArrayList<ArrayList<String>>> getPairs() {
        return pairs;
    }

    public void saveToFile(){
        String fileName = String.format("Transaction-Data-%d-%d", VNodeNum, transactionSize);
        DataFile.saveDataToFile(fileName, JSONObject.toJSONString(transactions));
        String fileName2 = String.format("Transaction-Pair-Data-%d-%d-%d", VNodeNum, transactionSize, pairNum);
        DataFile.saveDataToFile(fileName2, JSONObject.toJSONString(pairs));
    }
}