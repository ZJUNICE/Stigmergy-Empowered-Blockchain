package util;

import model.Transaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;

public class TransactionSearch {

    public static int search(Transaction startTransaction, Transaction endTransaction, HashMap<String, Transaction> transactionMap, boolean ifMCMC){
        ArrayList<String> outputRoute =  new ArrayList<>();
        HashMap<String, String> edgeMap = new HashMap<>();
        ArrayList<String> nextRouteList = new ArrayList<>();
        String endTradeId = endTransaction.getTxID();
        ArrayList<String> processedPoints = new ArrayList<>();
        nextRouteList.add(startTransaction.getTxID());
        processedPoints.add(startTransaction.getTxID());
        int searchNum = 0;
        int step = 0;
        while(!edgeMap.containsKey(endTradeId)){
            ArrayList<String> innerNextRouteList = new ArrayList<>();
            for(String part: nextRouteList){
                ArrayList<String> out = nextRoute(transactionMap.get(part), processedPoints, ifMCMC);
                innerNextRouteList.addAll(out);
                for(String ele: out){
                    edgeMap.put(ele, part);
                    processedPoints.add(ele);
                    searchNum++;
                }
            }
            LinkedHashSet<String> hashSet = new LinkedHashSet<>(innerNextRouteList);
            nextRouteList = new ArrayList<>(hashSet);
            step++;
        }
        String tradeId = endTradeId;
        outputRoute.add(tradeId);
        while(!tradeId.equals(startTransaction.getTxID())){
            String nextTradeId = edgeMap.get(tradeId);
            outputRoute.add(nextTradeId);
            tradeId = nextTradeId;
        }
//        System.out.println("Number of Search Nodes: " + searchNum + "; Number of Search Steps: "+ step);
        return searchNum;
    }

    /**
     * get next route
     * @param t
     * @return
     */
    private static ArrayList<String> nextRoute(Transaction t, ArrayList<String> processedPoint, boolean ifMCMC){
        ArrayList<String> output = new ArrayList<>();
        ArrayList<String> nextRouteList = new ArrayList<>();
        ArrayList<String> allChildList = t.getServiceChildIDList();
        if(ifMCMC) {
            allChildList = t.getChildIDList();
        }
        if(!ifMCMC && t.getSecurityHeight() == 0){
            allChildList.clear();
        }
        if(!allChildList.isEmpty()){
            nextRouteList.addAll(allChildList);
        }
        if(ifMCMC){
            if(t.getParent1ID() != null){
                nextRouteList.add(t.getParent1ID());
            }
            if(t.getParent2ID() != null){
                nextRouteList.add(t.getParent2ID());
            }
        }else {
            if (t.getServiceParentID() != null) {
                nextRouteList.add(t.getServiceParentID());
            }
        }
        for(String part: nextRouteList){
            if(!processedPoint.contains(part)){
                output.add(part);
            }
        }
        return output;
    }


}
