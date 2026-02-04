package com.FinSight_Backend.controller;

import com.FinSight_Backend.dto.UserDTO;
import com.FinSight_Backend.dto.UserCreateDTO;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testAddUser_Success() throws Exception {
        UserCreateDTO createDTO = new UserCreateDTO();
        createDTO.setUsername("tamanna");

        UserDTO saved = new UserDTO();
        saved.setUser_id(1);

        Mockito.when(userService.addUser(Mockito.any()))
                .thenReturn(saved);

        mockMvc.perform(post("/api/v1/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated());
    }

    @Test
    void testGetUser_NotFound() throws Exception {
        Mockito.when(userService.getUser(1))
                .thenReturn(null);

        mockMvc.perform(get("/api/v1/user/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteUser_Success() throws Exception {
        Mockito.when(userService.deleteUser(1))
                .thenReturn("Deleted");

        mockMvc.perform(delete("/api/v1/user/1"))
                .andExpect(status().isOk());
    }
}
