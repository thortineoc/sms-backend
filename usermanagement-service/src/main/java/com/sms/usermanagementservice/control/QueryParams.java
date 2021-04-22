package com.sms.usermanagementservice.control;
import java.util.Map;



public class QueryParams {

    private String group;
    private String phoneNumber;
    private String middleName;
    private String pesel;

    public QueryParams(Map<String, String> queryParameters){
        if(queryParameters.containsKey("group")) group= queryParameters.get("group");
        if(queryParameters.containsKey("phoneNumber")) phoneNumber= queryParameters.get("phoneNumber");
        if(queryParameters.containsKey("middleName")) middleName= queryParameters.get("middleName");
        if(queryParameters.containsKey("pesel")) pesel= queryParameters.get("pesel");
    }

    public String getGroup(){
        return group;
    }
    public String getPhoneNumber(){
        return phoneNumber;
    }
    public String getMiddleName(){
        return middleName;
    }
    public String getPesel(){
        return pesel;
    }

    private void setGroup(String param){
        this.group=param;
    }
    private void setPhoneNumber(String param){
        this.phoneNumber=param;
    }
    private void setMiddleName(String param){
       this.middleName=param;
    }
    private void setPesel(String param){
       this.pesel=param;
    }

}
