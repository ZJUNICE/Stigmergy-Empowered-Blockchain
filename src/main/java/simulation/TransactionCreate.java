package simulation;

import model.Transaction;
import model.VNode;
import org.apache.commons.math3.distribution.ExponentialDistribution;
import org.apache.commons.math3.random.Well19937c;
import util.TypeGetter;

import java.util.*;
import java.util.stream.Collectors;

public class TransactionCreate {
    // Cloud Number
    private Integer cloudNum = 10;
    // MEC Number
    private Integer mecNum = 20;
    // Operator Number
    private Integer operatorNum = 10;
    // VNode Number
    private Integer VNodeNum = 10000;
    // RSU Number
    private Integer RSUNum = 30;
    // Satellite Number
    private Integer satelliteNum = 10;
    // UAV Number
    private Integer UAVNum =20;
    // Each Round Time (millisecond)
    private final Long T = 60*1000L;
    // Transaction Round Time for each Unit Time
    private final int roundTime = 3;
    // Random
    private final Random numR;
    // Transaction TransType Number
    private Integer transactionTypeNum = 4;
    // Transaction Indicator Number
    private final Integer transactionIndicatorNum = 2;
    // Initial Time
    private long initialTime = System.currentTimeMillis();
    // seed
    private int seed = 200;
    // Transaction TransType Getter
    private TypeGetter typeGetter = new TypeGetter(transactionTypeNum,transactionIndicatorNum);
    private HashMap<Integer, short[]> vNodeDelayMatrix;
    private int rt = 0;
    private final int delayUnit = 1000;
    private double[][] vNodeLocations;
    public TransactionCreate(int seed){
        numR = new Random(seed);
    }
    public TransactionCreate(Integer VNodeNum, Integer transactionTypeNum, int seed){
        this.cloudNum  = VNodeNum/1000;
        this.mecNum = VNodeNum/500;
        this.operatorNum = VNodeNum/1000;
        this.VNodeNum = VNodeNum;
        this.transactionTypeNum = transactionTypeNum;
        this.seed = seed;
        this.numR = new Random(this.seed);
        this.RSUNum = VNodeNum*3/1000;
        this.satelliteNum = VNodeNum/1000;
        this.UAVNum = VNodeNum/500;
        this.vNodeDelayMatrix = new HashMap<>();
        this.vNodeLocations = new double[VNodeNum][2];
        typeGetter = new TypeGetter(transactionTypeNum,transactionIndicatorNum);
    }
    public void renewTime(){
        initialTime = System.currentTimeMillis();
    }
    private boolean randomPer(float per){
        Random random = new Random(this.seed);
        float val = random.nextFloat();
        return val <= per;
    }
    private ArrayList<Integer> getRandomPerList(int total, float per) {
        ArrayList<Integer> oList = new ArrayList<>();
        ArrayList<Integer> rawList = new ArrayList<>();
        float count = total*per;
        for (int j = 0; j< total; j++){
            rawList.add(j);
        }
        if(per >= 1.0){
            return rawList;
        }else {
            for (int i = 0; i < count; i++) {
                int intRandom = this.numR.nextInt(rawList.size() - 1);
                oList.add(rawList.get(intRandom));
                rawList.remove(rawList.get(intRandom));
            }
            return oList;
        }
    }
    private List<Long> createArrivalTime(int transactionNum){
        ExponentialDistribution eType = new ExponentialDistribution(new Well19937c(this.seed), Double.valueOf(T)/transactionNum);
        double[] arrivalIntervalType = eType.sample(transactionNum);
        Arrays.parallelPrefix(arrivalIntervalType, Double::sum);
        List<Double> out = Arrays.stream(arrivalIntervalType).boxed().collect(Collectors.toList());
        List<Long> out2 =out.stream().map(Double::longValue).collect(Collectors.toList());
        Collections.shuffle(out);
        return out2;
    }
    public ArrayList<Transaction> nextRound(){
        ArrayList<Transaction> transactionArrayList = new ArrayList<>();
        ArrayList<Integer> type1RandomList = this.getRandomPerList(VNodeNum, 0.03f);
        ArrayList<Integer> type2RandomList = this.getRandomPerList(VNodeNum, 0.01f);
        ArrayList<Integer> type3RandomList = this.getRandomPerList(VNodeNum, 0.12f);
        ArrayList<Integer> type4RandomList = this.getRandomPerList(VNodeNum, 0.04f);
        long createTime = initialTime + rt*T;
        // Create Transaction Arrival/Create Times
        // type1 context service
        List<Long> arrivalIntervalType1 = createArrivalTime(type1RandomList.size());
        int timeIntervalNum1 = 0;
        // type2 computing offloading
        List<Long> arrivalIntervalType2 = createArrivalTime(type2RandomList.size()*roundTime);
        int timeIntervalNum2 = 0;
        // type3 telecom service
        List<Long> arrivalIntervalType3 = createArrivalTime(type3RandomList.size());
        int timeIntervalNum3 = 0;
        // type4 data sharing service
        List<Long> arrivalIntervalType4 = createArrivalTime(type4RandomList.size()*roundTime);
        int timeIntervalNum4 = 0;

        for (int j = 0; j < VNodeNum; j++) {
            String VNodeName = "VNode-" + j;
            String cloudName;
            String mecName;
            String operatorName;
            String rsuName;
            String satelliteName;
            String UAVName;
            Transaction newTransaction;
            VNode vNode = new VNode(VNodeName, vNodeDelayMatrix.get(j), j);
            // VNode => MEC => type1 context service
            if(type1RandomList.contains(j)){
                mecName = "Mec-" + numR.nextInt(mecNum);
                newTransaction = new Transaction(VNodeName, mecName, UUID.randomUUID().toString(), typeGetter.getRandomType(1), vNode,createTime + arrivalIntervalType1.get(timeIntervalNum1));
                timeIntervalNum1++;
                transactionArrayList.add(newTransaction);
            }

            // VNode => MEC or CLOUD => type2 computing offloading
            if(type2RandomList.contains(j)){
                for(int k =0 ;k < roundTime; k++){
                    if(randomPer(1/2.0f)){
                        mecName = "Mec-" + numR.nextInt(mecNum);
                        newTransaction = new Transaction(VNodeName, mecName, UUID.randomUUID().toString(), typeGetter.getRandomType(2), vNode,createTime + arrivalIntervalType2.get(timeIntervalNum2));
                        timeIntervalNum2++;
                        transactionArrayList.add(newTransaction);
                    }else{
                        cloudName = "Cloud-" + numR.nextInt(cloudNum);
                        newTransaction = new Transaction(VNodeName, cloudName, UUID.randomUUID().toString(), typeGetter.getRandomType(2),vNode, createTime + arrivalIntervalType2.get(timeIntervalNum2));
                        timeIntervalNum2++;
                        transactionArrayList.add(newTransaction);
                    }
                }
            }

            // VNode => Operator or RSU or satellite => type3 telecom service
            if(type3RandomList.contains(j)){
                if(randomPer(1/3.0f)) {
                    operatorName = "Operator-"+ numR.nextInt(operatorNum);
                    newTransaction = new Transaction(VNodeName, operatorName, UUID.randomUUID().toString(), typeGetter.getRandomType(3), vNode,createTime + arrivalIntervalType3.get(timeIntervalNum3));
                    timeIntervalNum3++;
                    transactionArrayList.add(newTransaction);
                }else {
                    if(randomPer(1/2.0f)) {
                        rsuName =  "RSU-"+ numR.nextInt(RSUNum);
                        newTransaction = new Transaction(VNodeName, rsuName, UUID.randomUUID().toString(), typeGetter.getRandomType(3), vNode,createTime + arrivalIntervalType3.get(timeIntervalNum3));
                        timeIntervalNum3++;
                        transactionArrayList.add(newTransaction);
                    }else{
                        satelliteName = "Satellite-"+ numR.nextInt(satelliteNum);
                        newTransaction = new Transaction(VNodeName, satelliteName, UUID.randomUUID().toString(), typeGetter.getRandomType(3), vNode,createTime + arrivalIntervalType3.get(timeIntervalNum3));
                        timeIntervalNum3++;
                        transactionArrayList.add(newTransaction);
                    }
                }
            }
            // VNode => VNode or MEC or Cloud or RSU or UAV => type4 data sharing service
            if(type4RandomList.contains(j)){
                for(int k =0 ;k < roundTime; k++) {
                    if(randomPer(1/5.0f)) {
                        int nn = numR.nextInt(VNodeNum - 1);
                        nn = nn >= j? nn+1: nn;
                        String destVNodeName = "VNode-" + nn;
                        newTransaction = new Transaction(VNodeName, destVNodeName, UUID.randomUUID().toString(), typeGetter.getRandomType(4), vNode,createTime + arrivalIntervalType4.get(timeIntervalNum4));
                        timeIntervalNum4++;
                        transactionArrayList.add(newTransaction);
                    }else {
                        if(randomPer(1/4.0f)) {
                            mecName = "Mec-" + numR.nextInt(mecNum);
                            newTransaction = new Transaction(VNodeName, mecName, UUID.randomUUID().toString(), typeGetter.getRandomType(4), vNode,createTime + arrivalIntervalType4.get(timeIntervalNum4));
                            timeIntervalNum4++;
                            transactionArrayList.add(newTransaction);
                        }else{
                            if(randomPer(1/3.0f)) {
                                cloudName = "Cloud-" + numR.nextInt(cloudNum);
                                newTransaction = new Transaction(VNodeName, cloudName, UUID.randomUUID().toString(), typeGetter.getRandomType(4), vNode,createTime + arrivalIntervalType4.get(timeIntervalNum4));
                                timeIntervalNum4++;
                                transactionArrayList.add(newTransaction);
                            }else{
                                if(randomPer(1/2.0f)) {
                                    rsuName = "RSU-" + numR.nextInt(RSUNum);
                                    newTransaction = new Transaction(VNodeName, rsuName, UUID.randomUUID().toString(), typeGetter.getRandomType(4), vNode,createTime + arrivalIntervalType4.get(timeIntervalNum4));
                                    timeIntervalNum4++;
                                    transactionArrayList.add(newTransaction);
                                }else{
                                    UAVName = "UAV-" + numR.nextInt(UAVNum);
                                    newTransaction = new Transaction(VNodeName, UAVName, UUID.randomUUID().toString(), typeGetter.getRandomType(4), vNode,createTime + arrivalIntervalType4.get(timeIntervalNum4));
                                    timeIntervalNum4++;
                                    transactionArrayList.add(newTransaction);
                                }
                            }
                        }
                    }
                }
            }
        }
        transactionArrayList.sort(Comparator.comparing(Transaction::getCreateTime));
        rt++;
        return transactionArrayList;
    }
    public HashMap<Integer, ArrayList<ArrayList<String>>> getRandomPair(ArrayList<Transaction> transactions, Integer pairNum){
        HashMap<Integer, ArrayList<Transaction>> transactionTypeMaps = new HashMap<>();
        HashMap<Integer, ArrayList<ArrayList<String>>> output = new HashMap<>();
        Random pairR = new Random(this.seed);
        for(Transaction eleTransaction : transactions){
            if(!transactionTypeMaps.containsKey(eleTransaction.getType().getTransactionType())){
                transactionTypeMaps.put(eleTransaction.getType().getTransactionType(), new ArrayList<>());
            }
            transactionTypeMaps.get(eleTransaction.getType().getTransactionType()).add(eleTransaction);
        }
        for(Integer key: transactionTypeMaps.keySet()){
            ArrayList<Transaction> transactionArray = transactionTypeMaps.get(key);
            ArrayList<ArrayList<String>> tradeArrayIn = new ArrayList<>();
            int n = transactionArray.size();
            for(int i =0; i< pairNum; i++){
                ArrayList<String> tradePair = new ArrayList<>();
                int a = pairR.nextInt(n);
                int b = pairR.nextInt(n);
                while(a == b){
                    b = pairR.nextInt(n);
                }
                tradePair.add(transactionArray.get(a).getTxID());
                tradePair.add(transactionArray.get(b).getTxID());
                tradeArrayIn.add(tradePair);
            }
            output.put(key, tradeArrayIn);
        }
        return output;
    }
    public void setUniformLocation(){
        Random random = new Random(this.seed);
        for(int i = 0; i < VNodeNum; i++){
            double x = 0.5 - random.nextDouble();
            double y = 0.5 - random.nextDouble();
            vNodeLocations[i][0] = x;
            vNodeLocations[i][1] = y;
        }
        this.updateVNodeDelayMatrix();
    }
    public void setGaussianLocation(){
        Random random = new Random(this.seed);
        for(int i = 0; i < VNodeNum; i++){
            double x = random.nextGaussian();
            double y = random.nextGaussian();
            if(x<-3){
                x = -3d;
            }
            if(x>3){
                x = 3d;
            }
            if(y<-3){
                y = -3d;
            }
            if(y>3){
                y = 3d;
            }
            vNodeLocations[i][0] = x/6;
            vNodeLocations[i][1] = y/6;
        }
        this.updateVNodeDelayMatrix();
    }
    public void updateVNodeDelayMatrix(){
        vNodeDelayMatrix = new HashMap<>();
        for(int i = 0; i < VNodeNum; i++){
            Integer m = i;
            for(int j = 0; j < VNodeNum; j++){
                double dist = calDist(vNodeLocations[i], vNodeLocations[j]);
                if(vNodeDelayMatrix.containsKey(m)){
                    vNodeDelayMatrix.get(m)[j] = (short) Math.round(dist*delayUnit);
                }else{
                    short[] element = new short[VNodeNum];
                    element[j] = (short) Math.round(dist*delayUnit);
                    vNodeDelayMatrix.put(m, element);
                }
            }
        }
    }

    private double calDist(double[] l1, double[] l2){
        return Math.sqrt(Math.pow(l1[0]-l2[0], 2)+(Math.pow(l1[1]-l2[1], 2)));
    }
}
