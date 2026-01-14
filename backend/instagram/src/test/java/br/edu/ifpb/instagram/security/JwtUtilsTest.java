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

}
