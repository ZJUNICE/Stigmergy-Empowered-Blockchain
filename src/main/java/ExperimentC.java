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
 * @description: Calculate Time Cost
 * @creator: Li DX
 */
@lombok.extern.slf4j.Slf4j
public class ExperimentC {
    public static void main(String[] args) throws Exception {
        ArrayList<Integer> VNodeNumList = Config.VNodeNumList(2000,50000,25);
        Integer transactionTypeNum = Config.transactionTypeNum;
        ConsoleProgressBarUtils cpb = new ConsoleProgressBarUtils(100,VNodeNumList.size(), "ExperimentC");
        String distributionType = Config.distributionType;
        ArrayList<Long> TSPSTimeCost = new ArrayList<>();
        ArrayList<Long> TSATimeCost = new ArrayList<>();
        for(int k = 0; k < Objects.requireNonNull(VNodeNumList).size(); k++) {
            Integer VNodeNum = VNodeNumList.get(k);
            Integer transactionSize = (int) (VNodeNum*0.3);
            // data create
            SimulationDataCreate simulationDataCreate = new SimulationDataCreate();
            simulationDataCreate.run(transactionSize, VNodeNum, 0, transactionTypeNum);
            ArrayList<Transaction> transactions = simulationDataCreate.getTransactions();
            ArrayList<Transaction> clonedTransactions = Utils.transactionListClone(transactions);
            // RUN SEB
//            log.info("*********************** SEB Simulation Result ***********************");
            SEB seb = new SEB();
            seb.run(clonedTransactions, "SEB Transaction Attach", 1);
            TSPSTimeCost.add(seb.getTSPSCostTime());
            // RUN IOTA
//            log.info("*********************** IOTA Simulation Result ***********************");
            IOTA iota = new IOTA();
            iota.run(transactions, "IOTA Transaction Attach", 1);
            TSATimeCost.add(iota.getTsaCostTime());
            cpb.show(k+1);
        }
        log.info("*********************** VNode Number: "+ VNodeNumList + " ***********************");
        System.out.println(distributionType+"_TSPS_Time_Cost = " + TSPSTimeCost+";");
        System.out.println(distributionType+"_TSA_Time_Cost = " + TSATimeCost+";");
    }
}
