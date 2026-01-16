package br.edu.ifpb.instagram.repository;

import br.edu.ifpb.instagram.model.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private UserEntity user;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        user = new UserEntity();
        user.setFullName("Yasmin Sarinho");
        user.setUsername("yasmin123");
        user.setEmail("yasmin@test.com");
        user.setEncryptedPassword("senha_hash");
        userRepository.save(user);
    }

    @Test
    void testExistsByEmail_ReturnsTrueWhenEmailExists() {
        boolean exists = userRepository.existsByEmail("yasmin@test.com");
        assertTrue(exists);
    }

    @Test
    void testExistsByUsername_ReturnsTrueWhenUsernameExists() {
        boolean exists = userRepository.existsByUsername("yasmin123");
        assertTrue(exists);
    }

    @Test
    void testFindByUsername_ReturnsUserEntity() {
        Optional<UserEntity> found = userRepository.findByUsername("yasmin123");
        assertTrue(found.isPresent());
        assertEquals("yasmin@test.com", found.get().getEmail());
    }

    @Test
    void testUpdatePartialUser_UpdatesOnlyProvidedFields() {

        int updatedRows = userRepository.updatePartialUser(
                "Yasmin Updated",
                null,
                null,
                null,
                user.getId()
        );

        assertEquals(1, updatedRows);

        UserEntity updatedUser = userRepository.findById(user.getId()).get();
        assertEquals("Yasmin Updated", updatedUser.getFullName());
        assertEquals("yasmin123", updatedUser.getUsername());
        assertEquals("yasmin@test.com", updatedUser.getEmail());
    }
}