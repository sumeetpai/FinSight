package com.FinSight_Backend.controller;

import com.FinSight_Backend.dto.UserCreateDTO;
import com.FinSight_Backend.dto.UserDTO;
import com.FinSight_Backend.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    void addUser_badRequest() throws Exception {
        UserCreateDTO dto = new UserCreateDTO();
        mockMvc.perform(post("/api/v1/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addUser_created() throws Exception {
        UserCreateDTO dto = new UserCreateDTO();
        dto.setUsername("demo");
        UserDTO saved = new UserDTO();
        saved.setUsername("demo");
        Mockito.when(userService.addUser(Mockito.any())).thenReturn(saved);

        mockMvc.perform(post("/api/v1/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    void getUser_notFound() throws Exception {
        Mockito.when(userService.getUser(1)).thenReturn(null);
        mockMvc.perform(get("/api/v1/user/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateUser_notFound() throws Exception {
        UserDTO dto = new UserDTO();
        Mockito.when(userService.updateUser(Mockito.eq(1), Mockito.any())).thenReturn(null);

        mockMvc.perform(put("/api/v1/user/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteUser_notFound() throws Exception {
        Mockito.when(userService.deleteUser(1)).thenReturn(null);
        mockMvc.perform(delete("/api/v1/user/1"))
                .andExpect(status().isNotFound());
    }
}
