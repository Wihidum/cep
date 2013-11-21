package org.wso2.carbon.cep.wihidum.loadbalancer.nodemanager;


import java.util.ArrayList;
import java.util.List;

public class LBStream {

   private String id;
   private List<String> rrdList = new ArrayList<String>();
   private List<String> joinList = new ArrayList<String>();
   private List<String> directList = new ArrayList<String>();


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getRrdList() {
        return rrdList;
    }

    public void addRrdList(String id) {
        rrdList.add(id);
    }

    public List<String> getJoinList() {
        return joinList;
    }

    public void addJoinList(String id) {
        joinList.add(id);
    }

    public List<String> getDirectList() {
        return directList;
    }

    public void addDirectList(String id) {
        directList.add(id);
    }

    public  void clear(){
        rrdList.clear();
        directList.clear();
        joinList.clear();
    }
}
