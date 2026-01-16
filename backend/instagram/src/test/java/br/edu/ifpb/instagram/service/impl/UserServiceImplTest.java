package br.edu.ifpb.instagram.service.impl;

import br.edu.ifpb.instagram.exception.FieldAlreadyExistsException;
import br.edu.ifpb.instagram.model.dto.UserDto;
import br.edu.ifpb.instagram.model.entity.UserEntity;
import br.edu.ifpb.instagram.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
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
    void testUpdateUser_ThrowsExceptionWhenUserNotFound(){
        UserDto userDto = new UserDto(
                999L,
                "Updated User da Silva",
                "UpdatedUser",
                "updateduser@gmail.com",
                "updatedpassword",
                null);

        when(userRepository.findById(userDto.id())).thenReturn(Optional.empty());
        Exception exception = assertThrows(RuntimeException.class, () -> userService.updateUser(userDto));
        assertEquals("User not found with id: " + userDto.id(), exception.getMessage());
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

        Exception exception1 = assertThrows(IllegalArgumentException.class, () -> userService.updateUser(null));
        Exception exception2 = assertThrows(IllegalArgumentException.class, () -> userService.updateUser(userDto));

        assertEquals("UserDto or UserDto.id must not be null", exception1.getMessage());
        assertEquals("UserDto or UserDto.id must not be null", exception2.getMessage());
    }

    @Test
    void testUpdateUser_ThrowsExceptionWhenUserDtoIDisNull(){
        UserDto userDto = new UserDto(
                null,
                "Updated User da Silva",
                "UpdatedUser",
                "updateduser@gmail.com",
                "updatedpassword",
                null);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> userService.updateUser(userDto));

        assertEquals("UserDto or UserDto.id must not be null", exception.getMessage());
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
        Exception exception = assertThrows(RuntimeException.class, () -> userService.deleteUser(userId));

        assertEquals("User not found with id: " + userId, exception.getMessage());
        verify(userRepository, never()).deleteById(userId);
    }


    @Test
    void testFindAll_RetrievesAllUsersSuccessfully(){
        UserEntity user1 = new UserEntity();
        user1.setId(1L);
        user1.setFullName("User One");
        user1.setUsername("userone");
        user1.setEmail("userone@gmail.com");

        UserEntity user2 = new UserEntity();
        user2.setId(2L);
        user2.setFullName("User Two");
        user2.setUsername("usertwo");
        user2.setEmail("usertwo@gmail.com");

        when(userRepository.findAll())
                .thenReturn(List.of(user1, user2));

        List<UserDto> resultList = userService.findAll();

        assertEquals(2, resultList.size());
        assertEquals("User One", resultList.get(0).fullName());
        assertEquals("User Two", resultList.get(1).fullName());

        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testCreateUser_Success() {
        UserDto dto = new UserDto(null, "Yadmiin batista sarinho", "yasmin123", "yas@min.com", "yasmin123", null);

        when(userRepository.existsByEmail(dto.email())).thenReturn(false);
        when(userRepository.existsByUsername(dto.username())).thenReturn(false);
        when(passwordEncoder.encode(dto.password())).thenReturn("senha_criptografada");

        UserEntity mockUserEntity = new UserEntity();
        mockUserEntity.setId(10L);
        mockUserEntity.setFullName(dto.fullName());
        mockUserEntity.setUsername(dto.username());
        mockUserEntity.setEmail(dto.email());

        when(userRepository.save(any(UserEntity.class))).thenReturn(mockUserEntity);

        UserDto result = userService.createUser(dto);

        assertNotNull(result.id());
        assertEquals("yasmin123", result.username());
        verify(passwordEncoder).encode("yasmin123");
        verify(userRepository).save(any(UserEntity.class));
    }

    @Test
    void testCreateUser_ThrowsExceptionWhenEmailAlreadyExists() {

        UserDto dto = new UserDto(null, "Yasmin Sarinho", "yasmin123", "duplicado@test.com", "senha123", null);

        when(userRepository.existsByEmail(dto.email())).thenReturn(true);

        FieldAlreadyExistsException exception = assertThrows(FieldAlreadyExistsException.class, () -> {userService.createUser(dto);});

        assertEquals("E-email already in use.", exception.getMessage());
        verify(userRepository, never()).save(any(UserEntity.class));
    }
}
