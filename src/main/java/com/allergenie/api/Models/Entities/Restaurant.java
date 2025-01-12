package com.allergenie.api.Models.Entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.ResultSet;
import java.sql.SQLException;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
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

    public Restaurant(ResultSet rs) throws SQLException {
        this.id = rs.getInt("id");
        this.name = rs.getString("name");
        this.phoneNumber = rs.getString("phoneNumber");
        this.emailAddress = rs.getString("emailAddress");
        this.streetAddress = rs.getString("streetAddress");
        this.streetAddressTwo = rs.getString("streetAddressTwo");
        this.city = rs.getString("city");
        this.state = rs.getString("state");
        this.zipCode = rs.getString("zipCode");
    }
}


