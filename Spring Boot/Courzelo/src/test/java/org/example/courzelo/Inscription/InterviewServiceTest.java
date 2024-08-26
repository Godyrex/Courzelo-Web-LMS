package org.example.courzelo.Inscription;

import org.example.courzelo.models.Application.Interview;
import org.example.courzelo.models.User;
import org.example.courzelo.repositories.Application.InterviewRepository;
import org.example.courzelo.services.Application.InterviewService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class InterviewServiceTest {

    @Mock
    private InterviewRepository interviewRepository;

    @InjectMocks
    private InterviewService interviewService;

    public InterviewServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testScheduleInterview() {
        User interviewer = new User();
        interviewer.setEmail("interviewer@example.com");

        Interview interview = new Interview();
        interview.setInterviewer(interviewer);

        when(interviewRepository.save(any(Interview.class))).thenReturn(interview);

        Interview scheduledInterview = interviewService.saveInterview(interview);

        assertNotNull(scheduledInterview);
        assertEquals("interviewer@example.com", scheduledInterview.getInterviewer().getEmail());
    }

    @Test
    public void testFindByInterviewer() {
        User interviewer = new User();
        interviewer.setEmail("interviewer@example.com");

        Interview interview1 = new Interview();
        interview1.setInterviewer(interviewer);

        Interview interview2 = new Interview();
        interview2.setInterviewer(interviewer);

        when(interviewRepository.findByInterviewer(interviewer))
                .thenReturn(List.of(interview1, interview2));

        List<Interview> results = interviewService.getInterviewByUser(interviewer);

        assertNotNull(results);
        assertEquals(2, results.size());
    }
}
