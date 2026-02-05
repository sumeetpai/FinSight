package com.FinSight_Backend.repository;

import com.FinSight_Backend.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = Replace.ANY)
class UserRepoTest {

    @Autowired
    private UserRepo userRepo;

    @Test
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
    }
}
