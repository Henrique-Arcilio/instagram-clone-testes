package br.edu.ifpb.instagram.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JwtUtilsTest {
    JwtUtils jwtUtils;

    @Mock
    Authentication auth;

    @BeforeEach
    void setUp(){
        jwtUtils = new JwtUtils();
    }

    @Test // Objetivo: Verificar se a classe consegue gerar tokens.
    void testGenerateToken_Success(){
        String username = "testuser";
        when(auth.getName()).thenReturn(username);

        String token = jwtUtils.generateToken(auth);

        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertEquals(3, token.split("\\.").length);

        verify(auth, times(1)).getName();
    }

    @Test // Objetivo: garantir que o token gerado é aceito como válido
    void testValidateToken_ValidTokenReturnsTrue(){
        when(auth.getName()).thenReturn("usuario_valido");
        String token = jwtUtils.generateToken(auth);

        boolean result = jwtUtils.validateToken(token);
        assertTrue(result);
    }

    @Test
    void testGetUsernameFromToken_ReturnsCorrectUsername(){
        String expectedUser = "yasmin123";
        when(auth.getName()).thenReturn(expectedUser);
        String token = jwtUtils.generateToken(auth);

        String resultUser = jwtUtils.getUsernameFromToken(token);
        assertEquals(expectedUser, resultUser);
    }

}
