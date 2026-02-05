package com.FinSight_Backend.controller;

<<<<<<< HEAD
import com.FinSight_Backend.dto.UserCreateDTO;
import com.FinSight_Backend.dto.UserDTO;
=======
import com.FinSight_Backend.dto.UserDTO;
import com.FinSight_Backend.dto.UserCreateDTO;
>>>>>>> 323f0136fc4e141b74bb75a0c52ada2cc2f11153
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
<<<<<<< HEAD
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
=======
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
>>>>>>> 323f0136fc4e141b74bb75a0c52ada2cc2f11153

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

<<<<<<< HEAD
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
=======
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
>>>>>>> 323f0136fc4e141b74bb75a0c52ada2cc2f11153
                .andExpect(status().isCreated());
    }

    @Test
<<<<<<< HEAD
    void getUser_notFound() throws Exception {
        Mockito.when(userService.getUser(1)).thenReturn(null);
=======
    void testGetUser_NotFound() throws Exception {
        Mockito.when(userService.getUser(1))
                .thenReturn(null);

>>>>>>> 323f0136fc4e141b74bb75a0c52ada2cc2f11153
        mockMvc.perform(get("/api/v1/user/1"))
                .andExpect(status().isNotFound());
    }

    @Test
<<<<<<< HEAD
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
=======
    void testDeleteUser_Success() throws Exception {
        Mockito.when(userService.deleteUser(1))
                .thenReturn("Deleted");

        mockMvc.perform(delete("/api/v1/user/1"))
                .andExpect(status().isOk());
>>>>>>> 323f0136fc4e141b74bb75a0c52ada2cc2f11153
    }
}
