package org.example.entity;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;

public class Employee {
    @SerializedName("id")
    private int id;
    @SerializedName("name")
    private String name;
    @SerializedName("salary")
    private BigDecimal salary;
    @SerializedName("tax")
    private BigDecimal tax;

    public Employee() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }

    public BigDecimal getTax() {
        return tax;
    }

    public void setTax(BigDecimal tax) {
        this.tax = tax;
    }
}
