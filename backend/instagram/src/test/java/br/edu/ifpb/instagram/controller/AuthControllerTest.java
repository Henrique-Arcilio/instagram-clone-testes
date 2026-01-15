package br.edu.ifpb.instagram.controller;


import br.edu.ifpb.instagram.model.dto.UserDto;
import br.edu.ifpb.instagram.model.request.LoginRequest;
import br.edu.ifpb.instagram.model.request.UserDetailsRequest;
import br.edu.ifpb.instagram.service.UserService;
import br.edu.ifpb.instagram.service.impl.AuthServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;

    @MockitoBean
    private AuthServiceImpl authService;
    @MockitoBean
    private UserService userService;

    @Test
    void signIn_ShouldReturnToken() throws Exception {
        LoginRequest request = new LoginRequest("yasmin", "senha123");
        String fakeToken = "token-falso";

        when(authService.authenticate(any(LoginRequest.class))).thenReturn(fakeToken);

        mockMvc.perform(post("/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("yasmin"))
                .andExpect(jsonPath("$.token").value(fakeToken));
    }

    @Test
    void signUp_ShouldReturnCreatedUser() throws Exception {
        UserDetailsRequest request = new UserDetailsRequest(null, "yasmin@test.com", "senha123", "Yasmin Sarinho", "yasmin123");
        UserDto createdDto = new UserDto(1L, "Yasmin Sarinho", "yasmin123", "yasmin@test.com", null, null);

        when(userService.createUser(any(UserDto.class))).thenReturn(createdDto);

        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.username").value("yasmin123"));
    }

}
