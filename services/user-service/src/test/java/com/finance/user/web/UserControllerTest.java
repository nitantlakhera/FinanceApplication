package com.finance.user.web;

import com.finance.user.domain.User;
import com.finance.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createUser_returnsCreatedUser() throws Exception {
        UUID id = UUID.randomUUID();
        User user = new User("alice@example.com", "Alice", "Smith");
        user.setId(id);

        when(userService.createUser(any(), any(), any())).thenReturn(user);

        UserController.CreateUserRequest request = new UserController.CreateUserRequest(
                "alice@example.com", "Alice", "Smith");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.email").value("alice@example.com"));
    }

    @Test
    void getUser_returnsUser() throws Exception {
        UUID id = UUID.randomUUID();
        User user = new User("bob@example.com", "Bob", "Jones");
        user.setId(id);

        when(userService.getUser(id)).thenReturn(Optional.of(user));

        mockMvc.perform(get("/users/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("bob@example.com"));
    }

    @Test
    void getUser_notFound() throws Exception {
        UUID id = UUID.randomUUID();
        when(userService.getUser(id)).thenReturn(Optional.empty());

        mockMvc.perform(get("/users/{id}", id))
                .andExpect(status().isNotFound());
    }
}
