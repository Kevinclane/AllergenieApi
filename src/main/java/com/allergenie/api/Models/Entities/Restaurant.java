package com.allergenie.api.Models.Entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.ResultSet;
import java.sql.SQLException;

import static com.allergenie.api.Utils.isValidEmailAddress;

@Entity
@Data
@Builder
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
    private String details;

    public Restaurant(ResultSet rs) throws SQLException {
        this.id = rs.getInt("id");
        this.name = rs.getString("name");
        this.phoneNumber = rs.getString("phone_number");
        this.emailAddress = rs.getString("email_address");
        this.streetAddress = rs.getString("street_address");
        this.streetAddressTwo = rs.getString("street_address_two");
        this.city = rs.getString("city");
        this.state = rs.getString("state");
        this.zipCode = rs.getString("zip_code");
        this.details = rs.getString("details");
    }

    public boolean isValid() {
        if (this.name.isEmpty() || this.name.length() > 150) {
            return false;
        }
        if (this.phoneNumber.isEmpty() || this.phoneNumber.length() > 10 || !this.phoneNumber.matches("\\d+")) {
            return false;
        }
        if (!isValidEmailAddress(this.emailAddress)) {
            return false;
        }
        if (this.streetAddress.isEmpty() || this.streetAddress.length() > 50) {
            return false;
        }
        if (this.city.isEmpty() || this.city.length() > 45) {
            return false;
        }
        if (this.state.isEmpty() || this.state.length() > 2) {
            return false;
        }
        if (this.zipCode.length() != 5) {
            return false;
        }
        return true;
    }
}


