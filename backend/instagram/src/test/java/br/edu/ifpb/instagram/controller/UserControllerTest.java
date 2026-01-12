package br.edu.ifpb.instagram.controller;

import br.edu.ifpb.instagram.model.request.UserDetailsRequest;
import br.edu.ifpb.instagram.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
@Transactional
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Test
    void getUserById_ShouldReturnUserDetailsResponse() throws Exception {

        mockMvc.perform(get("/users/{id}", 1L))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
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
        mockMvc.perform(get("/users"))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.size()").value(3))
                .andExpect(jsonPath("$[*].username", containsInAnyOrder("testuser1", "testuser2", "testuser3")));
    }


    @Test
    void updateUser_ShouldReturnUserDetailsResponseWithUpdatedUserInfo() throws Exception {
        UserDetailsRequest request = new UserDetailsRequest(
                1L,
                "updatedUser@gmail.com",
                "updatedPassword",
                "Updated User",
                "updateUser");

        mockMvc.perform(put("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.fullName").value("Updated User"))
                .andExpect(jsonPath("$.username").value("updateUser"))
                .andExpect(jsonPath("$.email").value("updatedUser@gmail.com"));
    }

    @Test
    void updateUser_ThrowsExceptionWhenNullDataDTO() throws Exception {
        UserDetailsRequest request = new UserDetailsRequest(
                null,
                "updatedUser@gmail.com",
                "updatedPassword",
                "Updated User",
                "updateUser");

        ServletException nullIdException = assertThrows(ServletException.class, () -> mockMvc.perform(put("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request)))
                .andReturn());

        assertTrue(nullIdException.getMessage().contains("UserDto or UserDto.id must not be null"));
    }

    @Test
    void deleteUser_ShouldDeleteUserFromDataBase() throws Exception {
        Long userId = 1L;
        mockMvc.perform(delete("/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(content().string("user was deleted!"));
    }

    @Test
    void deleteUser_ThrowsExceptionWhenUserNotFound() throws Exception {
        Long userId = 999L;
        ServletException ex = assertThrows(ServletException.class,
                () -> mockMvc.perform(delete("/users/{id}", userId))
                .andReturn());
        assertTrue(ex.getMessage().contains("User not found with id: " + userId));


    }
}
