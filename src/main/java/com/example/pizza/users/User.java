package com.example.pizza.users;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/*
    User entity for JPA repository.
    Stores user name and order ids linked to this user.
 */
@Entity
public class User {

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique=true)
    private String name;

    @ElementCollection
    @JsonIgnore
    private List<Long> orderIds;

    public User() {
        this.orderIds = new ArrayList<Long>();
    }

    public User(String name) {
        this.name = name;
        this.orderIds = new ArrayList<Long>();
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Long> getOrderIds() {
        return this.orderIds;
    }

    public void setOrderIds(List<Long> orders) {
        this.orderIds = orders;
    }

    @Override
    public String toString() {
       return "User{" + "id=" + this.id + ", name='" + this.name + "'}";
    }
}