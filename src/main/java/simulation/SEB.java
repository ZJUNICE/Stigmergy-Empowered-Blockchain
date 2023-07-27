package simulation;

import model.Transaction;
import model.TransType;
import model.VNode;
import util.Utils;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.util.*;
import java.util.concurrent.*;

/**
 * @program: SEB-Framework
 * @description: SEB
 * @creator: Li DX
 */
public class SEB {
    private int R = 10;
    private int C = 10;
    private Random random = new Random(1);
    private LinkedHashMap<String, Transaction> graphTransaction = new LinkedHashMap<>();
    private HashMap<String, Transaction> outGraphTransaction = new HashMap<>();
    private ArrayList<Transaction> graphTransactionList = new ArrayList<>();
    private LinkedHashMap<String, Transaction> visibleTransactions = new LinkedHashMap<>();
    private LinkedHashMap<String, Transaction> visibleSecurityOrphanList = new LinkedHashMap<>();
    private LinkedHashMap<String, Transaction> visibleServiceOrphanList = new LinkedHashMap<>();
    // Each Round Time (millisecond)
    private final Long T = 60*1000L;
    private long TSPSCostTime = 0L;
    public void set(int R, int C, int randomSeed){
        this.R = R;
        this.C = C;
        random = new Random(randomSeed);
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
            // max transaction size for periodically approval is 3000
            if(i % 3000 == 0){
                this.reset();
            }
            if(strict == 2 && TSPSCostTime > T){
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
            // find orphan/tip transactions
            long t = System.currentTimeMillis();
            getOrphanTransactions();
            // attach transactions
            runTSPS(transaction);
            if(strict == 2){
                Utils.runPow(transaction.toString());
            }
//            Considering the reality that puzzles can be processed in parallel with transaction attach, it is negligible.
            TSPSCostTime += (System.currentTimeMillis() - t);
//            if(i%10 == 0){
//                cpb.show(i);
//            }
        }
        outGraphTransaction.putAll(graphTransaction);
//        cpb.show(transactions.size());
    }
    public long getTSPSCostTime(){
        return TSPSCostTime;
    }
    public long getTps(){
        return (outGraphTransaction.size() * 1000L)/T;
    }
    public long getCtps(int heightNum){
        int size = 0;
        for(Transaction t: graphTransaction.values()){
            if(t.getSecurityHeight() >= heightNum){
                size++;
            }
        }
        return size*1000L/T;
    }
    public HashMap<String, Transaction> getTransactionGraph(){
        return graphTransaction;
    }
    private void reset(){
        outGraphTransaction.putAll(graphTransaction);
        graphTransaction = new LinkedHashMap<>();
        visibleTransactions = new LinkedHashMap<>();
        // create Initial Transactions
        this.createInitialTransactions();
        graphTransactionList = new ArrayList<>(graphTransaction.values());
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
                if(entry.getValue().getCreateTime() < visibleTime) {
                    visibleTransactions.put(entry.getKey(), entry.getValue());
                }
            }
        }
    }

    public HashMap<String, Transaction> getOutGraphTransaction() {
        return outGraphTransaction;
    }

    public void setOutGraphTransaction(HashMap<String, Transaction> outGraphTransaction) {
        this.outGraphTransaction = outGraphTransaction;
    }

    //    private void getOrphanTransactions(){
//        visibleSecurityOrphanList = new HashMap<>();
//        visibleServiceOrphanList = new HashMap<>();
//        ArrayList<String> keySetList = new ArrayList<>(visibleTransactions.keySet());
//        int size = securityOrphanList.size();
//        for(int i = 0; i < size; i++) {
//            String key = securityOrphanList.get(i);
//            if (visibleTransactions.containsKey(key)) {
//                visibleSecurityOrphanList.put(key, visibleTransactions.get(key));
//            } else {
//                if (graphTransaction.get(key).getSecurityParentID() != null) {
//                    String securityParentID = graphTransaction.get(key).getSecurityParentID();
//                    while (!visibleTransactions.containsKey(securityParentID)) {
//                        securityParentID = graphTransaction.get(securityParentID).getSecurityParentID();
//                    }
//                    Transaction transaction1 = visibleTransactions.get(securityParentID);
//                    if(!ifInside(keySetList, transaction1.getSecurityChildIDList())) {
//                        visibleSecurityOrphanList.put(securityParentID, transaction1);
//                    }
//                }
//            }
//        }
//        size = serviceOrphanList.size();
//        for(int i = 0; i< size; i++){
//            String key = serviceOrphanList.get(i);
//            if(visibleTransactions.containsKey(key)){
//                visibleServiceOrphanList.put(key, visibleTransactions.get(key));
//            }else{
//                if(graphTransaction.get(key).getSecurityParentID() != null) {
//                    String serviceParentID = graphTransaction.get(key).getServiceParentID();
//                    while (!visibleTransactions.containsKey(serviceParentID)) {
//                        serviceParentID = graphTransaction.get(serviceParentID).getServiceParentID();
//                    }
//                    Transaction transaction1 = visibleTransactions.get(serviceParentID);
//                    if (!ifInside(keySetList, transaction1.getServiceChildIDList())) {
//                        visibleServiceOrphanList.put(serviceParentID, transaction1);
//                    }
//                }
//            }
//        }
//    }
    private void getOrphanTransactions(){
        visibleServiceOrphanList = new LinkedHashMap<>();
        visibleSecurityOrphanList = new LinkedHashMap<>();
        Set<String> keySetList = visibleTransactions.keySet();
        for(Map.Entry<String, Transaction> entry: visibleTransactions.entrySet()){
            Transaction transaction = entry.getValue();
            if(Collections.disjoint(keySetList, new LinkedHashSet<>(transaction.getServiceChildIDList()))){
                visibleServiceOrphanList.put(entry.getKey(), transaction);
            }
            if(Collections.disjoint(keySetList, new LinkedHashSet<>(transaction.getSecurityChildIDList()))){
                visibleSecurityOrphanList.put(entry.getKey(),transaction);
            }
        }
    }
    private void runTSPS(Transaction transaction){
        double attractIndex = 0D;
        ArrayList<String> servicePId = new ArrayList<>();
        // find service parent transaction
        for(String orphanKey: visibleServiceOrphanList.keySet()){
            double orphanAttractiveIndex = 0D;
            Transaction tt = visibleServiceOrphanList.get(orphanKey);
            for(int j=0; j < R+1; j++){
                assert tt != null;
                orphanAttractiveIndex = orphanAttractiveIndex + CalAttractiveIndex(transaction, tt, j);
                if(tt.getServiceParentID() == null || tt.getServiceParentID().isEmpty()){
                    break;
                }
                tt = visibleTransactions.get(tt.getServiceParentID());
            }
            if(orphanAttractiveIndex > attractIndex){
                servicePId.clear();
                servicePId.add(orphanKey);
                attractIndex = orphanAttractiveIndex;
            }else if (orphanAttractiveIndex == attractIndex){
                servicePId.add(orphanKey);
            }
        }
        String finalServicePId = getMinHeightNode(servicePId, visibleServiceOrphanList);
        transaction.setServiceParentID(finalServicePId);
        Transaction addOrphanTransaction = visibleTransactions.get(finalServicePId);
        transaction.setServiceHeight(addOrphanTransaction.getServiceHeight() + 1);

        Long maxSecurityHeight = 0L;
        ArrayList<String> SecurityPId = new ArrayList<>();
        // find security parent transaction
        for(String orphanKey: visibleSecurityOrphanList.keySet()) {
            Transaction tt = visibleSecurityOrphanList.get(orphanKey);
            if (tt.getSecurityHeight() > maxSecurityHeight) {
                maxSecurityHeight = tt.getSecurityHeight();
                SecurityPId.clear();
                SecurityPId.add(orphanKey);
            } else if (tt.getSecurityHeight().equals(maxSecurityHeight)) {
                SecurityPId.add(orphanKey);
            }
        }
        int nn = random.nextInt(SecurityPId.size());
        String finalSecurityPId = SecurityPId.get(nn);
        transaction.setSecurityParentID(finalSecurityPId);
        transaction.setSecurityHeight(maxSecurityHeight + 1);
        if(checkSecurityParent(transaction) && checkServiceParent(transaction, addOrphanTransaction)){
            visibleTransactions.get(finalServicePId).addServiceChildID(transaction.getTxID());
            visibleTransactions.get(finalSecurityPId).addSecurityChildID(transaction.getTxID());
            graphTransaction.put(transaction.getTxID(), transaction);
        }
    }
    private void createInitialTransactions(){
        Transaction initialTransaction = new Transaction(null,null, UUID.randomUUID().toString(), null,  new VNode(null,null,0),0L);
        initialTransaction.setServiceHeight(0L);
        initialTransaction.setSecurityHeight(0L);
        graphTransaction.put(initialTransaction.getTxID(), initialTransaction);
        // 初始节点，四类业务
        for(int k = 0; k < 4; k++) {
            TransType t1 = new TransType();
            t1.setTransactionType(k+1);
            t1.setTransactionIndicator(0);
            Transaction transaction = new Transaction(null, null, UUID.randomUUID().toString(), t1,  new VNode(null,null,0), 0L);
            transaction.setServiceHeight(1L);
            transaction.setSecurityHeight(1L);
            transaction.setServiceParentID(initialTransaction.getTxID());
            transaction.setSecurityParentID(initialTransaction.getTxID());
            initialTransaction.addServiceChildID(transaction.getTxID());
            initialTransaction.addSecurityChildID(transaction.getTxID());
            graphTransaction.put(transaction.getTxID(), transaction);
        }
    }
    private boolean ifInside(Set<String> allString, ArrayList<String> findString){
        if(findString.size() > 0) {
            for (String ele : findString) {
                if (allString.contains(ele)) {
                    return true;
                }
            }
        }
        return false;
    }
    private boolean checkSecurityParent(Transaction transaction){
        long threshold = 1;
        if(transaction.getSecurityHeight() < threshold){
            return false;
        }else{
            return true;
        }
    }
    private  boolean checkServiceParent(Transaction transaction, Transaction parentTransaction){
        double threshold = 0.5;
        double attractiveIndex = CalAttractiveIndex(transaction, parentTransaction, 1);
        if(attractiveIndex < threshold){
            return false;
        }else{
            return true;
        }
    }
    /**
     * calculate attractive index
     * @param t1
     * @param t2
     * @param i
     * @return
     */
    private Double CalAttractiveIndex(Transaction t1, Transaction t2, int i){
        TransType transType1 = t1.getType();
        TransType transType2 = t2.getType();
        if(transType1 != null && transType2 != null && transType1.getTransactionType().equals(transType2.getTransactionType())){
            if(transType1.getTransactionIndicator().equals(transType2.getTransactionIndicator())){
                return 1*Math.pow(Math.E, -i*i/(2F*C*C));
            }else{
                return Math.pow(Math.E, -1/(2F*C*C))*Math.pow(Math.E, -i*i/(2F*C*C));
            }
        }else{
            return 0D;
        }
    }
    /**
     * get min height transaction node
     * @param trade2ArrayList
     * @param transactionList
     * @return
     */
    private String getMinHeightNode(ArrayList<String> trade2ArrayList, LinkedHashMap<String, Transaction> transactionList){
        ArrayList<String> minTradeList = new ArrayList<>();
        Long maxHeight = 0L;
        for(int i =0; i< trade2ArrayList.size(); i++){
            Transaction t = transactionList.get(trade2ArrayList.get(i));
            if(t.getServiceHeight() < maxHeight || maxHeight == 0L){
                minTradeList.clear();
                maxHeight = t.getServiceHeight();
                minTradeList.add(t.getTxID());
            }else if(t.getServiceHeight().equals(maxHeight)){
                minTradeList.add(t.getTxID());
            }
        }
        return minTradeList.get(random.nextInt(minTradeList.size()));
    }
}
