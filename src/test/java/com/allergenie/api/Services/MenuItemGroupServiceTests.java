package com.allergenie.api.Services;

import com.allergenie.api.Models.Entities.MenuItemGroup;
import com.allergenie.api.Repos.MenuItemGroupRepo;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MenuItemGroupServiceTests {
    @Mock
    private MenuItemGroupRepo menuItemGroupRepo;

    @InjectMocks
    private MenuItemGroupService service;

    @Nested
    @DisplayName("saveGroups")
    public class SaveGroups {
        @Test
        public void shouldCallMenuItemGroupRepo() {
            List<MenuItemGroup> groups = asList(
                    MenuItemGroup.builder()
                            .position(0)
                            .name("first")
                            .menuId(33)
                            .build(),
                    MenuItemGroup.builder()
                            .position(1)
                            .name("second")
                            .menuId(33)
                            .build()
            );

            service.saveGroups(groups);
            verify(menuItemGroupRepo).saveAll(groups);
        }
    }

    @Nested
    @DisplayName("deleteUnusedGroups")
    public class DeleteUnusedGroups {

        @Nested
        @DisplayName("WhenIdsAreReturned")
        public class WhenIdsAreReturned {
            @Test
            public void shouldCallRepoToDeleteByIds() {
                List<Integer> menuItemIds = asList(3, 5, 6);
                Integer menuId = 11;
                List<Integer> idsToDelete = asList(69, 70, 71);

                when(menuItemGroupRepo.findGroupIdsToDelete(menuItemIds, menuId))
                        .thenReturn(idsToDelete);

                service.deleteUnusedGroups(menuItemIds, menuId);
                verify(menuItemGroupRepo).deleteByIdIn(idsToDelete);
            }
        }

        @Nested
        @DisplayName("WhenNoIdsAreReturned")
        public class WhenNoIdsAreReturned {
            @Test
            public void shouldNotCallRepoToDeleteByIds() {
                List<Integer> menuItemIds = asList(3, 5, 6);
                Integer menuId = 11;

                when(menuItemGroupRepo.findGroupIdsToDelete(menuItemIds, menuId))
                        .thenReturn(emptyList());

                service.deleteUnusedGroups(menuItemIds, menuId);
                verifyNoMoreInteractions(menuItemGroupRepo);
            }
        }
    }
}
