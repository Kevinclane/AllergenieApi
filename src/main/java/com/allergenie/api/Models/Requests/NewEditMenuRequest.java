package com.allergenie.api.Models.Requests;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class NewEditMenuRequest {
    private Integer id;
    private String name;
    private Boolean isActive;
    private Boolean isLinked;
    private Integer cloneOptionId;
    private Integer baseRestaurantId;
    private List<Integer> restaurantIds;
}
