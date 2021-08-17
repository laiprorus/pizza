package com.example.pizza.constraints;

import com.example.pizza.config.OrderConstraints;
import com.google.gson.annotations.Expose;

// Stores constraints for creating order.
public class Constraints {

    @Expose
    private String[] flavor;
    @Expose
    private String[] crust;
    @Expose
    private String[] size;

    public Constraints() {
        this.flavor = OrderConstraints.flavor.clone();
        this.crust = OrderConstraints.crust.clone();
        this.size = OrderConstraints.size.clone();
    }

    public String[] getFlavor(){
        return flavor;
    }
    public void setFlavor(String[] flavor){
        this.flavor = flavor;
    }

    public String[] getCrust(){
        return crust;
    }
    public void setCrust(String[] crust){
        this.crust = crust;
    }

    public String[] getSize(){
        return size;
    }
    public void setSize(String[] size){
        this.size = size;
    }

}
