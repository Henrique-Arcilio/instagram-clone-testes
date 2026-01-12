package br.edu.ifpb.instagram.controller;

import br.edu.ifpb.instagram.repository.UserRepository;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Test
    void getUserById_ShouldReturnUserDetailsResponse() throws Exception {

        mockMvc.perform(get("/users/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.fullName").value("Test User1"))
                .andExpect(jsonPath("$.username").value("testuser1"))
                .andExpect(jsonPath("$.email").value("testuser1@gmail.com"));
    }

    @Test
    void getUserById_ThrowsExceptionWhenUserNotFound() {
        Long userID = 999L;
        ServletException ex = assertThrows(ServletException.class, () -> {
            mockMvc.perform(get("/users/{id}", userID))
                    .andReturn();
        });

        assertTrue(ex.getMessage().contains("User not found with id: " + userID));
    }

    @Test
    void getUsers_ShouldReturnAListOfUserDetailsResponse() throws Exception {
        mockMvc.perform(get("/users")).andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.size()").value(3))
                .andExpect(jsonPath("$[*].username", containsInAnyOrder("testuser1", "testuser2", "testuser3")));
    }

}
