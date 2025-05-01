package com.allergenie.api.Services;

import com.allergenie.api.Models.Entities.Allergen;
import com.allergenie.api.Models.Entities.MenuItemAllergen;
import com.allergenie.api.Repos.AllergenRepo;
import com.allergenie.api.Repos.MenuItemAllergenRepo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AllergenServiceTests {
    @Mock
    private AllergenRepo allergenRepo;

    @Mock
    private MenuItemAllergenRepo menuItemAllergenRepo;

    @InjectMocks
    private AllergenService service;

    Allergen milkAllergen = Allergen.builder()
            .id(1)
            .name("Milk")
            .build();
    Allergen eggAllergen = Allergen.builder()
            .id(2)
            .name("Eggs")
            .build();
    Allergen fishAllergen = Allergen.builder()
            .id(3)
            .name("Fish")
            .build();
    Allergen shellfishAllergen = Allergen.builder()
            .id(4)
            .name("Shellfish")
            .build();
    Allergen treeNutAllergen = Allergen.builder()
            .id(5)
            .name("Tree Nuts")
            .build();
    Allergen peanutAllergen = Allergen.builder()
            .id(6)
            .name("Peanuts")
            .build();
    Allergen wheatAllergen = Allergen.builder()
            .id(7)
            .name("Wheat")
            .build();
    Allergen soyAllergen = Allergen.builder()
            .id(8)
            .name("Soybeans")
            .build();
    Allergen sesameAllergen = Allergen.builder()
            .id(9)
            .name("Sesame")
            .build();

    @Nested
    @DisplayName("getAllergens")
    public class GetAllergens {
        @Test
        public void shouldReturnAllAllergens() {
            List<Allergen> allergens = asList(
              milkAllergen,
              eggAllergen,
              fishAllergen,
              shellfishAllergen,
              treeNutAllergen,
              peanutAllergen,
              wheatAllergen,
              soyAllergen,
              sesameAllergen
            );

            when(allergenRepo.findAll())
                    .thenReturn(allergens);

            List<Allergen> actual = service.getAllergens();

            assertEquals(allergens, actual);
        }
    }

    @Nested
    @DisplayName("getAllByMenuId")
    public class GetAllByMenuId {
        @Test
        public void shouldReturnMenuItemAllergens() {
            Integer menuId = 22;
            MenuItemAllergen firstMIA = MenuItemAllergen.builder()
                    .id(12)
                    .allergenId(2)
                    .menuItemId(9)
                    .build();
            MenuItemAllergen secondMIA = MenuItemAllergen.builder()
                    .id(13)
                    .allergenId(4)
                    .menuItemId(10)
                    .build();

            List<MenuItemAllergen> expected = asList(
                    firstMIA,
                    secondMIA
            );

            when(menuItemAllergenRepo.findAllByMenuId(menuId))
                    .thenReturn(expected);

            List<MenuItemAllergen> actual = service.getAllByMenuId(menuId);
            assertEquals(expected, actual);
        }
    }

    @Nested
    @DisplayName("getAllByMenuItemId")
    public class GetAllByMenuItemId {
        @Test
        public void shouldReturnMenuItemAllergens() {
            Integer menuItemId = 33;
            MenuItemAllergen firstMIA = MenuItemAllergen.builder()
                    .id(12)
                    .allergenId(2)
                    .menuItemId(menuItemId)
                    .build();
            MenuItemAllergen secondMIA = MenuItemAllergen.builder()
                    .id(13)
                    .allergenId(4)
                    .menuItemId(menuItemId)
                    .build();

            List<MenuItemAllergen> expected = asList(
                    firstMIA,
                    secondMIA
            );

            when(menuItemAllergenRepo.findAllByMenuItemId(menuItemId))
                    .thenReturn(expected);

            List<MenuItemAllergen> actual = service.getAllByMenuItemId(menuItemId);
            assertEquals(expected, actual);
        }
    }

    @Nested
    @DisplayName("saveMenuItemAllergens")
    public class SaveMenuItemAllergens {
        @Test
        public void shouldCallMenuItemAllergenRepo() {
            List<MenuItemAllergen> mias = asList(
                    MenuItemAllergen.builder()
                            .menuItemId(33)
                            .allergenId(1)
                            .build(),
                    MenuItemAllergen.builder()
                            .menuItemId(35)
                            .allergenId(3)
                            .build()
            );

            service.saveMenuItemAllergens(mias);
            verify(menuItemAllergenRepo).saveAll(mias);
        }
    }

    @Nested
    @DisplayName("deleteMenuItemAllergens")
    public class DeleteMenuItemAllergens {
        @Test
        public void shouldCallMenuItemAllergenRepo() {
            List<MenuItemAllergen> mias = asList(
                    MenuItemAllergen.builder()
                            .menuItemId(33)
                            .allergenId(1)
                            .build(),
                    MenuItemAllergen.builder()
                            .menuItemId(35)
                            .allergenId(3)
                            .build()
            );

            service.deleteMenuItemAllergens(mias);
            verify(menuItemAllergenRepo).deleteAll(mias);
        }
    }

    @Nested
    @DisplayName("deleteUnusedMenuItemAllergens")
    public class DeleteUnusedMenuItemAllergens {

        @Nested
        @DisplayName("WhenIdsAreReturned")
        public class WhenIdsAreReturned {
            @Test
            public void shouldCallRepoToDeleteByIds() {
                List<Integer> menuItemIds = asList(3, 5, 6);
                Integer menuId = 11;
                List<Integer> idsToDelete = asList(69, 70, 71);

                when(menuItemAllergenRepo.findMenuItemAllergenIdsToDelete(menuItemIds, menuId))
                        .thenReturn(idsToDelete);

                service.deleteUnusedMenuItemAllergens(menuItemIds, menuId);
                verify(menuItemAllergenRepo).deleteByIdIn(idsToDelete);
            }
        }

        @Nested
        @DisplayName("WhenNoIdsAreReturned")
        public class WhenNoIdsAreReturned {
            @Test
            public void shouldNotCallRepoToDeleteByIds() {
                List<Integer> menuItemIds = asList(3, 5, 6);
                Integer menuId = 11;

                when(menuItemAllergenRepo.findMenuItemAllergenIdsToDelete(menuItemIds, menuId))
                        .thenReturn(emptyList());

                service.deleteUnusedMenuItemAllergens(menuItemIds, menuId);
                verifyNoMoreInteractions(menuItemAllergenRepo);
            }
        }
    }
}
