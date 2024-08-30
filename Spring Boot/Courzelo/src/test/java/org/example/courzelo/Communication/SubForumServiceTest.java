package org.example.courzelo.Communication;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

import org.example.courzelo.models.Forum.SubForum;
import org.example.courzelo.repositories.Forum.SubforumRepository;
import org.example.courzelo.serviceImpls.Forum.SubForumServiceImpl;
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
public class SubForumServiceTest {

    @Mock
    private SubforumRepository subForumRepository;

    @InjectMocks
    private SubForumServiceImpl subForumService;

    private SubForum subForum;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        subForum = new SubForum();
        subForum.setId("1");
        subForum.setName("Test SubForum");
    }

    @Test
    public void testCreateSubForum() {
        when(subForumRepository.save(subForum)).thenReturn(subForum);

        SubForum savedSubForum = subForumService.saveSub(subForum);

        assertThat(savedSubForum).isNotNull();
        assertThat(savedSubForum.getName()).isEqualTo("Test SubForum");
        verify(subForumRepository).save(subForum);
    }

    @Test
    public void testGetSubForumById() {
        when(subForumRepository.findById("1")).thenReturn(Optional.of(subForum));

        SubForum foundSubForum = subForumService.getSubForum("1");

        assertThat(foundSubForum).isNotNull();
        assertThat(foundSubForum.getId()).isEqualTo("1");
        verify(subForumRepository).findById("1");
    }

    @Test
    public void testGetAllSubForums() {
        List<SubForum> subForums = new ArrayList<>();
        subForums.add(subForum);
        when(subForumRepository.findAll()).thenReturn(subForums);

        List<SubForum> allSubForums = subForumService.getSubForums();

        assertThat(allSubForums).hasSize(1);
        verify(subForumRepository).findAll();
    }

    @Test
    public void testDeleteSubForum() {
        doNothing().when(subForumRepository).deleteById("1");

        subForumService.deleteSubForum("1");

        verify(subForumRepository).deleteById("1");
    }
}
