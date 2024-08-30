package org.example.courzelo.support;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

import org.example.courzelo.repositories.FaqRepository;
import org.example.courzelo.services.FaqService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.example.courzelo.models.FAQ;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest

public class FAQTest {

    @Mock
    private FaqRepository faqRepository;

    @InjectMocks
    private FaqService faqService;

    private FAQ faq;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        faq = new FAQ();
        faq.setId("1");
        faq.setQuestion("Test Question");
        faq.setAnswer("Test Answer");
    }

    @Test
    public void testSaveFAQ() {
        when(faqRepository.save(faq)).thenReturn(faq);

        FAQ savedFAQ = faqService.saveFAQ1(faq);

        assertThat(savedFAQ).isNotNull();
        assertThat(savedFAQ.getQuestion()).isEqualTo("Test Question");
        verify(faqRepository).save(faq);
    }

    @Test
    public void testUpdateFAQ() {
        when(faqRepository.save(faq)).thenReturn(faq);

        FAQ updatedFAQ = faqService.updateFAQ1(faq);

        assertThat(updatedFAQ).isNotNull();
        assertThat(updatedFAQ.getQuestion()).isEqualTo("Test Question");
        verify(faqRepository).save(faq);
    }

    @Test
    public void testDeleteFAQ() {
        doNothing().when(faqRepository).deleteById("1");

        faqService.deleteFAQ("1");

        verify(faqRepository).deleteById("1");
    }
}
