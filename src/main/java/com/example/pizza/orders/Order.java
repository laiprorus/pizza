package com.example.pizza.orders;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

// Order model https://order-pizza-api.herokuapp.com/api/ui/#!/Orders/orders_read_all
public class Order {

    @Expose(serialize = false)
    @SerializedName("Order_ID")
    private Long orderId;

    @Expose
    @SerializedName("Crust")
    private String crust;

    @Expose
    @SerializedName("Flavor")
    private String flavor;

    @Expose
    @SerializedName("Size")
    private String size;

    @Expose
    @SerializedName("Table_No")
    private int tableNumber;

    @Expose(serialize = false)
    @SerializedName("Timestamp")
    private String timeStamp;

    public Order() {
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getCrust() {
        return crust;
    }

    public void setCrust(String crust) {
        this.crust = crust;
    }

    public String getFlavor() {
        return flavor;
    }

    public void setFlavor(String flavor) {
        this.flavor = flavor;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public int getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(int tableNumber) {
        this.tableNumber = tableNumber;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    @Override
    public String toString() {
        return "Order [Order_ID=" + orderId + ", Table_No=" + tableNumber + "]";
    }
}
