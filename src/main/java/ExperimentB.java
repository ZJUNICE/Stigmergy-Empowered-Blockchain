import com.alibaba.fastjson.JSON;
import model.Transaction;
import simulation.ExperimentBThread;
import util.Config;
import util.ConsoleProgressBarUtils;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @program: SEB-Framework
 * @description: Simulation Test Orphan Height
 * @creator: Li DX
 */
@lombok.extern.slf4j.Slf4j
public class ExperimentB {
    public static void main(String[] args) throws Exception {
        ArrayList<Integer> transactionSizeList = Config.sizeList(100, 3000, 30);
        ArrayList<Integer> VNodeNumList = Config.VNodeNumList(5000,20000,4);
        int transactionTypeNum = Config.transactionTypeNum;
        int pairNum = Config.transactionPairs;
        ExecutorService fixedExecutor = Executors.newFixedThreadPool(1);
        ArrayList<Future<ArrayList<Long>>> results = new ArrayList<>();
        String distributionType = Config.distributionType;
        for(int i = 0; i < Objects.requireNonNull(VNodeNumList).size(); i++) {
            Integer VNodeNum = VNodeNumList.get(i);
            ArrayList<Long> SEBHeightList = new ArrayList<>();
            ArrayList<Long> IOTHeightList = new ArrayList<>();
            results.clear();
            ConsoleProgressBarUtils cpb = new ConsoleProgressBarUtils(100, transactionSizeList.size(), "ExperimentB");
            for (int j = 0; j < Objects.requireNonNull(transactionSizeList).size(); j++) {
                Integer transactionSize = transactionSizeList.get(j);
//                log.info("Transaction Size: " + transactionSize + " VNode Number: "+ VNodeNum + " Start!");
                ExperimentBThread experimentBThread = new ExperimentBThread();
                experimentBThread.setParameters(VNodeNum, transactionSize, transactionTypeNum, pairNum);
                Future<ArrayList<Long>> result = fixedExecutor.submit(experimentBThread);
                results.add(result);
//                log.info("SEB Orphan Height: " + TSPSOrphanHeight);
//                log.info("IOTA Orphan Height: " + MCMCOrphanHeight);
//                log.info("Transaction Size: " + transactionSize + " VNode Number: "+ VNodeNum + " Finished!");
            }
            int k = 1;
            for(Future<ArrayList<Long>> f : results){
                ArrayList<Long> resultValue = f.get();
                SEBHeightList.add(resultValue.get(0));
                IOTHeightList.add(resultValue.get(1));
                cpb.show(k);
                k++;
            }
            log.info("*********************** Transaction Size: " + JSON.toJSONString(transactionSizeList) + " VNode Number: "+ VNodeNum + " ***********************");
            System.out.println(distributionType+"_V"+VNodeNum+"_SEB_Orphan_Height = "+JSON.toJSONString(SEBHeightList)+";");
            System.out.println(distributionType+"_V"+VNodeNum+"_IOTA_Orphan_Height = "+JSON.toJSONString(IOTHeightList)+";");
        }
        fixedExecutor.shutdown();
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
