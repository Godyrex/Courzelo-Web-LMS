package org.example.courzelo.Communication;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

import org.example.courzelo.models.GroupChat.Group;
import org.example.courzelo.repositories.Groups.GroupMessageRepository;
import org.example.courzelo.serviceImpls.Groups.GroupService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SpringBootTest
public class GroupTest {
    @Mock
    private GroupMessageRepository groupRepository;

    @InjectMocks
    private GroupService groupService;

    private Group group;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        group = new Group();
        group.setId("1");
        group.setName("Test Group");
        group.setMembers(new ArrayList<>());
        // Mock User entities can be set if needed
    }

    @Test
    public void testCreateGroup() {
        when(groupRepository.save(group)).thenReturn(group);

        Group savedGroup = groupService.createGroup(group);

        assertThat(savedGroup).isNotNull();
        assertThat(savedGroup.getName()).isEqualTo("Test Group");
        verify(groupRepository).save(group);
    }

    @Test
    public void testGetGroupById() {
        when(groupRepository.findById("1")).thenReturn(Optional.of(group));

        Group foundGroup = groupService.getGroupById("1");

        assertThat(foundGroup).isNotNull();
        assertThat(foundGroup.getId()).isEqualTo("1");
        verify(groupRepository).findById("1");
    }

    @Test
    public void testGetAllGroups() {
        List<Group> groups = new ArrayList<>();
        groups.add(group);
        when(groupRepository.findAll()).thenReturn(groups);

        List<Group> allGroups = groupService.getAllGroups();

        assertThat(allGroups).hasSize(1);
        verify(groupRepository).findAll();
    }

    @Test
    public void testDeleteGroup() {
        doNothing().when(groupRepository).deleteById("1");

        groupService.deleteGroup("1");

        verify(groupRepository).deleteById("1");
    }
}
