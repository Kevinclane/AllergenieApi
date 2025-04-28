package com.allergenie.api.Models;

import com.allergenie.api.Models.Entities.Restaurant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class RestaurantTests {
    @Nested
    @DisplayName("isValid")
    public class IsValid {
        @Test
        public void whenAllFieldsAreValid_shouldReturnTrue() {
            Restaurant restaurant = Restaurant.builder()
                    .id(0)
                    .name("Name")
                    .phoneNumber("1112223333")
                    .emailAddress("123@123.com")
                    .streetAddress("123 Sesame St")
                    .city("Muppetville")
                    .state("ID")
                    .zipCode("55555")
                    .build();
            assertTrue(restaurant.isValid());
        }

        @Test
        public void whenNameIsEmpty_shouldReturnFalse() {
            Restaurant restaurant = Restaurant.builder()
                    .id(0)
                    .name("")
                    .phoneNumber("1112223333")
                    .emailAddress("123@123.com")
                    .streetAddress("123 Sesame St")
                    .city("Muppetville")
                    .state("ID")
                    .zipCode("55555")
                    .build();
            assertFalse(restaurant.isValid());
        }

        @Test
        public void whenNameIsGreaterThan150Character_shouldReturnFalse() {
            Restaurant restaurant = Restaurant.builder()
                    .id(0)
                    .name("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa")
                    .phoneNumber("1112223333")
                    .emailAddress("123@123.com")
                    .streetAddress("123 Sesame St")
                    .city("Muppetville")
                    .state("ID")
                    .zipCode("55555")
                    .build();
            assertFalse(restaurant.isValid());
        }

        @Test
        public void whenPhoneNumberIsEmpty_shouldReturnFalse() {
            Restaurant restaurant = Restaurant.builder()
                    .id(0)
                    .name("Name")
                    .phoneNumber("")
                    .emailAddress("123@123.com")
                    .streetAddress("123 Sesame St")
                    .city("Muppetville")
                    .state("ID")
                    .zipCode("55555")
                    .build();
            assertFalse(restaurant.isValid());
        }

        @Test
        public void whenPhoneNumberIsGreaterThan10Characters_shouldReturnFalse() {
            Restaurant restaurant = Restaurant.builder()
                    .id(0)
                    .name("Name")
                    .phoneNumber("11122233339")
                    .emailAddress("123@123.com")
                    .streetAddress("123 Sesame St")
                    .city("Muppetville")
                    .state("ID")
                    .zipCode("55555")
                    .build();
            assertFalse(restaurant.isValid());
        }

        @Test
        public void whenPhoneNumberContainsNonNumericValues_shouldReturnFalse() {
            Restaurant restaurant = Restaurant.builder()
                    .id(0)
                    .name("Name")
                    .phoneNumber("111222333d")
                    .emailAddress("123@123.com")
                    .streetAddress("123 Sesame St")
                    .city("Muppetville")
                    .state("ID")
                    .zipCode("55555")
                    .build();
            assertFalse(restaurant.isValid());
        }

        @ParameterizedTest
        @ValueSource(strings = {"aaa", "aaa.com"})
        public void whenEmailIsInvalid_shouldReturnFalse(String email) {
            Restaurant restaurant = Restaurant.builder()
                    .id(0)
                    .name("Name")
                    .phoneNumber("1112223333")
                    .emailAddress(email)
                    .streetAddress("123 Sesame St")
                    .city("Muppetville")
                    .state("ID")
                    .zipCode("55555")
                    .build();
            assertFalse(restaurant.isValid());
        }

        @Test
        public void whenStreetAddressIsEmpty_shouldReturnFalse() {
            Restaurant restaurant = Restaurant.builder()
                    .id(0)
                    .name("Name")
                    .phoneNumber("1112223333")
                    .emailAddress("123@123.com")
                    .streetAddress("")
                    .city("Muppetville")
                    .state("ID")
                    .zipCode("55555")
                    .build();
            assertFalse(restaurant.isValid());
        }

        @Test
        public void whenStreetAddressIsGreaterThan50Characters_shouldReturnFalse() {
            Restaurant restaurant = Restaurant.builder()
                    .id(0)
                    .name("Name")
                    .phoneNumber("1112223333")
                    .emailAddress("123@123.com")
                    .streetAddress("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa")
                    .city("Muppetville")
                    .state("ID")
                    .zipCode("55555")
                    .build();
            assertFalse(restaurant.isValid());
        }

        @Test
        public void whenCityIsEmpty_shouldReturnFalse() {
            Restaurant restaurant = Restaurant.builder()
                    .id(0)
                    .name("Name")
                    .phoneNumber("111222333")
                    .emailAddress("123@123.com")
                    .streetAddress("123 Sesame St")
                    .city("")
                    .state("ID")
                    .zipCode("55555")
                    .build();
            assertFalse(restaurant.isValid());
        }

        @Test
        public void whenCityIsGreaterThan45Characters_shouldReturnFalse() {
            Restaurant restaurant = Restaurant.builder()
                    .id(0)
                    .name("Name")
                    .phoneNumber("111222333")
                    .emailAddress("123@123.com")
                    .streetAddress("123 Sesame St")
                    .city("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa")
                    .state("ID")
                    .zipCode("55555")
                    .build();
            assertFalse(restaurant.isValid());
        }

        @Test
        public void whenStateIsEmpty_shouldReturnFalse() {
            Restaurant restaurant = Restaurant.builder()
                    .id(0)
                    .name("Name")
                    .phoneNumber("111222333")
                    .emailAddress("123@123.com")
                    .streetAddress("123 Sesame St")
                    .city("Muppetville")
                    .state("")
                    .zipCode("55555")
                    .build();
            assertFalse(restaurant.isValid());
        }

        @Test
        public void whenStateIsGreaterThan2Characters_shouldReturnFalse() {
            Restaurant restaurant = Restaurant.builder()
                    .id(0)
                    .name("Name")
                    .phoneNumber("111222333")
                    .emailAddress("123@123.com")
                    .streetAddress("123 Sesame St")
                    .city("Muppetville")
                    .state("IDA")
                    .zipCode("55555")
                    .build();
            assertFalse(restaurant.isValid());
        }

        @ParameterizedTest
        @ValueSource(strings =  {"1234", "123456"})
        public void whenZipCodeIsNot5Characters_shouldReturnFalse(String zipCode) {
            Restaurant restaurant = Restaurant.builder()
                    .id(0)
                    .name("Name")
                    .phoneNumber("111222333")
                    .emailAddress("123@123.com")
                    .streetAddress("123 Sesame St")
                    .city("Muppetville")
                    .state("ID")
                    .zipCode(zipCode)
                    .build();
            assertFalse(restaurant.isValid());
        }
    }
}
