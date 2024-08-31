package org.example.courzelo.Communication;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

import org.example.courzelo.models.GroupChat.Group;
import org.example.courzelo.models.GroupChat.Message;
import org.example.courzelo.repositories.Groups.MessageRepository;
import org.example.courzelo.serviceImpls.Groups.MessageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class MessageTest {

    @Mock
    private MessageRepository messageRepository;

    @InjectMocks
    private MessageService messageService;

    private Message message;
    private Group group;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        group = new Group();
        group.setId("1");
        group.setName("Test Group");

        message = new Message();
        message.setId("1");
        message.setGroup(group);
        message.setText("Test Message");
    }

    @Test
    public void testCreateMessage() {
        when(messageRepository.save(message)).thenReturn(message);

        Message savedMessage = messageService.sendMessage(message);

        assertThat(savedMessage).isNotNull();
        assertThat(savedMessage.getText()).isEqualTo("Test Message");
        verify(messageRepository).save(message);
    }

    @Test
    public void testGetMessagesByGroupId() {
        List<Message> messages = new ArrayList<>();
        messages.add(message);
        when(messageRepository.findByGroupId("1")).thenReturn(messages);

        List<Message> foundMessages = messageService.getMessagesByGroupId("1");

        assertThat(foundMessages).hasSize(1);
        assertThat(foundMessages.get(0).getText()).isEqualTo("Test Message");
        verify(messageRepository).findByGroupId("1");
    }

    @Test
    public void testDeleteMessage() {
        doNothing().when(messageRepository).deleteById("1");

        messageService.deleteMessage("1");

        verify(messageRepository).deleteById("1");
    }
}
