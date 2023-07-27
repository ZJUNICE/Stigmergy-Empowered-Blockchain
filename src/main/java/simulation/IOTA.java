package simulation;

import model.Transaction;
import model.TransType;
import model.VNode;
import util.Utils;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.*;

/**
 * @program: SEB-Framework
 * @description:
 * @creator: Li DX
 */
public class IOTA {
    private double alpha = 0.001;
    private Random random = new Random(2);
    private LinkedHashMap<String, Transaction> graphTransaction = new LinkedHashMap<>();
    private ArrayList<Transaction> graphTransactionList = new ArrayList<>();
    private HashMap<String, Transaction> outGraphTransaction = new HashMap<>();
    private LinkedHashMap<String, Transaction> visibleTransactions = new LinkedHashMap<>();
    private String initialTransactionID = "";
    private HashMap<String, Transaction> orphanList = new HashMap<>();
    private ArrayList<String> searchTransactions = new ArrayList<>();
    private long tsaCostTime = 0L;
    // Each Round Time (millisecond)
    private final Long T = 60*1000L;
    private int confirmThreshold = 150;
    public void set(double alpha, int randomSeed){
        this.alpha = alpha;
        this.random = new Random(randomSeed);
    }
    public void run(ArrayList<Transaction> transactions, String jobName) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        run(transactions, jobName, 0);
    }

    /**
     *
     * @param transactions
     * @param jobName
     * @param strict: 0 for ExperimentA and B; 1 for ExperimentC which make all transactions visible; 2 for ExperimentD: program exit when cost time exceed the round time T
     * @throws UnsupportedEncodingException
     * @throws NoSuchAlgorithmException
     */
    public void run(ArrayList<Transaction> transactions, String jobName, int strict) throws UnsupportedEncodingException, NoSuchAlgorithmException {
//        ConsoleProgressBarUtils cpb = new ConsoleProgressBarUtils(100,transactions.size(), jobName);
        int transactionSize = transactions.size();
        for(int i =0; i < transactionSize; i++){
            if(i % 3000 == 0){
                this.reset();
            }
            if(strict == 2 && tsaCostTime > T){
                break;
            }
            Transaction transaction = transactions.get(i);
            Long arrivalTime = transaction.getCreateTime();
            // find visible transactions
            if(strict >= 1){
                visibleTransactions = graphTransaction;
            }else {
                getVisibleTransactions(arrivalTime, transaction.getvNode());
            }
            // attach transactions
            long t = System.currentTimeMillis();
            // Considering the reality that puzzles can be processed in parallel with transaction attach, it is negligible.
            runTSA(transaction);
            if(strict == 2){
                Utils.runPow(transaction.toString());
            }
//            System.out.println(i+": "+visibleTransactions.size());
            tsaCostTime += (System.currentTimeMillis() - t);
//            if(i%10 == 0){
//                cpb.show(i);
//            }

        }
//        cpb.show(transactions.size());
        outGraphTransaction.putAll(graphTransaction);
    }

    public long getTsaCostTime(){
        return tsaCostTime;
    }
    public long getTps(){
        return outGraphTransaction.size()* 1000L/T;
    }
    public long getCtps(Integer confirmThreshold){
        this.confirmThreshold = confirmThreshold;
        return this.getCtps();
    }
    public long getCtps(){
        int size = 0;
        for(Transaction t: outGraphTransaction.values()){
            if(t.getWeight() >= confirmThreshold){
                size++;
            }
        }
        return size*1000L/T;
    }

    private void reset(){
        outGraphTransaction.putAll(graphTransaction);
        graphTransaction = new LinkedHashMap<>();
        visibleTransactions = new LinkedHashMap<>();
        orphanList = new HashMap<>();
        searchTransactions = new ArrayList<>();
        // create Initial Transactions
        this.createInitialTransactions();
        graphTransactionList = new ArrayList<>(graphTransaction.values());
    }

    private void createInitialTransactions(){
        Transaction initialTransaction = new Transaction(null,null, UUID.randomUUID().toString(), null, new VNode(null,null,0),0L);
        initialTransactionID = initialTransaction.getTxID();
        initialTransaction.setHeight(0L);
        graphTransaction.put(initialTransaction.getTxID(), initialTransaction);
        // 初始节点，四类业务
        for(int k = 0; k < 4; k++) {
            TransType t1 = new TransType();
            t1.setTransactionType(k+1);
            t1.setTransactionIndicator(0);
            Transaction transaction = new Transaction(null, null, UUID.randomUUID().toString(), t1, new VNode(null,null,0), 0L);
            transaction.setHeight(1L);
            transaction.setParent1ID(initialTransaction.getTxID());
            transaction.setParent2ID(initialTransaction.getTxID());
            initialTransaction.addChildID(transaction.getTxID());
            graphTransaction.put(transaction.getTxID(), transaction);
        }
    }

    private void getVisibleTransactions(Long arrivalTime, VNode transactionVNode){
        visibleTransactions = new LinkedHashMap<>();
        short[] delays = transactionVNode.getDelays();
        for(Map.Entry<String, Transaction> entry: graphTransaction.entrySet()){
            // initial transactions are always visible
            if(entry.getValue().getSenderID() == null){
                visibleTransactions.put(entry.getKey(), entry.getValue());
            }else {
                long visibleTime = arrivalTime - delays[entry.getValue().getvNode().getvNodeNum()];
                if(entry.getValue().getCreateTime() < visibleTime){
                    visibleTransactions.put(entry.getKey(), entry.getValue());
                }
            }
        }
    }

    private void getOrphanTransactions(){
        orphanList = new HashMap<>();
        ArrayList<String> keySetList = new ArrayList<>(visibleTransactions.keySet());
        for(Map.Entry<String, Transaction> entry: visibleTransactions.entrySet()) {
            Transaction transaction = entry.getValue();
            if (transaction.getChildIDList().size() == 0 || !ifInside(keySetList, transaction.getServiceChildIDList())) {
                orphanList.put(transaction.getTxID(), transaction);
            }
        }

    }

    public HashMap<String, Transaction> getOutGraphTransaction() {
        return outGraphTransaction;
    }

    public void setOutGraphTransaction(HashMap<String, Transaction> outGraphTransaction) {
        this.outGraphTransaction = outGraphTransaction;
    }

    private void runTSA(Transaction transaction){
        String p1ID = mcmc();
        String p2ID = mcmc();
        transaction.setParent1ID(p1ID);
        transaction.setParent2ID(p2ID);
        visibleTransactions.get(p1ID).addChildID(transaction.getTxID());
        visibleTransactions.get(p2ID).addChildID(transaction.getTxID());
        long newHeight = Math.max(visibleTransactions.get(p1ID).getHeight(), visibleTransactions.get(p2ID).getHeight()) + 1;
        transaction.setHeight(newHeight);
        // update Weights
        getTransactionsList(transaction);
        updateVisibleTransactionWeights();
        graphTransaction.put(transaction.getTxID(), transaction);
    }

    /**
     * update transaction weights
     * @param 
     * @return
     */
    public void updateVisibleTransactionWeights(){
        for(String tID: searchTransactions){
            visibleTransactions.get(tID).updateWeight();
        }
    }
    // get transactions between new transaction and initial transaction
    private void getTransactionsList(Transaction startTransactionID){
        searchTransactions = new ArrayList<>();
        searchTransaction(startTransactionID);
    }

    private void searchTransaction(Transaction t){
        if(t.getParent1ID() != null){
            if(!searchTransactions.contains(t.getParent1ID())){
                searchTransactions.add(t.getParent1ID());
                searchTransaction(visibleTransactions.get(t.getParent1ID()));
            }
        }
        if(t.getParent2ID() != null){
            if(!searchTransactions.contains(t.getParent2ID())){
                searchTransactions.add(t.getParent2ID());
                searchTransaction(visibleTransactions.get(t.getParent2ID()));
            }
        }
    }

    private boolean ifInsideMap(HashMap<String, Transaction> transactionHashMap, Transaction t){
        ArrayList<String> childList = t.getServiceChildIDList();
        boolean output = true;
        for(String id: childList){
            if(!transactionHashMap.containsKey(id)){
                output = false;
                break;
            }
        }
        return output;
    }

    /**
     * mcmc walk
     * @param
     * @param
     * @return
     */
    public String mcmc(){
        Transaction mcmcTransaction = visibleTransactions.get(initialTransactionID);
        while(mcmcTransaction.getChildIDList().size() >0){
            Double fatherWeight = mcmcTransaction.getWeight();
            Double cumulativeExp = 0D;
            ArrayList<Double> pList = new ArrayList<>();
            ArrayList<Double> weightList = new ArrayList<>();
            ArrayList<String> IDList = new ArrayList<>();
            for(String childId: mcmcTransaction.getChildIDList()){
                if(visibleTransactions.containsKey(childId)) {
                    Transaction transaction = visibleTransactions.get(childId);
                    double val = Math.pow(Math.E, -alpha * (fatherWeight - transaction.getWeight()));
                    cumulativeExp += val;
                    weightList.add(cumulativeExp);
                    IDList.add(childId);
                }
            }
            for(Double val: weightList){
                Double pxy = val/cumulativeExp;
                pList.add(pxy);
            }
            if(pList.size() == 0){
                break;
            }
            String childTradeID = IDList.get(randomChoose(pList));
            mcmcTransaction = visibleTransactions.get(childTradeID);
        }
        return mcmcTransaction.getTxID();
    }

    /**
     * choose by weight
     * @param weightList
     * @return
     */
    public int randomChoose(ArrayList<Double> weightList){
        double randomVal = random.nextDouble();
        double min = 0D;
        int output = 0;
        for(int i = 0; i < weightList.size(); i++){
            Double v = weightList.get(i);
            if(randomVal>min && randomVal<= v){
                output = i;
                break;
            }
            min = v;
        }
        return output;
    }

    private boolean ifInside(ArrayList<String> allString, ArrayList<String> findString){
        for(String ele: findString){
            if(allString.contains(ele)){
                return true;
            }
        }
        return false;
    }
    public HashMap<String, Transaction> getTransactionGraph(){
        return graphTransaction;
    }
}
