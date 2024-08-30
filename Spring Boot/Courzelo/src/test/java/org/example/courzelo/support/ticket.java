package org.example.courzelo.support;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;
import org.example.courzelo.models.Status;
import org.example.courzelo.models.Ticket;
import org.example.courzelo.repositories.TicketRepository;
import org.example.courzelo.services.TicketServiceImp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

@SpringBootTest
public class ticket {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private TicketServiceImp ticketServiceImp;
    private Ticket ticket;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        ticket = new Ticket();
        ticket.setId("1");
        ticket.setSujet("Test Subject");
        ticket.setDetails("Test Details");
        ticket.setDateCreation(LocalDateTime.now());
        ticket.setStatus(Status.EN_ATTENTE);
    }

    @Test
    public void testSaveTicket() {
        when(ticketRepository.save(ticket)).thenReturn(ticket);

        Ticket savedTicket = ticketServiceImp.saveTicket1(ticket);

        assertThat(savedTicket).isNotNull();
        assertThat(savedTicket.getSujet()).isEqualTo("Test Subject");
        verify(ticketRepository).save(ticket);
    }

    @Test
    public void testUpdateTicket() {
        when(ticketRepository.save(ticket)).thenReturn(ticket);

        Ticket updatedTicket = ticketServiceImp.updateTicket2(ticket);

        assertThat(updatedTicket).isNotNull();
        assertThat(updatedTicket.getSujet()).isEqualTo("Test Subject");
        verify(ticketRepository).save(ticket);
    }

    @Test
    public void testDeleteTicket() {
        doNothing().when(ticketRepository).deleteById("1");

        ticketServiceImp.deleteTicket("1");

        verify(ticketRepository).deleteById("1");
    }

}
