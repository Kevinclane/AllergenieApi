package com.allergenie.api.Models.Requests;

import lombok.Data;

import java.util.List;

@Data

public class NewEditMenuRequest {
    private Integer id;
    private String name;
    private Boolean isActive;
    private List<Integer> restaurantIds;
}
