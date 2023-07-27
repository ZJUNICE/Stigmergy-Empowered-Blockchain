package model;

import java.io.Serializable;
import java.util.HashMap;

/**
 * @program: SEB-Framework
 * @description: VNode
 * @creator: Li DX
 */
public class VNode implements Cloneable, Serializable {
    private String vNodeId;
    private short[] delays;
    private int vNodeNum;

    public VNode(String vNodeId, short[] delays, int vNodeNum){
        this.vNodeId = vNodeId;
        this.delays = delays;
        this.vNodeNum = vNodeNum;
    }

    public String getvNodeId() {
        return vNodeId;
    }

    public void setvNodeId(String vNodeId) {
        this.vNodeId = vNodeId;
    }

    public short[] getDelays() {
        return delays;
    }

    public void setDelays(short[] delays) {
        this.delays = delays;
    }

    public int getvNodeNum() {
        return vNodeNum;
    }

    public void setvNodeNum(int vNodeNum) {
        this.vNodeNum = vNodeNum;
    }
}
