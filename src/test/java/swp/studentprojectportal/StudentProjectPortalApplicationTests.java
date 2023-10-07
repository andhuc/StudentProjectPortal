package swp.studentprojectportal;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import swp.studentprojectportal.utils.Validate;

@SpringBootTest
class StudentProjectPortalApplicationTests {

    @Test
    void contextLoads() {
        boolean rs = Validate.validEmail("abc@abc@");
        boolean expected = false;
        Assertions.assertEquals(expected,rs);
    }

}
