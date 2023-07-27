import com.alibaba.fastjson.JSON;
import model.Transaction;
import simulation.IOTA;
import simulation.SEB;
import simulation.SimulationDataCreate;
import util.Config;
import util.ConsoleProgressBarUtils;
import util.Utils;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @program: SEB-Framework
 * @description: Calculate TPS/CTPS
 * @creator: Li DX
 */
@lombok.extern.slf4j.Slf4j
public class ExperimentD {
    public static void main(String[] args) throws Exception {
        ArrayList<Integer> VNodeNumList = Config.VNodeNumList(2000,50000,25);
        ConsoleProgressBarUtils cpb = new ConsoleProgressBarUtils(100,VNodeNumList.size(), "ExperimentD");
        Integer transactionTypeNum = Config.transactionTypeNum;
        String distributionType = Config.distributionType;
        ArrayList<Long> SEBTps = new ArrayList<>();
        ArrayList<Long> IOTATps = new ArrayList<>();
        ArrayList<Long> IOTACTps = new ArrayList<>();
        for(int k = 0; k < Objects.requireNonNull(VNodeNumList).size(); k++) {
            Integer VNodeNum = VNodeNumList.get(k);
            Integer transactionSize = (int) (VNodeNum*0.3);
            // data create
            SimulationDataCreate simulationDataCreate = new SimulationDataCreate();
            simulationDataCreate.run(transactionSize, VNodeNum, 0, transactionTypeNum);
            ArrayList<Transaction> transactions = simulationDataCreate.getTransactions();
            ArrayList<Transaction> clonedTransactions = Utils.transactionListClone(transactions);
//            System.out.println("Transaction Size: " + transactionSize + " VNode Number: "+ VNodeNum +  " Start!");
            // RUN SEB
//            log.info("*********************** SEB Simulation Result ***********************");
            SEB seb = new SEB();
            seb.run(clonedTransactions, "IOTA Transaction Attach", 2);
            SEBTps.add(seb.getTps());
            // RUN IOTA
//            log.info("*********************** IOTA Simulation Result ***********************");
            IOTA iota = new IOTA();
            iota.run(transactions, "IOTA Transaction Attach", 2);
            IOTATps.add(iota.getTps());
            IOTACTps.add(iota.getCtps());
            cpb.show(k+1);
//            log.info("Transaction Size: " + transactionSize + " VNode Number: "+ VNodeNum +  " Finished!");
        }

        log.info("*********************** VNode Number: " + JSON.toJSONString(VNodeNumList) + " ***********************");
        System.out.println(distributionType+"_SEB_TPS = " + SEBTps+";");
        System.out.println(distributionType+"_IOTA_TPS = " + IOTATps+";");
        System.out.println(distributionType+"_IOTA_CTPS = " + IOTACTps+";");
    }

}
