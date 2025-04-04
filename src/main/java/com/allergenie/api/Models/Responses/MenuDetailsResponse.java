package com.allergenie.api.Models.Responses;

import com.allergenie.api.Models.Entities.Restaurant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MenuDetailsResponse {
    private Integer id;
    private String name;
    private Boolean isActive;
    private Boolean isLinked;
    private List<Restaurant> linkedRestaurants;
    private List<Restaurant> allRestaurants;
}
