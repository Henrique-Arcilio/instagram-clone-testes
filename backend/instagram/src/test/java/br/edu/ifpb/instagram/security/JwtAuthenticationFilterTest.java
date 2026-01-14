package br.edu.ifpb.instagram.security;

import br.edu.ifpb.instagram.service.impl.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JwtAuthenticationFilterTest {
    @Mock
    JwtUtils jwtUtils;
    @Mock
    UserDetailsServiceImpl userDetailsService;
    @Mock
    HttpServletRequest req;
    @Mock
    HttpServletResponse res;
    @Mock
    FilterChain chain;
    @InjectMocks
    JwtAuthenticationFilter filter;

    @BeforeEach
    void setup() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void testDoFilter_SemHeader_DeveSeguirAFila() throws Exception {

        when(req.getHeader("Authorization")).thenReturn(null);

        filter.doFilterInternal(req, res, chain);

        verify(chain, times(1)).doFilter(req, res);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}
