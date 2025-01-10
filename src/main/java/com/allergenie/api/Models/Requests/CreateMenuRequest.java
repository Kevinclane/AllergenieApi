package com.allergenie.api.Models.Requests;

import lombok.Data;

import java.util.List;

@Data
public class CreateMenuRequest {
    private String name;
    private List<Integer> restaurantIds;

}
