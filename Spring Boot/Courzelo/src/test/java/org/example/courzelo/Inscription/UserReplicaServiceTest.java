package org.example.courzelo.Inscription;

import org.example.courzelo.models.Inscriptions.UserReplica;
import org.example.courzelo.models.institution.Institution;
import org.example.courzelo.repositories.Inscriptions.UserReplicaReposity;
import org.example.courzelo.services.Inscriptions.UserReplicaService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class UserReplicaServiceTest {

    @Mock
    private UserReplicaReposity userReplicaRepository;

    @InjectMocks
    private UserReplicaService userReplicaService;

    public UserReplicaServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testFindAllByInstitution() {
        Institution institution = new Institution();
        institution.setId("institutionId");

        UserReplica userReplica1 = new UserReplica();
        userReplica1.setEmail("test1@example.com");

        UserReplica userReplica2 = new UserReplica();
        userReplica2.setEmail("test2@example.com");

        when(userReplicaRepository.findUserReplicaByInstitution(institution))
                .thenReturn(List.of(userReplica1, userReplica2));

        List<UserReplica> results = userReplicaService.getReplicaByInstitution(institution.getId());

        assertNotNull(results);
        assertEquals(2, results.size());
        assertEquals("test1@example.com", results.get(0).getEmail());
        assertEquals("test2@example.com", results.get(1).getEmail());
    }

    @Test
    public void testSaveUserReplica() {
        UserReplica userReplica = new UserReplica();
        userReplica.setEmail("test@example.com");

        when(userReplicaRepository.save(any(UserReplica.class))).thenReturn(userReplica);

        UserReplica savedUserReplica = userReplicaService.addReplica(userReplica);

        assertNotNull(savedUserReplica);
        assertEquals("test@example.com", savedUserReplica.getEmail());
    }

    @Test
    void loadUserByUsername() {
    }

    @Test
    void loadUserByEmail() {
    }

    @Test
    void validUser() {
    }

    @Test
    void updateUserProfile() {
    }

    @Test
    void uploadProfileImage() {
    }

    @Test
    void getProfileImage() {
    }

    @Test
    void getUserProfile() {
    }

    @Test
    void updatePassword() {
    }

    @Test
    void resetPassword() {
    }

    @Test
    void generateTwoFactorAuthQrCode() {
    }

    @Test
    void generateQRCodeImage() {
    }

    @Test
    void enableTwoFactorAuth() {
    }

    @Test
    void disableTwoFactorAuth() {
    }

    @Test
    void verifyTwoFactorAuth() {
    }

    @Test
    void getUserProfileByEmail() {
    }

    @Test
    void addSkill() {
    }

    @Test
    void getSkills() {
    }

    @Test
    void removeSkill() {
    }

    @Test
    void getProfById() {
    }
}
