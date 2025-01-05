package com.allergenie.api.Models.Responses;

import lombok.Data;

@Data
public class TempResponse {
    private Integer id;
    private String name;
    private String phoneNumber;
    private String emailAddress;
    private String streetAddress;
    private String streetAddressTwo;
    private String city;
    private String state;
    private String zipCode;
}
