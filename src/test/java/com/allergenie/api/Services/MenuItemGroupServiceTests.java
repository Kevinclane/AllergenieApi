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
import static org.mockito.Mockito.verify;

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
        @Test
        public void shouldCallMenuItemGroupRepo() {
            List<Integer> groupIds = asList(3, 6, 9);
            Integer menuId = 11;

            service.deleteUnusedGroups(groupIds, menuId);
            verify(menuItemGroupRepo).deleteUnusedGroups(groupIds, menuId);
        }
    }
}
