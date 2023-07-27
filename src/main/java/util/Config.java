package util;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: SEB-Framework
 * @description: config parameters
 * @creator: Li DX
 */
public class Config {

    // list of transaction size
    public static ArrayList<Integer> sizeList(Integer min, Integer max, Integer num){
        ArrayList<Integer> out = new ArrayList<>();
        int interval = 0;
        if(num-1<0){
            System.out.println("Error, Num must be greater Than 0");
            return null;
        }else if(num > 1){
            interval = (max-min)/(num-1);
        }
        for(int i = 0; i < num; i++){
            out.add(min + i*interval);
        }
        return out;
    }

    // list of VNode number
    public static ArrayList<Integer> VNodeNumList(Integer min, Integer max, Integer num){
        ArrayList<Integer> out = new ArrayList<>();
        int interval = 0;
        if(num-1<0){
            System.out.println("Error, Num must be greater Than 0");
            return null;
        }else if(num > 1){
            interval = (max-min)/(num-1);
        }
        for(int i = 0; i < num; i++){
            out.add(min + i*interval);
        }
        return out;
    }

    // network delay distribution
    public static final String distributionType = "Gaussian";
//    public static final String distributionType = "Uniform";

    // random seed for all experiment
    public static final int seed = 200;

    // number of transaction pairs for searching test
    public static final Integer transactionPairs = 500;

    // number of transaction type
    public static final Integer transactionTypeNum = 4;

    // files saving address
    public static final String fileAddress = "/tmp";


}
