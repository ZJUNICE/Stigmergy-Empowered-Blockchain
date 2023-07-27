package util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import model.TransType;
import model.Transaction;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class DataFile {
    private static String rootAddress = Config.fileAddress;
    public void setRootAddress(String address){
        rootAddress=address;
    }
    /**
     * load transaction data
     * @param fileName
     * @return
     */
    public static ArrayList<Transaction> getTransactionFromFile(String fileName) {
        String path= rootAddress + fileName+ ".dat";
        Object fileOjbect = readFile(path);
        ArrayList<Transaction> t = new ArrayList<>(JSON.parseArray(fileOjbect.toString(), Transaction.class));
        return t;
    }
    /**
     * load transaction pairs for search test
     * @param fileName
     * @return
     */
    public static JSONObject getPairsFromFile(String fileName) {
        String path= rootAddress + fileName+ ".dat";
        Object fileOjbect = readFile(path);
        return JSONObject.parseObject(fileOjbect.toString());
    }

    public static HashMap<String, Transaction> getTransactionGraphFromFile(String fileName){
        String path= rootAddress + fileName+ ".dat";
        Object fileOjbect = readFile(path);
        JSONArray t = JSON.parseArray(fileOjbect.toString());
        HashMap<String, Transaction> out = new HashMap<>();
        for(int i =0; i<t.size(); i++){
            JSONObject ele = t.getJSONObject(i);
            Transaction tt = parseJSONObject2Transaction(ele);
            out.put(ele.getString("txID"), tt);
        }
        return out;
    }

    private static Transaction parseJSONObject2Transaction(JSONObject jsonObject){
        TransType type = jsonObject.getObject("transType", TransType.class);
        Transaction out = JSON.parseObject(jsonObject.toJSONString(), Transaction.class);
        out.transType = type;
        out.parent1ID = jsonObject.getString("parent1ID");
        out.parent2ID = jsonObject.getString("parent2ID");
        out.serviceParentID = jsonObject.getString("serviceParentID");
        out.securityParentID = jsonObject.getString("securityParentID");
        out.serviceHeight = jsonObject.getLong("serviceHeight");
        out.securityHeight = jsonObject.getLong("securityHeight");
        out.weight = jsonObject.getDouble("weight");
        out.serviceChildIDList = new ArrayList<>(jsonObject.getJSONArray("serviceChildIDList").toJavaList(String.class));
        out.securityChildIDList = new ArrayList<>(jsonObject.getJSONArray("securityChildIDList").toJavaList(String.class));
        out.height = jsonObject.getLong("height");
        out.childIDList = new ArrayList<>(jsonObject.getJSONArray("childIDList").toJavaList(String.class));
        return out;
    }
    /**
     * save data
     * @param fileName
     * @param data
     */
    public static void saveDataToFile(String fileName, Object data) {
        File file = new File(rootAddress+ fileName + ".dat");
        //如果文件不存在，则新建一个
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        FileOutputStream out;
        try {
            out = new FileOutputStream(file);
            ObjectOutputStream objOut=new ObjectOutputStream(out);
            objOut.writeObject(data);
            objOut.flush();
            objOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Object readFile(String path){
        FileInputStream in;
        Object temp = null;
        try {
            in = new FileInputStream(path);
            ObjectInputStream objIn=new ObjectInputStream(in);
            temp=objIn.readObject();
            objIn.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return temp;
    }

}
