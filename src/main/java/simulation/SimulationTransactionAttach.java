package simulation;

import com.alibaba.fastjson.JSONObject;
import model.Transaction;
import util.DataFile;
import util.Utils;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @program: SEB-Framework
 * @description: Transaction Attach
 * @creator: Li DX
 */
@lombok.extern.slf4j.Slf4j
public class SimulationTransactionAttach {
    private HashMap<String, Transaction> IOTAGraphResult;
    private HashMap<String, Transaction> SEBGraphResult;
    private Integer transactionSize;
    private Integer VNodeNum;
    public void run(Integer transactionSize, Integer VNodeNum, ArrayList<Transaction> transactions) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        this.transactionSize = transactionSize;
        this.VNodeNum=VNodeNum;
        // RUN SEB
        SEB seb = new SEB();
        seb.run(Utils.transactionListClone(transactions), "SEB Transaction Attach");
        SEBGraphResult = seb.getOutGraphTransaction();
        // RUN IOTA
        IOTA iota = new IOTA();
        iota.run(transactions, "IOTA Transaction Attach");
        IOTAGraphResult = iota.getOutGraphTransaction();
    }


    public HashMap<String, Transaction> getIOTAGraphResult() {
        return IOTAGraphResult;
    }

    public HashMap<String, Transaction> getSEBGraphResult() {
        return SEBGraphResult;
    }

    public void saveToFile(){
        String MCMCFilename = String.format("IOTA-Graph-%d-%d", VNodeNum, transactionSize);
        String TSPSFilename = String.format("SEB-Graph-%d-%d", VNodeNum, transactionSize);
        DataFile.saveDataToFile(TSPSFilename, JSONObject.toJSONString(SEBGraphResult));
        DataFile.saveDataToFile(MCMCFilename, JSONObject.toJSONString(IOTAGraphResult));
    }
}
