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
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.assertNotNull;
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
    void testFilter_NoHeader_ShouldContinueChain() throws Exception {

        when(req.getHeader("Authorization")).thenReturn(null);

        filter.doFilterInternal(req, res, chain);

        verify(chain, times(1)).doFilter(req, res);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void testFilter_ValidToken_ShouldAuthenticate() throws Exception {
        String token = "Bearer token.valido.aqui";
        String jwt = "token.valido.aqui";
        String user = "yasmin";

        when(req.getHeader("Authorization")).thenReturn(token);
        when(jwtUtils.getUsernameFromToken(jwt)).thenReturn(user);
        when(jwtUtils.validateToken(jwt)).thenReturn(true);

        UserDetails mockDetails = mock(UserDetails.class);
        when(userDetailsService.loadUserByUsername(user)).thenReturn(mockDetails);

        filter.doFilterInternal(req, res, chain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        verify(chain).doFilter(req, res);
    }

    @Test
    void testDoFilter_TokenRuim_NaoDeveAutenticar() throws Exception {
        String token = "Bearer token.errado";
        String jwt = "token.errado";

        when(req.getHeader("Authorization")).thenReturn(token);
        when(jwtUtils.getUsernameFromToken(jwt)).thenReturn("user");
        when(jwtUtils.validateToken(jwt)).thenReturn(false);

        filter.doFilterInternal(req, res, chain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(chain).doFilter(req, res);
    }
}
