package com.FinSight_Backend.repository;

import com.FinSight_Backend.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
<<<<<<< HEAD
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.ANY)
@ActiveProfiles("test")
=======
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = Replace.ANY)
>>>>>>> 323f0136fc4e141b74bb75a0c52ada2cc2f11153
class UserRepoTest {

    @Autowired
    private UserRepo userRepo;

    @Test
<<<<<<< HEAD
    void saveAndFindById() {
        User user = new User();
        user.setUsername("demo");
        User saved = userRepo.save(user);

        assertTrue(userRepo.findById(saved.getUser_id()).isPresent());
=======
    @Rollback
    void testSaveAndFind() {
        User u = new User();
        u.setUsername("u1");
        u.setEmail("u1@example.com");
        u.setPassword("pass");

        User saved = userRepo.save(u);
        assertThat(saved.getUser_id()).isNotNull();

        User found = userRepo.findById(saved.getUser_id()).orElse(null);
        assertThat(found).isNotNull();
        assertThat(found.getUsername()).isEqualTo("u1");
>>>>>>> 323f0136fc4e141b74bb75a0c52ada2cc2f11153
    }
}
