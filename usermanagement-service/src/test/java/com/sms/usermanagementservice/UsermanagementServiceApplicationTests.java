package com.sms.usermanagementservice;

import com.sms.usermanagementservice.boundary.UsersService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class UsermanagementServiceApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void calculatePassword(){

        String firstName = "UserFirstName";
        String lastName = "LastName";

        UsersService usersService = new UsersService();

        Assertions.assertEquals(usersService.calculatePassword(firstName, lastName), "UserLast");

        firstName = "Us";
        lastName = "L";

        Assertions.assertEquals(usersService.calculatePassword(firstName, lastName), "UsL");
    }

}
