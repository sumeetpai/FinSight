package com.FinSight_Backend.controller;

import com.FinSight_Backend.dto.UserDTO;
import com.FinSight_Backend.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/user")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    // -------------------- CREATE USER --------------------
    @PostMapping
    public ResponseEntity<UserDTO> addUser(@RequestBody UserDTO userDTO) {
        if (userDTO == null || userDTO.getUsername() == null || userDTO.getUsername().isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        UserDTO savedUser = userService.addUser(userDTO);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }

    // -------------------- GET USER BY ID --------------------
    @GetMapping("{user_id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable Integer user_id) {
        UserDTO userDTO = userService.getUser(user_id);
        if (userDTO == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(userDTO, HttpStatus.OK);
    }

    // -------------------- UPDATE USER --------------------
    @PutMapping("{user_id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Integer user_id,
                                              @RequestBody UserDTO userDTO) {
        UserDTO updatedUser = userService.updateUser(user_id, userDTO);
        if (updatedUser == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    // -------------------- DELETE USER --------------------
    @DeleteMapping("{user_id}")
    public ResponseEntity<String> deleteUser(@PathVariable Integer user_id) {
        String msg = userService.deleteUser(user_id);
        if (msg == null) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(msg, HttpStatus.OK);
    }
}
