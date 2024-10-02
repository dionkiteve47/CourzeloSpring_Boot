package com.courzelo.lms;

import tn.esprit.user.entities.schedule.ElementModule;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class LmsApplicationTests {

    @Test
    void contextLoads() {
        assertNull(null);
    }
    @Test
        //@Order(1)
    void testStringEmpty() {
        ElementModule elementModule = new ElementModule();
        elementModule.setName(null); // Set the name to null for testing
       assertEquals(null, elementModule.getName(), "The name is null");
    }

}
