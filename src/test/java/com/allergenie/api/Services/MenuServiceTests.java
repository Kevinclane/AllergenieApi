package com.allergenie.api.Services;

import com.allergenie.api.Models.Entities.*;
import com.allergenie.api.Models.Requests.NewEditMenuRequest;
import com.allergenie.api.Models.Responses.MenuDetailsResponse;
import com.allergenie.api.Models.Responses.MenuItemDetails;
import com.allergenie.api.Models.Responses.MenuItemGroupDetails;
import com.allergenie.api.Repos.MenuJdbcRepo;
import com.allergenie.api.Repos.MenuRepo;
import com.allergenie.api.Repos.RestaurantMenuCrosswalkRepo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MenuServiceTests {
    @Mock
    private MenuJdbcRepo menuJdbcRepo = mock(MenuJdbcRepo.class);
    @Mock
    private MenuRepo menuRepo = mock(MenuRepo.class);
    @Mock
    private RestaurantMenuCrosswalkRepo restaurantMenuCrosswalkRepo = mock(RestaurantMenuCrosswalkRepo.class);
    @Mock
    private RestaurantService restaurantService = mock(RestaurantService.class);
    @Mock
    private MenuItemGroupService menuItemGroupService = mock(MenuItemGroupService.class);
    @Mock
    private MenuItemService menuItemService = mock(MenuItemService.class);
    @Mock
    private AllergenService allergenService = mock(AllergenService.class);

    @InjectMocks
    private MenuService service;


    @Nested
    @DisplayName("getMenusByRestaurantId")
    public class GetMenusByRestaurantId {
        @Test
        public void whenIdIsValid_shouldReturnListOfMenus() {
            Integer restaurantId = 69;

            Menu firstMenu = Menu.builder()
                    .id(1)
                    .name("First Name")
                    .isActive(true)
                    .isLinked(false)
                    .build();
            Menu secondMenu = Menu.builder()
                    .id(2)
                    .name("Second Name")
                    .isActive(false)
                    .isLinked(true)
                    .build();

            List<Menu> expected = asList(firstMenu, secondMenu);

            when(menuJdbcRepo.findByRestaurantId(restaurantId))
                    .thenReturn(expected);

            List<Menu> actual = service.getMenusByRestaurantId(restaurantId);

            assertEquals(expected, actual);
        }

        @Test
        public void whenIdIsInvalid_shouldReturnEmptyList() {
            Integer restaurantId = 20;
            when(menuJdbcRepo.findByRestaurantId(restaurantId))
                    .thenReturn(emptyList());

            List<Menu> actual = service.getMenusByRestaurantId(restaurantId);
            assertEquals(emptyList(), actual);
        }
    }

    @Nested
    @DisplayName("createMenu")
    public class CreateMenu {

        @Nested
        @DisplayName("When clone option id is present")
        public class WhenCloneOptionIdIsPresent {
            @Test
            public void whenIsLinkedIsTrue_shouldSaveManyCrosswalksToCloneMenu() {
                Integer menuId = 69;
                List<Integer> restaurantIds = asList(20, 21, 22);
                List<Integer> crosswalkIds = asList(10, 11, 12);

                NewEditMenuRequest request = NewEditMenuRequest.builder()
                        .id(null)
                        .name(null)
                        .isActive(true)
                        .isLinked(true)
                        .cloneOptionId(menuId)
                        .baseRestaurantId(20)
                        .restaurantIds(restaurantIds)
                        .build();

                Menu cloneMenu = Menu.builder()
                        .id(menuId)
                        .name("Clone Menu")
                        .isActive(request.getIsActive())
                        .isLinked(request.getIsLinked())
                        .build();

                List<RestaurantMenuCrosswalk> crosswalks = asList(
                        RestaurantMenuCrosswalk.builder()
                                .id(null)
                                .menuId(menuId)
                                .restaurantId(restaurantIds.get(0))
                                .build(),
                        RestaurantMenuCrosswalk.builder()
                                .id(null)
                                .menuId(menuId)
                                .restaurantId(restaurantIds.get(1))
                                .build(),
                        RestaurantMenuCrosswalk.builder()
                                .id(null)
                                .menuId(menuId)
                                .restaurantId(restaurantIds.get(2))
                                .build()
                );

                when(menuRepo.findById(menuId))
                        .thenReturn(Optional.ofNullable(cloneMenu));

                when(menuRepo.save(cloneMenu))
                        .thenAnswer(args -> {
                            Menu m = args.getArgument(0);
                            m.setId(menuId);
                            return m;
                        });

                when(restaurantMenuCrosswalkRepo.saveAll(crosswalks))
                        .thenAnswer(args -> {
                            List<RestaurantMenuCrosswalk> xwalks = args.getArgument(0);
                            for (int i = 0; i < xwalks.size(); i++) {
                                xwalks.get(i).setId(crosswalkIds.get(i));
                                xwalks.get(i).setMenuId(menuId);
                            }
                            return xwalks;
                        });

                List<RestaurantMenuCrosswalk> expectedCrosswalks = asList(
                        RestaurantMenuCrosswalk.builder()
                                .id(crosswalkIds.get(0))
                                .menuId(menuId)
                                .restaurantId(restaurantIds.get(0))
                                .build(),
                        RestaurantMenuCrosswalk.builder()
                                .id(crosswalkIds.get(1))
                                .menuId(menuId)
                                .restaurantId(restaurantIds.get(1))
                                .build(),
                        RestaurantMenuCrosswalk.builder()
                                .id(crosswalkIds.get(2))
                                .menuId(menuId)
                                .restaurantId(restaurantIds.get(2))
                                .build()
                );

                Menu expected = Menu.builder()
                        .id(cloneMenu.getId())
                        .name(cloneMenu.getName())
                        .isActive(cloneMenu.getIsActive())
                        .isLinked(cloneMenu.getIsLinked())
                        .build();

                Menu actual = service.createMenu(request);

                assertEquals(expected, actual);
                verify(menuRepo).save(expected);
                verify(restaurantMenuCrosswalkRepo).saveAll(expectedCrosswalks);
            }

            @Test
            public void whenIsLinkedIsFalse_shouldSaveManyCrosswalksToManyMenusWithCloneMenusProperties() {
                Integer cloneMenuId = 68;
                List<Integer> menuIds = asList(69, 70, 71);
                List<Integer> restaurantIds = asList(20, 21, 22);
                List<Integer> crosswalkIds = asList(10, 11, 12);

                NewEditMenuRequest request = NewEditMenuRequest.builder()
                        .id(null)
                        .name(null)
                        .isActive(true)
                        .isLinked(false)
                        .cloneOptionId(cloneMenuId)
                        .baseRestaurantId(restaurantIds.get(0))
                        .restaurantIds(restaurantIds)
                        .build();

                Menu cloneMenu = Menu.builder()
                        .id(cloneMenuId)
                        .name("Clone Menu")
                        .isActive(request.getIsActive())
                        .isLinked(request.getIsLinked())
                        .build();

                List<RestaurantMenuCrosswalk> crosswalks = asList(
                        RestaurantMenuCrosswalk.builder()
                                .id(null)
                                .menuId(menuIds.get(0))
                                .restaurantId(restaurantIds.get(0))
                                .build(),
                        RestaurantMenuCrosswalk.builder()
                                .id(null)
                                .menuId(menuIds.get(1))
                                .restaurantId(restaurantIds.get(1))
                                .build(),
                        RestaurantMenuCrosswalk.builder()
                                .id(null)
                                .menuId(menuIds.get(2))
                                .restaurantId(restaurantIds.get(2))
                                .build()
                );

                when(menuRepo.findById(cloneMenuId))
                        .thenReturn(Optional.of(cloneMenu));

                when(restaurantMenuCrosswalkRepo.saveAll(crosswalks))
                        .thenAnswer(args -> {
                            List<RestaurantMenuCrosswalk> xwalks = args.getArgument(0);
                            for (int i = 0; i < xwalks.size(); i++) {
                                xwalks.get(i).setId(crosswalkIds.get(i));
                                xwalks.get(i).setMenuId(menuIds.get(i));
                            }
                            return xwalks;
                        });

                when(menuRepo.save(any()))
                        .thenAnswer(args -> {
                            Menu m = args.getArgument(0);
                            m.setId(menuIds.get(0));
                            return m;
                        }).thenAnswer(args -> {
                            Menu m = args.getArgument(0);
                            m.setId(menuIds.get(1));
                            return m;
                        }).thenAnswer(args -> {
                            Menu m = args.getArgument(0);
                            m.setId(menuIds.get(2));
                            return m;
                        });

                List<RestaurantMenuCrosswalk> expectedCrosswalks = asList(
                        RestaurantMenuCrosswalk.builder()
                                .id(crosswalkIds.get(0))
                                .menuId(menuIds.get(0))
                                .restaurantId(restaurantIds.get(0))
                                .build(),
                        RestaurantMenuCrosswalk.builder()
                                .id(crosswalkIds.get(1))
                                .menuId(menuIds.get(1))
                                .restaurantId(restaurantIds.get(1))
                                .build(),
                        RestaurantMenuCrosswalk.builder()
                                .id(crosswalkIds.get(2))
                                .menuId(menuIds.get(2))
                                .restaurantId(restaurantIds.get(2))
                                .build()
                );

                Menu expected = Menu.builder()
                        .id(menuIds.get(0))
                        .name(cloneMenu.getName())
                        .isActive(request.getIsActive())
                        .isLinked(request.getIsLinked())
                        .build();

                Menu actual = service.createMenu(request);

                assertEquals(expected, actual);
                verify(restaurantMenuCrosswalkRepo).saveAll(expectedCrosswalks);
            }
        }

        @Nested
        @DisplayName("When clone option id is not present")
        public class WhenCloneOptionIdIsNotPresent {
            @Test
            public void whenIsLinkedIsTrue_shouldSaveOneMenuAndManyCrosswalks() {
                Integer menuId = 69;
                List<Integer> restaurantIds = asList(20, 21, 22);

                NewEditMenuRequest request = NewEditMenuRequest.builder()
                        .id(null)
                        .name("NewMenu")
                        .isActive(true)
                        .isLinked(true)
                        .cloneOptionId(0)
                        .baseRestaurantId(20)
                        .restaurantIds(restaurantIds)
                        .build();

                Menu menu = Menu.builder()
                        .id(request.getId())
                        .name(request.getName())
                        .isActive(request.getIsActive())
                        .isLinked(request.getIsLinked())
                        .build();

                List<RestaurantMenuCrosswalk> crosswalks = asList(
                        RestaurantMenuCrosswalk.builder()
                                .id(null)
                                .menuId(menuId)
                                .restaurantId(restaurantIds.get(0))
                                .build(),
                        RestaurantMenuCrosswalk.builder()
                                .id(null)
                                .menuId(menuId)
                                .restaurantId(restaurantIds.get(1))
                                .build(),
                        RestaurantMenuCrosswalk.builder()
                                .id(null)
                                .menuId(menuId)
                                .restaurantId(restaurantIds.get(2))
                                .build()
                );

                when(menuRepo.save(menu))
                        .thenAnswer(args -> {
                            Menu m = args.getArgument(0);
                            m.setId(menuId);
                            return m;
                        });

                Menu expected = Menu.builder()
                        .id(menuId)
                        .name(request.getName())
                        .isActive(request.getIsActive())
                        .isLinked(request.getIsLinked())
                        .build();

                Menu actual = service.createMenu(request);

                assertEquals(expected, actual);
                verify(menuRepo).save(expected);
                verify(restaurantMenuCrosswalkRepo).saveAll(crosswalks);
            }

            @Test
            public void whenIsLinkedIsFalse_shouldSaveManyMenusAndManyCrosswalks() {
                List<Integer> menuIds = asList(69, 70, 71);
                List<Integer> restaurantIds = asList(20, 21, 22);

                NewEditMenuRequest request = NewEditMenuRequest.builder()
                        .id(null)
                        .name("NewMenu")
                        .isActive(true)
                        .isLinked(false)
                        .cloneOptionId(0)
                        .baseRestaurantId(restaurantIds.get(0))
                        .restaurantIds(restaurantIds)
                        .build();

                List<RestaurantMenuCrosswalk> crosswalks = asList(
                        RestaurantMenuCrosswalk.builder()
                                .id(null)
                                .menuId(menuIds.get(0))
                                .restaurantId(restaurantIds.get(0))
                                .build(),
                        RestaurantMenuCrosswalk.builder()
                                .id(null)
                                .menuId(menuIds.get(1))
                                .restaurantId(restaurantIds.get(1))
                                .build(),
                        RestaurantMenuCrosswalk.builder()
                                .id(null)
                                .menuId(menuIds.get(2))
                                .restaurantId(restaurantIds.get(2))
                                .build()
                );

                when(menuRepo.save(any()))
                        .thenAnswer(args -> {
                            Menu m = args.getArgument(0);
                            m.setId(menuIds.get(0));
                            return m;
                        }).thenAnswer(args -> {
                            Menu m = args.getArgument(0);
                            m.setId(menuIds.get(1));
                            return m;
                        }).thenAnswer(args -> {
                            Menu m = args.getArgument(0);
                            m.setId(menuIds.get(2));
                            return m;
                        });

                Menu expected = Menu.builder()
                        .id(menuIds.get(0))
                        .name(request.getName())
                        .isActive(request.getIsActive())
                        .isLinked(request.getIsLinked())
                        .build();
                Menu actual = service.createMenu(request);

                assertEquals(expected, actual);
                verify(restaurantMenuCrosswalkRepo).saveAll(crosswalks);
            }
        }

        @Test
        public void whenCloneOptionIdIsInvalid_shouldThrowException() {
            NewEditMenuRequest request = NewEditMenuRequest.builder()
                    .id(null)
                    .name("NewMenu")
                    .isActive(true)
                    .isLinked(true)
                    .cloneOptionId(33)
                    .baseRestaurantId(20)
                    .restaurantIds(emptyList())
                    .build();

            Exception exception = assertThrows(Exception.class, () -> {
                service.createMenu(request);
            });

            assertFalse(exception.getMessage().isEmpty());
        }
    }

    @Nested
    @DisplayName("Update Menu")
    public class UpdateMenu {

        @Test
        public void whenNoRestaurantIdsPresent_shouldThrowException() {
            NewEditMenuRequest request = NewEditMenuRequest.builder()
                    .id(69)
                    .restaurantIds(emptyList())
                    .build();

            when(menuRepo.findById(request.getId()))
                    .thenReturn(Optional.of(Menu.builder().build()));

            Exception exception = assertThrows(Exception.class, () -> {
                service.updateMenu(request);
            });

            assertEquals("Restaurant Ids must be provided to update menu", exception.getMessage());
        }

        @Test
        public void whenMenuIdIsInvalid_shouldThrowException() {
            NewEditMenuRequest request = NewEditMenuRequest.builder()
                    .id(5)
                    .restaurantIds(asList(2, 1))
                    .build();

            Exception exception = assertThrows(Exception.class, () -> {
                service.updateMenu(request);
            });

            assertEquals("Menu not found for menuId: 5", exception.getMessage());
        }

        @Test
        public void whenRequestDoesNotContainRestaurantIdsOfExistingCrosswalks_shouldDeleteCrosswalks() throws Exception {

            Menu existingMenu = Menu.builder()
                    .id(69)
                    .name("ExistingMenu")
                    .isActive(true)
                    .isLinked(true)
                    .build();

            RestaurantMenuCrosswalk firstExistingCrosswalk = RestaurantMenuCrosswalk.builder()
                    .id(30)
                    .restaurantId(20)
                    .menuId(existingMenu.getId())
                    .build();
            RestaurantMenuCrosswalk secondExistingCrosswalk = RestaurantMenuCrosswalk.builder()
                    .id(31)
                    .restaurantId(21)
                    .menuId(existingMenu.getId())
                    .build();
            RestaurantMenuCrosswalk thirdExistingCrosswalk = RestaurantMenuCrosswalk.builder()
                    .id(32)
                    .restaurantId(22)
                    .menuId(existingMenu.getId())
                    .build();

            NewEditMenuRequest request = NewEditMenuRequest.builder()
                    .id(existingMenu.getId())
                    .name("NewMenuName")
                    .isActive(true)
                    .isLinked(true)
                    .cloneOptionId(0)
                    .baseRestaurantId(20)
                    .restaurantIds(singletonList(
                            firstExistingCrosswalk.getRestaurantId()
                    ))
                    .build();

            when(menuRepo.findById(existingMenu.getId()))
                    .thenReturn(Optional.of(existingMenu));

            when(restaurantMenuCrosswalkRepo.findByMenuId(existingMenu.getId()))
                    .thenReturn(asList(
                            firstExistingCrosswalk,
                            secondExistingCrosswalk,
                            thirdExistingCrosswalk
                    ));

            Menu actual = service.updateMenu(request);

            assertEquals(existingMenu, actual);
            verify(restaurantMenuCrosswalkRepo).deleteAll(asList(
                    secondExistingCrosswalk,
                    thirdExistingCrosswalk
            ));

        }

        @Nested
        @DisplayName("When previous menu isLinked")
        public class WhenPreviousMenuIsLinked {

            @Test
            public void whenRequestIsLinked_shouldAddCrosswalksWithOneMenuId() throws Exception {

                Menu existingMenu = Menu.builder()
                        .id(69)
                        .name("ExistingMenu")
                        .isActive(true)
                        .isLinked(true)
                        .build();

                RestaurantMenuCrosswalk firstExistingCrosswalk = RestaurantMenuCrosswalk.builder()
                        .id(30)
                        .restaurantId(20)
                        .menuId(existingMenu.getId())
                        .build();
                RestaurantMenuCrosswalk secondExistingCrosswalk = RestaurantMenuCrosswalk.builder()
                        .id(31)
                        .restaurantId(21)
                        .menuId(existingMenu.getId())
                        .build();
                RestaurantMenuCrosswalk thirdExistingCrosswalk = RestaurantMenuCrosswalk.builder()
                        .id(32)
                        .restaurantId(22)
                        .menuId(existingMenu.getId())
                        .build();

                RestaurantMenuCrosswalk firstNewCrosswalk = RestaurantMenuCrosswalk.builder()
                        .id(null)
                        .restaurantId(23)
                        .menuId(existingMenu.getId())
                        .build();
                RestaurantMenuCrosswalk secondNewCrosswalk = RestaurantMenuCrosswalk.builder()
                        .id(null)
                        .restaurantId(24)
                        .menuId(existingMenu.getId())
                        .build();

                NewEditMenuRequest request = NewEditMenuRequest.builder()
                        .id(existingMenu.getId())
                        .name("NewMenuName")
                        .isActive(true)
                        .isLinked(true)
                        .cloneOptionId(0)
                        .baseRestaurantId(20)
                        .restaurantIds(asList(
                                firstExistingCrosswalk.getRestaurantId(),
                                secondExistingCrosswalk.getRestaurantId(),
                                thirdExistingCrosswalk.getRestaurantId(),
                                firstNewCrosswalk.getRestaurantId(),
                                secondNewCrosswalk.getRestaurantId()
                        ))
                        .build();

                when(menuRepo.findById(existingMenu.getId()))
                        .thenReturn(Optional.of(existingMenu));

                when(restaurantMenuCrosswalkRepo.findByMenuId(existingMenu.getId()))
                        .thenReturn(asList(firstExistingCrosswalk, secondExistingCrosswalk, thirdExistingCrosswalk));

                Menu expected = Menu.builder()
                        .id(existingMenu.getId())
                        .name(request.getName())
                        .isLinked(request.getIsLinked())
                        .isActive(request.getIsActive())
                        .build();
                Menu actual = service.updateMenu(request);

                assertEquals(expected, actual);
                verify(restaurantMenuCrosswalkRepo).deleteAll(emptyList());
                verify(restaurantMenuCrosswalkRepo).saveAll(asList(
                        firstExistingCrosswalk,
                        secondExistingCrosswalk,
                        thirdExistingCrosswalk,
                        firstNewCrosswalk,
                        secondNewCrosswalk
                ));
            }

            @Test
            public void whenRequestIsNotLinked_shouldSetAllLinkedCrosswalksToUniqueMenus() throws Exception {
                List<Integer> restaurantIds = asList(20, 21, 22, 23, 24);
                List<Integer> crosswalkIds = asList(30, 31, 32);
                List<Integer> menuIds = asList(69, 70, 71, 72, 73);

                Menu existingMenu = Menu.builder()
                        .id(69)
                        .name("ExistingMenu")
                        .isActive(true)
                        .isLinked(true)
                        .build();

                NewEditMenuRequest request = NewEditMenuRequest.builder()
                        .id(existingMenu.getId())
                        .name("NewMenuName")
                        .isActive(true)
                        .isLinked(false)
                        .cloneOptionId(0)
                        .baseRestaurantId(restaurantIds.get(0))
                        .restaurantIds(restaurantIds)
                        .build();

                RestaurantMenuCrosswalk firstExistingCrosswalk = RestaurantMenuCrosswalk.builder()
                        .id(crosswalkIds.get(0))
                        .restaurantId(restaurantIds.get(0))
                        .menuId(existingMenu.getId())
                        .build();
                RestaurantMenuCrosswalk secondExistingCrosswalk = RestaurantMenuCrosswalk.builder()
                        .id(crosswalkIds.get(1))
                        .restaurantId(restaurantIds.get(1))
                        .menuId(existingMenu.getId())
                        .build();
                RestaurantMenuCrosswalk thirdExistingCrosswalk = RestaurantMenuCrosswalk.builder()
                        .id(crosswalkIds.get(2))
                        .restaurantId(restaurantIds.get(2))
                        .menuId(existingMenu.getId())
                        .build();

                RestaurantMenuCrosswalk firstUpdatedCrosswalk = RestaurantMenuCrosswalk.builder()
                        .id(secondExistingCrosswalk.getId())
                        .restaurantId(restaurantIds.get(1))
                        .menuId(menuIds.get(1))
                        .build();
                RestaurantMenuCrosswalk secondUpdatedCrosswalk = RestaurantMenuCrosswalk.builder()
                        .restaurantId(restaurantIds.get(2))
                        .id(thirdExistingCrosswalk.getId())
                        .menuId(menuIds.get(2))
                        .build();
                RestaurantMenuCrosswalk firstNewCrosswalk = RestaurantMenuCrosswalk.builder()
                        .restaurantId(restaurantIds.get(3))
                        .menuId(menuIds.get(3))
                        .build();
                RestaurantMenuCrosswalk secondNewCrosswalk = RestaurantMenuCrosswalk.builder()
                        .restaurantId(restaurantIds.get(4))
                        .menuId(menuIds.get(4))
                        .build();

                when(menuRepo.findById(request.getId()))
                        .thenReturn(Optional.of(existingMenu));

                when(restaurantMenuCrosswalkRepo.findByMenuId(existingMenu.getId()))
                        .thenReturn(asList(firstExistingCrosswalk, secondExistingCrosswalk, thirdExistingCrosswalk));

                when(menuRepo.save(any()))
                        .thenAnswer(args -> {
                            Menu m = args.getArgument(0);
                            m.setId(menuIds.get(0));
                            return m;
                        })
                        .thenAnswer(args -> {
                            Menu m = args.getArgument(0);
                            m.setId(menuIds.get(1));
                            return m;
                        })
                        .thenAnswer(args -> {
                            Menu m = args.getArgument(0);
                            m.setId(menuIds.get(2));
                            return m;
                        })
                        .thenAnswer(args -> {
                            Menu m = args.getArgument(0);
                            m.setId(menuIds.get(3));
                            return m;
                        })
                        .thenAnswer(args -> {
                            Menu m = args.getArgument(0);
                            m.setId(menuIds.get(4));
                            return m;
                        });

                Menu expected = Menu.builder()
                        .id(existingMenu.getId())
                        .name(request.getName())
                        .isLinked(request.getIsLinked())
                        .isActive(request.getIsActive())
                        .build();
                Menu actual = service.updateMenu(request);

                assertEquals(expected, actual);

                verify(restaurantMenuCrosswalkRepo).saveAll(asList(
                        firstExistingCrosswalk,
                        firstUpdatedCrosswalk,
                        secondUpdatedCrosswalk,
                        firstNewCrosswalk,
                        secondNewCrosswalk
                ));
                verify(restaurantMenuCrosswalkRepo).deleteAll(emptyList());
                verify(menuRepo).save(existingMenu);
            }
        }

        @Nested
        @DisplayName("When previous menu isNotLinked")
        public class WhenPreviousMenuIsNotLinked {

            @Test
            public void whenRequestIsLinked_shouldCreateCrosswalksWithOneMenu() throws Exception {
                List<Integer> restaurantIds = asList(20, 21, 22, 23, 24);

                Menu existingMenu = Menu.builder()
                        .id(69)
                        .name("FirstMenu")
                        .isActive(true)
                        .isLinked(false)
                        .build();

                RestaurantMenuCrosswalk existingCrosswalk = RestaurantMenuCrosswalk.builder()
                        .id(30)
                        .restaurantId(restaurantIds.get(0))
                        .menuId(existingMenu.getId())
                        .build();

                NewEditMenuRequest request = NewEditMenuRequest.builder()
                        .id(existingMenu.getId())
                        .name("NewName")
                        .isActive(true)
                        .isLinked(true)
                        .cloneOptionId(0)
                        .baseRestaurantId(restaurantIds.get(0))
                        .restaurantIds(restaurantIds)
                        .build();

                when(menuRepo.findById(request.getId()))
                        .thenReturn(Optional.of(existingMenu));

                when(restaurantMenuCrosswalkRepo.findByMenuId(existingMenu.getId()))
                        .thenReturn(singletonList(existingCrosswalk));

                Menu expected = Menu.builder()
                        .id(existingMenu.getId())
                        .name(request.getName())
                        .isActive(request.getIsActive())
                        .isLinked(request.getIsLinked())
                        .build();

                Menu actual = service.updateMenu(request);

                assertEquals(expected, actual);
                verify(restaurantMenuCrosswalkRepo).deleteAll(emptyList());
                verify(restaurantMenuCrosswalkRepo).saveAll(asList(
                        existingCrosswalk,
                        RestaurantMenuCrosswalk.builder()
                                .id(null)
                                .menuId(existingMenu.getId())
                                .restaurantId(restaurantIds.get(1))
                                .build(),
                        RestaurantMenuCrosswalk.builder()
                                .id(null)
                                .menuId(existingMenu.getId())
                                .restaurantId(restaurantIds.get(2))
                                .build(),
                        RestaurantMenuCrosswalk.builder()
                                .id(null)
                                .menuId(existingMenu.getId())
                                .restaurantId(restaurantIds.get(3))
                                .build(),
                        RestaurantMenuCrosswalk.builder()
                                .id(null)
                                .menuId(existingMenu.getId())
                                .restaurantId(restaurantIds.get(4))
                                .build()
                ));
            }

            @Test
            public void whenRequestIsNotLinked_shouldAddCrosswalksWithUniqueMenus() throws Exception {
                List<Integer> restaurantIds = asList(20, 21, 22, 23, 24);
                List<Integer> crosswalkIds = asList(30, 31, 32, 33, 34);
                List<Integer> menuIds = asList(69, 70, 71, 72, 73);

                Menu existingMenu = Menu.builder()
                        .id(menuIds.get(0))
                        .name("ExistingMenu")
                        .isActive(true)
                        .isLinked(false)
                        .build();

                NewEditMenuRequest request = NewEditMenuRequest.builder()
                        .id(existingMenu.getId())
                        .name("NewMenuName")
                        .isActive(true)
                        .isLinked(false)
                        .cloneOptionId(0)
                        .baseRestaurantId(restaurantIds.get(0))
                        .restaurantIds(restaurantIds)
                        .build();

                RestaurantMenuCrosswalk existingCrosswalk = RestaurantMenuCrosswalk.builder()
                        .id(crosswalkIds.get(0))
                        .restaurantId(restaurantIds.get(0))
                        .menuId(existingMenu.getId())
                        .build();

                RestaurantMenuCrosswalk firstNewCrosswalk = RestaurantMenuCrosswalk.builder()
                        .restaurantId(restaurantIds.get(1))
                        .menuId(menuIds.get(1))
                        .build();
                RestaurantMenuCrosswalk secondNewCrosswalk = RestaurantMenuCrosswalk.builder()
                        .restaurantId(restaurantIds.get(2))
                        .menuId(menuIds.get(2))
                        .build();
                RestaurantMenuCrosswalk thirdNewCrosswalk = RestaurantMenuCrosswalk.builder()
                        .restaurantId(restaurantIds.get(3))
                        .menuId(menuIds.get(3))
                        .build();
                RestaurantMenuCrosswalk fourthNewCrosswalk = RestaurantMenuCrosswalk.builder()
                        .restaurantId(restaurantIds.get(4))
                        .menuId(menuIds.get(4))
                        .build();

                when(menuRepo.findById(request.getId()))
                        .thenReturn(Optional.of(existingMenu));

                when(restaurantMenuCrosswalkRepo.findByMenuId(existingMenu.getId()))
                        .thenReturn(singletonList(existingCrosswalk));

                when(menuRepo.save(any()))
                        .thenAnswer(args -> {
                            Menu m = args.getArgument(0);
                            m.setId(menuIds.get(0));
                            return m;
                        })
                        .thenAnswer(args -> {
                            Menu m = args.getArgument(0);
                            m.setId(menuIds.get(1));
                            return m;
                        })
                        .thenAnswer(args -> {
                            Menu m = args.getArgument(0);
                            m.setId(menuIds.get(2));
                            return m;
                        })
                        .thenAnswer(args -> {
                            Menu m = args.getArgument(0);
                            m.setId(menuIds.get(3));
                            return m;
                        })
                        .thenAnswer(args -> {
                            Menu m = args.getArgument(0);
                            m.setId(menuIds.get(4));
                            return m;
                        });

                when(restaurantMenuCrosswalkRepo.saveAll(any()))
                        .then(args -> {
                            List<RestaurantMenuCrosswalk> xwalks = args.getArgument(0);
                            assertEquals(xwalks.size(), restaurantIds.size());
                            return xwalks;
                        });

                Menu expected = Menu.builder()
                        .id(existingMenu.getId())
                        .name(request.getName())
                        .isLinked(request.getIsLinked())
                        .isActive(request.getIsActive())
                        .build();
                Menu actual = service.updateMenu(request);

                assertEquals(expected, actual);
                verify(restaurantMenuCrosswalkRepo).saveAll(asList(
                        existingCrosswalk,
                        firstNewCrosswalk,
                        secondNewCrosswalk,
                        thirdNewCrosswalk,
                        fourthNewCrosswalk
                ));
                verify(restaurantMenuCrosswalkRepo).deleteAll(emptyList());
                verify(menuRepo).save(existingMenu);

            }
        }

    }

    @Nested
    @DisplayName("getMenuDetails")
    public class GetMenuDetails {

        @Nested
        @DisplayName("When menu id present")
        public class WhenMenuIdPresent {
            @Test
            public void shouldReturnMenuAndLinkedRestaurants() {
                Integer menuId = 69;

                Menu menu = Menu.builder()
                        .id(menuId)
                        .name("Menu")
                        .isLinked(true)
                        .isActive(true)
                        .build();

                Restaurant firstRestaurant = Restaurant.builder()
                        .id(20)
                        .name("FirstRestaurant")
                        .details("details")
                        .phoneNumber("2223334444")
                        .city("FirstCity")
                        .state("FirstState")
                        .zipCode("13331")
                        .streetAddress("FirstStreet")
                        .emailAddress("FirstEmail")
                        .build();
                Restaurant secondRestaurant = Restaurant.builder()
                        .id(20)
                        .name("SecondRestaurant")
                        .details("details")
                        .phoneNumber("2223334444")
                        .city("SecondCity")
                        .state("SecondState")
                        .zipCode("13331")
                        .streetAddress("SecondStreet")
                        .emailAddress("SecondEmail")
                        .build();
                Restaurant thirdRestaurant = Restaurant.builder()
                        .id(20)
                        .name("ThirdRestaurant")
                        .details("details")
                        .phoneNumber("2223334444")
                        .city("ThirdCity")
                        .state("ThirdState")
                        .zipCode("13331")
                        .streetAddress("ThirdStreet")
                        .emailAddress("ThirdEmail")
                        .build();

                when(restaurantService.getRestaurants())
                        .thenReturn(asList(
                                firstRestaurant,
                                secondRestaurant,
                                thirdRestaurant
                        ));

                when(menuRepo.findById(menuId))
                        .thenReturn(Optional.ofNullable(menu));

                when(restaurantService.getByMenuId(menuId))
                        .thenReturn(asList(firstRestaurant, thirdRestaurant));

                MenuDetailsResponse expected = MenuDetailsResponse.builder()
                        .id(menuId)
                        .name(menu.getName())
                        .isActive(menu.getIsActive())
                        .isLinked(menu.getIsLinked())
                        .linkedRestaurants(asList(firstRestaurant, thirdRestaurant))
                        .allRestaurants(asList(firstRestaurant, secondRestaurant, thirdRestaurant))
                        .build();

                MenuDetailsResponse actual = service.getMenuDetails(menuId);

                assertEquals(expected, actual);
            }
        }

        @Nested
        @DisplayName("When menu id is not present")
        public class WhenMenuIdIsNotPresent {
            @Test
            public void shouldReturnListOfAllRestaurantsAndNoMenuInfo() {
                Restaurant firstRestaurant = Restaurant.builder()
                        .id(20)
                        .name("FirstRestaurant")
                        .details("details")
                        .phoneNumber("2223334444")
                        .city("FirstCity")
                        .state("FirstState")
                        .zipCode("13331")
                        .streetAddress("FirstStreet")
                        .emailAddress("FirstEmail")
                        .build();
                Restaurant secondRestaurant = Restaurant.builder()
                        .id(20)
                        .name("SecondRestaurant")
                        .details("details")
                        .phoneNumber("2223334444")
                        .city("SecondCity")
                        .state("SecondState")
                        .zipCode("13331")
                        .streetAddress("SecondStreet")
                        .emailAddress("SecondEmail")
                        .build();
                Restaurant thirdRestaurant = Restaurant.builder()
                        .id(20)
                        .name("ThirdRestaurant")
                        .details("details")
                        .phoneNumber("2223334444")
                        .city("ThirdCity")
                        .state("ThirdState")
                        .zipCode("13331")
                        .streetAddress("ThirdStreet")
                        .emailAddress("ThirdEmail")
                        .build();

                when(restaurantService.getRestaurants())
                        .thenReturn(asList(
                                firstRestaurant,
                                secondRestaurant,
                                thirdRestaurant
                        ));

                MenuDetailsResponse expected = MenuDetailsResponse.builder()
                        .allRestaurants(asList(firstRestaurant, secondRestaurant, thirdRestaurant))
                        .build();

                MenuDetailsResponse actual = service.getMenuDetails(0);

                assertEquals(expected, actual);
            }
        }

    }

    @Nested
    @DisplayName("updateMenuContents")
    public class UpdateMenuContents {
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
        @DisplayName("When allergen present on MenuItemDetails but crosswalk does not exist")
        public class WhenAllergenPresentOnMenuItemDetailsButCrosswalkDoesNotExist {
            @Test
            public void shouldSaveCrosswalks() {
                Integer menuId = 69;

                List<MenuItemGroupDetails> request = asList(
                        MenuItemGroupDetails.builder()
                                .id(420)
                                .menuId(menuId)
                                .name("MIGDName")
                                .position(0)
                                .menuItems(asList(
                                        MenuItemDetails.builder()
                                                .id(690)
                                                .menuId(menuId)
                                                .menuItemGroupId(420)
                                                .name("1-1")
                                                .description("1-1D")
                                                .extraDetails("1-1ED")
                                                .price(9.99)
                                                .position(0)
                                                .allergens(asList(wheatAllergen, soyAllergen))
                                                .build(),
                                        MenuItemDetails.builder()
                                                .id(691)
                                                .menuId(menuId)
                                                .menuItemGroupId(420)
                                                .name("1-2")
                                                .description("1-2D")
                                                .extraDetails("1-2ED")
                                                .price(19.99)
                                                .position(1)
                                                .allergens(asList(wheatAllergen, milkAllergen))
                                                .build(),
                                        MenuItemDetails.builder()
                                                .id(692)
                                                .menuId(menuId)
                                                .menuItemGroupId(420)
                                                .name("1-3")
                                                .description("1-3D")
                                                .extraDetails("1-3ED")
                                                .price(199.99)
                                                .position(2)
                                                .allergens(asList(sesameAllergen, eggAllergen))
                                                .build()
                                ))
                                .build(),
                        MenuItemGroupDetails.builder()
                                .id(421)
                                .menuId(menuId)
                                .name("MIGDName2")
                                .position(1)
                                .menuItems(asList(
                                        MenuItemDetails.builder()
                                                .id(700)
                                                .menuId(menuId)
                                                .menuItemGroupId(421)
                                                .name("2-1")
                                                .description("2-1D")
                                                .extraDetails("2-1ED")
                                                .price(3.33)
                                                .position(0)
                                                .allergens(asList(shellfishAllergen, peanutAllergen))
                                                .build(),
                                        MenuItemDetails.builder()
                                                .id(701)
                                                .menuId(menuId)
                                                .menuItemGroupId(421)
                                                .name("2-2")
                                                .description("2-2D")
                                                .extraDetails("2-2ED")
                                                .price(13.33)
                                                .position(1)
                                                .allergens(singletonList(soyAllergen))
                                                .build(),
                                        MenuItemDetails.builder()
                                                .id(702)
                                                .menuId(menuId)
                                                .menuItemGroupId(421)
                                                .name("2-3")
                                                .description("2-3D")
                                                .extraDetails("2-3ED")
                                                .price(33.33)
                                                .position(2)
                                                .allergens(asList(soyAllergen, wheatAllergen, eggAllergen, sesameAllergen))
                                                .build()
                                ))
                                .build()
                );

                //First allergen on each menu item - rest in list should be saved
                MenuItemAllergen firstLinkedMIA = MenuItemAllergen.builder()
                        .id(100)
                        .menuItemId(request.get(0).getMenuItems().get(0).getId())
                        .allergenId(wheatAllergen.getId())
                        .mayContain(false)
                        .refined(false)
                        .build();
                MenuItemAllergen secondLinkedMIA = MenuItemAllergen.builder()
                        .id(101)
                        .menuItemId(request.get(0).getMenuItems().get(1).getId())
                        .allergenId(wheatAllergen.getId())
                        .mayContain(false)
                        .refined(false)
                        .build();
                MenuItemAllergen thirdLinkedMIA = MenuItemAllergen.builder()
                        .id(102)
                        .menuItemId(request.get(0).getMenuItems().get(2).getId())
                        .allergenId(sesameAllergen.getId())
                        .mayContain(false)
                        .refined(false)
                        .build();
                MenuItemAllergen fourthLinkedMIA = MenuItemAllergen.builder()
                        .id(103)
                        .menuItemId(request.get(1).getMenuItems().get(0).getId())
                        .allergenId(shellfishAllergen.getId())
                        .mayContain(false)
                        .refined(false)
                        .build();
                MenuItemAllergen fifthLinkedMIA = MenuItemAllergen.builder()
                        .id(104)
                        .menuItemId(request.get(1).getMenuItems().get(1).getId())
                        .allergenId(soyAllergen.getId())
                        .mayContain(false)
                        .refined(false)
                        .build();
                MenuItemAllergen sixthLinkedMIA = MenuItemAllergen.builder()
                        .id(105)
                        .menuItemId(request.get(1).getMenuItems().get(2).getId())
                        .allergenId(soyAllergen.getId())
                        .mayContain(false)
                        .refined(false)
                        .build();

                when(allergenService.getAllByMenuItemId(request.get(0).getMenuItems().get(0).getId()))
                        .thenReturn(singletonList(firstLinkedMIA));
                when(allergenService.getAllByMenuItemId(request.get(0).getMenuItems().get(1).getId()))
                        .thenReturn(singletonList(secondLinkedMIA));
                when(allergenService.getAllByMenuItemId(request.get(0).getMenuItems().get(2).getId()))
                        .thenReturn(singletonList(thirdLinkedMIA));
                when(allergenService.getAllByMenuItemId(request.get(1).getMenuItems().get(0).getId()))
                        .thenReturn(singletonList(fourthLinkedMIA));
                when(allergenService.getAllByMenuItemId(request.get(1).getMenuItems().get(1).getId()))
                        .thenReturn(singletonList(fifthLinkedMIA));
                when(allergenService.getAllByMenuItemId(request.get(1).getMenuItems().get(2).getId()))
                        .thenReturn(singletonList(sixthLinkedMIA));

                List<MenuItemGroupDetails> actual = service.updateMenuContents(request);
                assertEquals(request, actual);

                verify(menuItemGroupService).saveGroups(asList(
                        MenuItemGroup.builder()
                                .id(request.get(0).getId())
                                .menuId(request.get(0).getMenuId())
                                .name(request.get(0).getName())
                                .position(request.get(0).getPosition())
                                .build(),
                        MenuItemGroup.builder()
                                .id(request.get(1).getId())
                                .menuId(request.get(1).getMenuId())
                                .name(request.get(1).getName())
                                .position(request.get(1).getPosition())
                                .build()
                ));

                //First Group Verification

                MenuItem firstGroupFirstMenuItem = new MenuItem(request.get(0).getMenuItems().get(0));
                MenuItem firstGroupSecondMenuItem = new MenuItem(request.get(0).getMenuItems().get(1));
                MenuItem firstGroupThirdMenuItem = new MenuItem(request.get(0).getMenuItems().get(2));
                firstGroupFirstMenuItem.setMenuItemGroupId(request.get(0).getId());
                firstGroupSecondMenuItem.setMenuItemGroupId(request.get(0).getId());
                firstGroupThirdMenuItem.setMenuItemGroupId(request.get(0).getId());

                verify(menuItemService).saveMenuItems(asList(firstGroupFirstMenuItem, firstGroupSecondMenuItem, firstGroupThirdMenuItem));

                verify(allergenService).saveMenuItemAllergens(asList(
                        MenuItemAllergen.builder()
                                .menuItemId(request.get(0).getMenuItems().get(0).getId())
                                .allergenId(soyAllergen.getId())
                                .build(),
                        MenuItemAllergen.builder()
                                .menuItemId(request.get(0).getMenuItems().get(1).getId())
                                .allergenId(milkAllergen.getId())
                                .build(),
                        MenuItemAllergen.builder()
                                .menuItemId(request.get(0).getMenuItems().get(2).getId())
                                .allergenId(eggAllergen.getId())
                                .build()
                ));

                //End First Group Verification

                //Second Group Verification

                MenuItem secondGroupFirstMenuItem = new MenuItem(request.get(1).getMenuItems().get(0));
                MenuItem secondGroupSecondMenuItem = new MenuItem(request.get(1).getMenuItems().get(1));
                MenuItem secondGroupThirdMenuItem = new MenuItem(request.get(1).getMenuItems().get(2));
                secondGroupFirstMenuItem.setMenuItemGroupId(request.get(1).getId());
                secondGroupSecondMenuItem.setMenuItemGroupId(request.get(1).getId());
                secondGroupThirdMenuItem.setMenuItemGroupId(request.get(1).getId());

                verify(menuItemService).saveMenuItems(asList(secondGroupFirstMenuItem, secondGroupSecondMenuItem, secondGroupThirdMenuItem));

                verify(allergenService).saveMenuItemAllergens(asList(
                        MenuItemAllergen.builder()
                                .menuItemId(request.get(1).getMenuItems().get(0).getId())
                                .allergenId(peanutAllergen.getId())
                                .build(),
                        MenuItemAllergen.builder()
                                .menuItemId(request.get(1).getMenuItems().get(2).getId())
                                .allergenId(wheatAllergen.getId())
                                .build(),
                        MenuItemAllergen.builder()
                                .menuItemId(request.get(1).getMenuItems().get(2).getId())
                                .allergenId(eggAllergen.getId())
                                .build(),
                        MenuItemAllergen.builder()
                                .menuItemId(request.get(1).getMenuItems().get(2).getId())
                                .allergenId(sesameAllergen.getId())
                                .build()
                ));

                //End Second Group Verification

                verify(allergenService).deleteUnusedMenuItemAllergens(asList(
                        firstGroupFirstMenuItem.getId(),
                        firstGroupSecondMenuItem.getId(),
                        firstGroupThirdMenuItem.getId(),
                        secondGroupFirstMenuItem.getId(),
                        secondGroupSecondMenuItem.getId(),
                        secondGroupThirdMenuItem.getId()
                ), menuId);

                verify(menuItemService).deleteUnusedMenuItems(asList(
                        firstGroupFirstMenuItem.getId(),
                        firstGroupSecondMenuItem.getId(),
                        firstGroupThirdMenuItem.getId(),
                        secondGroupFirstMenuItem.getId(),
                        secondGroupSecondMenuItem.getId(),
                        secondGroupThirdMenuItem.getId()
                ), menuId);

                verify(menuItemGroupService).deleteUnusedGroups(asList(
                        request.get(0).getId(),
                        request.get(1).getId()
                ), menuId);
            }
        }

        @Nested
        @DisplayName("When existing crosswalk's allergen id is not in request")
        public class WhenExistingCrosswalksAllergenIdIsNotInRequest {
            @Test
            public void shouldDeleteCrosswalks() {
                Integer menuId = 69;

                List<MenuItemGroupDetails> request = singletonList(
                        MenuItemGroupDetails.builder()
                                .id(420)
                                .menuId(menuId)
                                .name("MIGDName")
                                .position(0)
                                .menuItems(asList(
                                        MenuItemDetails.builder()
                                                .id(690)
                                                .menuId(menuId)
                                                .menuItemGroupId(420)
                                                .name("1-1")
                                                .description("1-1D")
                                                .extraDetails("1-1ED")
                                                .price(9.99)
                                                .position(0)
                                                .allergens(singletonList(wheatAllergen))
                                                .build(),
                                        MenuItemDetails.builder()
                                                .id(691)
                                                .menuId(menuId)
                                                .menuItemGroupId(420)
                                                .name("1-2")
                                                .description("1-2D")
                                                .extraDetails("1-2ED")
                                                .price(19.99)
                                                .position(1)
                                                .allergens(singletonList(wheatAllergen))
                                                .build()
                                ))
                                .build()
                );

                MenuItemAllergen firstLinkedMIA = MenuItemAllergen.builder()
                        .id(100)
                        .menuItemId(request.get(0).getMenuItems().get(0).getId())
                        .allergenId(wheatAllergen.getId())
                        .mayContain(false)
                        .refined(false)
                        .build();
                MenuItemAllergen secondLinkedMIA = MenuItemAllergen.builder()
                        .id(101)
                        .menuItemId(request.get(0).getMenuItems().get(0).getId())
                        .allergenId(soyAllergen.getId())
                        .mayContain(false)
                        .refined(false)
                        .build();
                MenuItemAllergen thirdLinkedMIA = MenuItemAllergen.builder()
                        .id(102)
                        .menuItemId(request.get(0).getMenuItems().get(1).getId())
                        .allergenId(wheatAllergen.getId())
                        .mayContain(false)
                        .refined(false)
                        .build();
                MenuItemAllergen fourthLinkedMIA = MenuItemAllergen.builder()
                        .id(102)
                        .menuItemId(request.get(0).getMenuItems().get(1).getId())
                        .allergenId(milkAllergen.getId())
                        .mayContain(false)
                        .refined(false)
                        .build();

                when(allergenService.getAllByMenuItemId(request.get(0).getMenuItems().get(0).getId()))
                        .thenReturn(asList(firstLinkedMIA, secondLinkedMIA));
                when(allergenService.getAllByMenuItemId(request.get(0).getMenuItems().get(1).getId()))
                        .thenReturn(asList(thirdLinkedMIA, fourthLinkedMIA));

                List<MenuItemGroupDetails> actual = service.updateMenuContents(request);
                assertEquals(request, actual);

                verify(allergenService).deleteMenuItemAllergens(singletonList(secondLinkedMIA));
                verify(allergenService).deleteMenuItemAllergens(singletonList(fourthLinkedMIA));

                MenuItem firstGroupFirstMenuItem = new MenuItem(request.get(0).getMenuItems().get(0));
                MenuItem firstGroupSecondMenuItem = new MenuItem(request.get(0).getMenuItems().get(1));
                firstGroupFirstMenuItem.setMenuItemGroupId(request.get(0).getId());
                firstGroupSecondMenuItem.setMenuItemGroupId(request.get(0).getId());

                verify(menuItemService).saveMenuItems(asList(firstGroupFirstMenuItem, firstGroupSecondMenuItem));

                verify(allergenService).deleteUnusedMenuItemAllergens(asList(
                        firstGroupFirstMenuItem.getId(),
                        firstGroupSecondMenuItem.getId()
                ), menuId);

                verify(menuItemService).deleteUnusedMenuItems(asList(
                        firstGroupFirstMenuItem.getId(),
                        firstGroupSecondMenuItem.getId()
                ), menuId);

                verify(menuItemGroupService).deleteUnusedGroups(singletonList(
                        request.get(0).getId()
                ), menuId);
            }
        }
    }


    @Test
    public void deleteMenuById_shouldDeleteMenu() {
        service.deleteMenuById(20);
        verify(menuJdbcRepo).deleteMenuAndChildren(20);
    }
}
