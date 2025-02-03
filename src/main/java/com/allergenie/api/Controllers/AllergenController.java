package com.allergenie.api.Controllers;

import com.allergenie.api.Models.Entities.Allergen;
import com.allergenie.api.Services.AllergenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api/allergen")
public class AllergenController {

    @Autowired
    private AllergenService allergenService;

    @GetMapping("/all")
    public ResponseEntity<List<Allergen>> getAllergens() {
        try {
            List<Allergen> allergens = allergenService.getAllergens();
            return new ResponseEntity<>(allergens, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
