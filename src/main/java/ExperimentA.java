import com.alibaba.fastjson.JSON;
import simulation.ExperimentAThread;
import util.Config;
import util.ConsoleProgressBarUtils;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @program: SEB-Framework
 * @description: Transaction Search Number
 * @creator: Li DX
 */
@lombok.extern.slf4j.Slf4j
public class ExperimentA {
    public static void main(String[] args) throws Exception {
        ArrayList<Integer> transactionSizeList = Config.sizeList(100, 3000, 30);
        ArrayList<Integer> VNodeNumList = Config.VNodeNumList(5000,20000,4);
        int transactionTypeNum = Config.transactionTypeNum;
        int pairNum = Config.transactionPairs;
        String distributionType = Config.distributionType;
        ExecutorService fixedExecutor = Executors.newFixedThreadPool(1);
        ArrayList<Future<ArrayList<ArrayList<Long>>>> results = new ArrayList<>();
        for(int i = 0; i < Objects.requireNonNull(VNodeNumList).size(); i++) {
            Integer VNodeNum = VNodeNumList.get(i);
            ArrayList<ArrayList<Long>> SEBSearchNumList = new ArrayList<>();
            ArrayList<ArrayList<Long>> IOTASearchNumList = new ArrayList<>();
            for(int n =0; n< transactionTypeNum; n++){
                SEBSearchNumList.add(new ArrayList<>());
                IOTASearchNumList.add(new ArrayList<>());
            }
            results.clear();
            ConsoleProgressBarUtils cpb = new ConsoleProgressBarUtils(100, transactionSizeList.size(), "ExperimentA");
            for (int j = 0; j < Objects.requireNonNull(transactionSizeList).size(); j++) {
                Integer transactionSize = transactionSizeList.get(j);
//                log.info("Transaction Size: " + transactionSize + " VNode Number: "+ VNodeNum + " Start!");
                ExperimentAThread experimentAThread = new ExperimentAThread();
                experimentAThread.setParameters(VNodeNum, transactionSize, transactionTypeNum, pairNum);
                Future<ArrayList<ArrayList<Long>>> result = fixedExecutor.submit(experimentAThread);
                results.add(result);
//                log.info("Transaction Size: " + transactionSize + " VNode Number: "+ VNodeNum + " Finished!");
            }
            int k = 1;
            for (Future<ArrayList<ArrayList<Long>>> f : results) {
                ArrayList<ArrayList<Long>> resultValue = f.get();
                for(int n =0; n< transactionTypeNum; n++){
                    SEBSearchNumList.get(n).add(resultValue.get(0).get(n));
                    IOTASearchNumList.get(n).add(resultValue.get(1).get(n));
                }
                cpb.show(k);
                k++;
            }

            log.info("*********************** Transaction Size: " + transactionSizeList + " VNode Number: "+ VNodeNum + " ***********************");
            for(int n =0; n< transactionTypeNum; n++){
                System.out.println(distributionType+"_V"+VNodeNum+"_SEB_Type_"+(n+1)+" = "+JSON.toJSONString(SEBSearchNumList.get(n))+";");
                System.out.println(distributionType+"_V"+VNodeNum+"_IOTA_Type_"+(n+1)+" = "+JSON.toJSONString(IOTASearchNumList.get(n))+";");
            }
        }
        fixedExecutor.shutdown();
    }
}