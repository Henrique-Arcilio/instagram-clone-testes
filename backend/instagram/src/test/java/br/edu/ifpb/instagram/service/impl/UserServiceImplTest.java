package br.edu.ifpb.instagram.service.impl;

import br.edu.ifpb.instagram.model.dto.UserDto;
import br.edu.ifpb.instagram.model.entity.UserEntity;
import br.edu.ifpb.instagram.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    UserRepository userRepository; // Repositório simulado

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    UserServiceImpl userService; // Classe sob teste

    @Test
    void testFindById_ReturnsUserDto() {
        // Configurar o comportamento do mock
        Long userId = 1L;

        UserEntity mockUserEntity = new UserEntity();
        mockUserEntity.setId(userId);
        mockUserEntity.setFullName("Paulo Pereira");
        mockUserEntity.setEmail("paulo@ppereira.dev");

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUserEntity));

        // Executar o método a ser testado
        UserDto userDto = userService.findById(userId);

        // Verificar o resultado
        assertNotNull(userDto);
        assertEquals(mockUserEntity.getId(), userDto.id());
        assertEquals(mockUserEntity.getFullName(), userDto.fullName());
        assertEquals(mockUserEntity.getEmail(), userDto.email());

        // Verificar a interação com o mock
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testFindById_ThrowsExceptionWhenUserNotFound() {
        // Configurar o comportamento do mock
        Long userId = 999L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Executar e verificar a exceção
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.findById(userId);
        });

        assertEquals("User not found with id: " + userId, exception.getMessage());

        // Verificar a interação com o mock
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testUpdateUser_UpdateSuccessfully(){
        UserDto userDto =
                new UserDto(1L,
                        "Updated User da Silva",
                        "UpdatedUser",
                        "updateduser@gmail.com",
                        "updatedpassword",
                        null);

        UserEntity oldUserEntity = new UserEntity();
        oldUserEntity.setId(userDto.id());
        oldUserEntity.setUsername("OldUser");
        oldUserEntity.setFullName("Old User da Silva");
        oldUserEntity.setEmail("olduser@gmail.com");


        when(userRepository.findById(userDto.id())).thenReturn(Optional.of(oldUserEntity));
        when(userRepository.save(any(UserEntity.class)))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        UserDto userDtoResult = userService.updateUser(userDto);

        assertEquals(userDto.id(), userDtoResult.id());
        assertEquals(userDto.username(), userDtoResult.username());
        assertEquals(userDto.fullName(), userDtoResult.fullName());
        assertEquals(userDto.email(), userDtoResult.email());
        assertNull(userDtoResult.password());
        assertNull(userDtoResult.encryptedPassword());
    }
    @Test
    void testUpdateUser_ThrowsExceptionWhenUserIdNotFound(){
        UserDto userDto = new UserDto(
                999L,
                "Updated User da Silva",
                "UpdatedUser",
                "updateduser@gmail.com",
                "updatedpassword",
                null);

        when(userRepository.findById(userDto.id())).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> userService.updateUser(userDto));
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    void testUpdateUser_ThrowsExceptionWhenUserDtoIsNull(){
        UserDto userDto = new UserDto(
                null,
                "Updated User da Silva",
                "UpdatedUser",
                "updateduser@gmail.com",
                "updatedpassword",
                null);
        assertThrows(IllegalArgumentException.class, () -> userService.updateUser(null));
        assertThrows(IllegalArgumentException.class, () -> userService.updateUser(userDto));

    }

    @Test
    void testDeleteUser_DeleteUserSuccessfully(){
        Long userId = 1L;
        UserEntity returnedUser = new UserEntity();
        returnedUser.setId(userId);
        returnedUser.setFullName("User da Silva");
        returnedUser.setEmail("user@gmail.com");

        when(userRepository.existsById(userId)).thenReturn(true);
        userService.deleteUser(userId);
        verify(userRepository, times(1)).existsById(userId);
        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    void testDeleteUser_ThrowsExceptionWhenUserNotFound(){
        Long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(false);
        assertThrows(RuntimeException.class, () -> userService.deleteUser(userId));
        verify(userRepository, never()).deleteById(userId);
    }

}
