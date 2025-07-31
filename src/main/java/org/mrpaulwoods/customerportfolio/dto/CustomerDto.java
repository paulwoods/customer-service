package org.mrpaulwoods.customerportfolio.dto;

/**
 * Data Transfer Object for Customer entity
 */
public class CustomerDto {

    private Integer id;
    private String name;
    private Integer balance;

    // Default constructor
    public CustomerDto() {
    }

    // Constructor with all fields
    public CustomerDto(Integer id, String name, Integer balance) {
        this.id = id;
        this.name = name;
        this.balance = balance;
    }

    // Getters and setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getBalance() {
        return balance;
    }

    public void setBalance(Integer balance) {
        this.balance = balance;
    }

}
