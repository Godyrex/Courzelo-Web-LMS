package org.example.courzelo.support;

import org.example.courzelo.models.MailExchange;
import org.example.courzelo.repositories.MailExchangeRepository;
import org.example.courzelo.services.MailService;
import org.springframework.boot.test.context.SpringBootTest;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;

@SpringBootTest

public class Mail {
    @Mock
    private MailExchangeRepository mailExchangeRepository;

    @InjectMocks
    private MailService mailExchangeService;

    private MailExchange mailExchange;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mailExchange = new MailExchange();
        mailExchange.setId("1");
        mailExchange.setSubject("Test Subject");
        mailExchange.setDetails("Test Details");
        mailExchange.setDateCreation(LocalDateTime.now());
        // Mock User entities can be set if needed
    }

    @Test
    public void testSaveMailExchange() {
        when(mailExchangeRepository.save(mailExchange)).thenReturn(mailExchange);

        MailExchange savedMailExchange = mailExchangeService.saveMail(mailExchange);

        assertThat(savedMailExchange).isNotNull();
        assertThat(savedMailExchange.getSubject()).isEqualTo("Test Subject");
        verify(mailExchangeRepository).save(mailExchange);
    }

    @Test
    public void testUpdateMailExchange() {
        when(mailExchangeRepository.save(mailExchange)).thenReturn(mailExchange);

        MailExchange updatedMailExchange = mailExchangeService.updateMail(mailExchange);

        assertThat(updatedMailExchange).isNotNull();
        assertThat(updatedMailExchange.getSubject()).isEqualTo("Test Subject");
        verify(mailExchangeRepository).save(mailExchange);
    }

    @Test
    public void testDeleteMailExchange() {
        doNothing().when(mailExchangeRepository).deleteById("1");

        mailExchangeService.deleteMail("1");

        verify(mailExchangeRepository).deleteById("1");
    }
}
