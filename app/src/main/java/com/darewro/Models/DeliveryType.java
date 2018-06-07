package com.darewro.Models;

/**
 * Created by Jaffar on 1/4/2018.
 */

public class DeliveryType {
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    int id;
    String title;
    String detail;
    String expectedCharges;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getExpectedCharges() {
        return expectedCharges;
    }

    public void setExpectedCharges(String expectedCharges) {
        this.expectedCharges = expectedCharges;
    }

    public String getExpectedDeliveryTime() {
        return expectedDeliveryTime;
    }

    public void setExpectedDeliveryTime(String expectedDeliveryTime) {
        this.expectedDeliveryTime = expectedDeliveryTime;
    }

    String expectedDeliveryTime;

    public DeliveryType() {
    }

    public DeliveryType(int id, String title, String detail, String expectedCharges, String expectedDeliveryTime) {
        this.id = id;
        this.title = title;
        this.detail = detail;
        this.expectedCharges = expectedCharges;
        this.expectedDeliveryTime = expectedDeliveryTime;
    }
}
