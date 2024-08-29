package org.example.courzelo.stages;


import org.example.courzelo.models.Stages;
import org.example.courzelo.models.User;
import org.example.courzelo.repositories.StagesRepository;
import org.example.courzelo.repositories.UserRepository;
import org.example.courzelo.serviceImpls.StagesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@SpringBootTest
public class StagesServiceTest {

    @InjectMocks
    private StagesService stagesService;

    @Mock
    private StagesRepository stagesRepository;

    @Mock
    private UserRepository userRepository;

    private Stages stage;
    private User student;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Create sample stage and user for testing
        stage = new Stages();
        stage.setId("stage-id");
        stage.setName("Test Stage");

        student = new User();
        student.setId("student-id");
        

        // Mock repository behavior
        when(stagesRepository.findById(anyString())).thenReturn(Optional.of(stage));
        when(stagesRepository.save(any(Stages.class))).thenReturn(stage);
        when(stagesRepository.findAll()).thenReturn(List.of(stage));
        when(stagesRepository.count()).thenReturn(1L);
        when(userRepository.findById(anyString())).thenReturn(Optional.of(student));
    }

    @Test
    void retrieveAllStages_successfullyRetrievesStages() {
        // Act
        List<Stages> stages = stagesService.retrieveAllStages();

        // Assert
        assertEquals(1, stages.size());
        assertEquals("Test Stage", stages.get(0).getName());
    }

    @Test
    void retrieveStage_successfullyRetrievesStage() {
        // Act
        Stages retrievedStage = stagesService.retrieveStage(stage.getId());

        // Assert
        assertEquals("Test Stage", retrievedStage.getName());
    }

    @Test
    void retrieveStage_returnsNullForNonExistentStage() {
        // Arrange
        when(stagesRepository.findById("non-existent-id")).thenReturn(Optional.empty());

        // Act
        Stages retrievedStage = stagesService.retrieveStage("non-existent-id");

        // Assert
        assertEquals(null, retrievedStage);
    }

    @Test
    void addStage_successfullyAddsStage() {
        // Act
        Stages addedStage = stagesService.addStage(stage);

        // Assert
        assertEquals("Test Stage", addedStage.getName());
    }

    @Test
    void removeStage_successfullyRemovesStage() {
        // Act
        stagesService.removeStage(stage.getId());

        // Assert
        verify(stagesRepository, times(1)).deleteById(stage.getId());
    }

    @Test
    void modifyStage_successfullyModifiesStage() {
        // Arrange
        stage.setName("Updated Stage");

        // Act
        Stages modifiedStage = stagesService.modifyStage(stage);

        // Assert
        assertEquals("Updated Stage", modifiedStage.getName());
    }

    @Test
    void GetNumberOfStage_successfullyCountsStages() {
        // Act
        Long count = stagesService.GetNumberOfStage();

        // Assert
        assertEquals(1L, count);
    }

    @Test
    void AssignStudentToInternship_successfullyAssignsStudent() {
        // Act
        stagesService.AssignStudentToInternship(student.getId(), stage.getId());

        // Assert
        verify(stagesRepository, times(1)).save(stage);
    }

    @Test
    void AssignStudentToInternship_logsErrorForNonExistentStudent() {
        // Arrange
        when(userRepository.findById("non-existent-student-id")).thenReturn(Optional.empty());

        // Act
        stagesService.AssignStudentToInternship("non-existent-student-id", stage.getId());

        // Assert
        verify(stagesRepository, times(0)).save(any(Stages.class));
    }

    @Test
    void AssignStudentToInternship_logsErrorForNonExistentStage() {
        // Arrange
        when(stagesRepository.findById("non-existent-stage-id")).thenReturn(Optional.empty());

        // Act
        stagesService.AssignStudentToInternship(student.getId(), "non-existent-stage-id");

        // Assert
        verify(stagesRepository, times(0)).save(any(Stages.class));
    }
}
