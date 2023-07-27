package util;

import com.alibaba.fastjson.JSON;
import model.Transaction;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.jcajce.provider.digest.SHA3;
import org.bouncycastle.util.encoders.Hex;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * @program: SEB-Framework
 * @description: common used utils
 * @creator: Li DX
 */
public class Utils {
    public static List<Integer> intSplit(int totalNum, int splitNum){
        List<Integer> out = new ArrayList<>();
        int e = totalNum%splitNum;
        int avg = totalNum/splitNum;
        for(int i = 0; i < splitNum; i++){
            out.add(avg);
        }
        if(e!=0){
            for(int j=0; j < e; j++){
                out.set(j, out.get(j)+1);
            }
        }
        return out;
    }

    /**
     * puzzle
     * @param transaction
     * @return
     */
    public static String puzzle(Transaction transaction) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA3-256");
            byte[] hashBytes = digest.digest(JSON.toJSONBytes(transaction));
            return new String(Hex.encode(hashBytes));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * deep copy
     * @param transactionList
     * @return
     */
    public static ArrayList<Transaction> transactionListClone(ArrayList<Transaction> transactionList){
        ArrayList<Transaction> cloneTransaction = new ArrayList<>();
        int size = transactionList.size();
        for(int i = 0;i < size; i++){
            Transaction ele = transactionList.get(i);
            cloneTransaction.add(CloneUtil.clone(ele));
        }
        return cloneTransaction;
    }

    /**
     * deep copy
     * @param transactionList
     * @return
     */
    public static HashMap<String, Transaction> transactionMapClone(ArrayList<Transaction> transactionList){
        HashMap<String, Transaction> cloneTransaction = new HashMap<>();
        int size = transactionList.size();
        for(int i = 0;i < size; i++){
            Transaction ele = transactionList.get(i);
            cloneTransaction.put(ele.getTxID(), CloneUtil.clone(ele));
        }
        return cloneTransaction;
    }

    /**
     * deep copy
     * @param transactionList
     * @return
     */
    public static HashMap<String, Transaction> transactionMapShadowClone(ArrayList<Transaction> transactionList){
        HashMap<String, Transaction> cloneTransaction = new HashMap<>();
        int size = transactionList.size();
        for(int i = 0;i < size; i++){
            Transaction ele = transactionList.get(i);
            cloneTransaction.put(ele.getTxID(), ele);
        }
        return cloneTransaction;
    }

    public static void getTypeSize(ArrayList<Transaction> transactionList){
        HashMap<Integer, Integer> hashMap = new HashMap<>();
        for(Transaction t: transactionList){
            Integer tt = t.getType().getTransactionType();
            if(hashMap.containsKey(tt)){
                hashMap.put(tt, hashMap.get(tt)+1);
            }else{
                hashMap.put(tt,1);
            }
        }
        System.out.println(hashMap);
    }


    public static int runPow(String content) {
        int counter = 1;
        SHA3.Digest256 digest = new SHA3.Digest256();
        while(true) {
            String str = content + counter;
            digest.update(str.getBytes(StandardCharsets.UTF_8));
            String encodeStr = byte2Hex(digest.digest());
            String prefix = encodeStr.substring(0,3);
            if (prefix.equals("000")) {
                // find the counter
                break;
            }
            counter++;
        }
        return counter;
    }
    private static String byte2Hex(byte[] bytes) {
        StringBuffer stringBuffer = new StringBuffer();
        String temp = null;
        for (int i = 0; i < bytes.length; i++) {
            temp = Integer.toHexString(bytes[i] & 0xFF);
            if (temp.length() == 1) {
                // 1得到一位的进行补0操作
                stringBuffer.append("0");
            }
            stringBuffer.append(temp);
        }
        return stringBuffer.toString();
    }
}
