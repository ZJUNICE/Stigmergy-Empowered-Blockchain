package model;


import java.io.Serializable;
import java.util.ArrayList;

public class Transaction implements Cloneable, Serializable {
    public String senderID;
    public String receiveID;
    public String txID;
    public TransType transType;
    public Long createTime;
    public Double weight = 1D;
    public VNode vNode;

    // Attributes For SEB
    public String serviceParentID;
    public String securityParentID;
    public Long serviceHeight;
    public Long securityHeight;
    public ArrayList<String> serviceChildIDList = new ArrayList<>();
    public ArrayList<String> securityChildIDList = new ArrayList<>();
    // Attributes for IOTA
    public String parent1ID;
    public String parent2ID;
    public Long height;
    public ArrayList<String> childIDList = new ArrayList<>();

    public Long getSecurityHeight() {
        return securityHeight;
    }

    public void setSecurityHeight(Long SecurityHeight) {
        this.securityHeight = SecurityHeight;
    }

    public void addChildID(String childID){
        if(!childIDList.contains(childID)){
            childIDList.add(childID);
        }
    }

    public void addSecurityChildID(String childID){
        if(!securityChildIDList.contains(childID)){
            securityChildIDList.add(childID);
        }
    }

    public void addServiceChildID(String childID){
        if(!serviceChildIDList.contains(childID)){
            serviceChildIDList.add(childID);
        }
    }

    public void updateWeight(){
        weight++;
    }

    public ArrayList<String> getChildIDList() { return childIDList; }

    public ArrayList<String> getServiceChildIDList(){
        return serviceChildIDList;
    }

    public void clearSecurityChildID(){
        securityChildIDList.clear();
    }

    public ArrayList<String> getSecurityChildIDList(){
        return securityChildIDList;
    }

    public void setWeight(Double newWeight){
        this.weight = newWeight;
    }

    public Double getWeight(){
        return weight;
    }

    public Transaction(String senderID, String receiveID, String txID, TransType transType, VNode vNode, Long createTime){
        this.senderID = senderID;
        this.receiveID = receiveID;
        this.txID = txID;
        this.transType = transType;
        this.vNode = vNode;
        this.createTime = createTime;
    }


    public String getServiceParentID() {
        return serviceParentID;
    }

    public void setServiceParentID(String serviceParentID) {
        this.serviceParentID = serviceParentID;
    }

    public String getSecurityParentID() {
        return securityParentID;
    }

    public void setSecurityParentID(String SecurityParentID) {
        this.securityParentID = SecurityParentID;
    }

    public String getTxID() {
        return txID;
    }

    public TransType getType() {
        return transType;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public Long getServiceHeight() {
        return serviceHeight;
    }

    public void setServiceHeight(Long serviceHeight) {
        this.serviceHeight = serviceHeight;
    }

    public String getParent1ID() {
        return parent1ID;
    }

    public void setParent1ID(String parent1ID) {
        this.parent1ID = parent1ID;
    }

    public String getParent2ID() {
        return parent2ID;
    }

    public void setParent2ID(String parent2ID) {
        this.parent2ID = parent2ID;
    }

    public Long getHeight() {
        return height;
    }

    public void setHeight(Long height) {
        this.height = height;
    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public String getReceiveID() {
        return receiveID;
    }

    public void setReceiveID(String receiveID) {
        this.receiveID = receiveID;
    }

    public void setTxID(String txID) {
        this.txID = txID;
    }

    public void setType(TransType transType) {
        this.transType = transType;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public void setServiceChildIDList(ArrayList<String> serviceChildIDList) {
        this.serviceChildIDList = serviceChildIDList;
    }

    public void setSecurityChildIDList(ArrayList<String> SecurityChildIDList) {
        this.securityChildIDList = SecurityChildIDList;
    }

    public void setChildIDList(ArrayList<String> childIDList) {
        this.childIDList = childIDList;
    }

    public VNode getvNode() {
        return vNode;
    }

    public void setvNode(VNode vNode) {
        this.vNode = vNode;
    }
}
