package com.darewro.Models;

/**
 * Created by Jaffar on 8/30/2017.
 */
public class GeneralOrder {
    String detail;
    String deliveryCharges;

    public String getDeliveryCharges() {
        return deliveryCharges;
    }

    public void setDeliveryCharges(String deliveryCharges) {
        this.deliveryCharges = deliveryCharges;
    }

    public String getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(String deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    String deliveryTime;
    String bringMeOrDropIt;

    public GeneralOrder() {

    }

    public GeneralOrder(String detail,  String bringMeOrDropIt, String deliveryCharges, String deliveryTime) {
        this.detail = detail;
        this.deliveryCharges = deliveryCharges;
        this.deliveryTime = deliveryTime;
        this.bringMeOrDropIt = bringMeOrDropIt;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }


    public String getBringMeOrDropIt() {
        return bringMeOrDropIt;
    }

    public void setBringMeOrDropIt(String bringMeOrDropIt) {
        this.bringMeOrDropIt = bringMeOrDropIt;
    }
}
