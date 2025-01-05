package com.allergenie.api.Models.Entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Restaurant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String phoneNumber;
    private String emailAddress;
    private String streetAddress;
    private String streetAddressTwo;
    private String city;
    private String state;
    private String zipCode;

    public Integer getId() {
        return id;
    }
}


