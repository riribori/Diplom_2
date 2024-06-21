import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.example.CreateUser;
import org.example.DeleteUser;
import org.example.LoginUser;
import org.example.Steps;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.hamcrest.Matchers.is;


public class TestLoginUser {

    private static Steps steps;

    private static String token;

    private static CreateUser createUser;

    private static String email;
    private static String name;

    @BeforeClass
    public static void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
        steps = new Steps();
        createUser = new CreateUser(RandomStringUtils.randomAlphabetic(10), RandomStringUtils.randomAlphabetic(10) + "@mail.ru", RandomStringUtils.randomAlphabetic(6));
        steps.createNewUser(createUser);
        email = createUser.getEmail();
        name = createUser.getName();
    }

    @Test
    @DisplayName("Login User")
    @Description("Base for api/auth/login endpoint")
    public void loginUserTest() {
        LoginUser loginUser = new LoginUser(createUser.getEmail(), createUser.getPassword());
        Response response = steps.loginUser(loginUser);
        response.then().assertThat().body("success", is(true))
                .and()
                .statusCode(200);
        token = response.jsonPath().getString("accessToken");

    }

    @Test
    @DisplayName("Login User error")
    @Description("Error login user for api/auth/login endpoint")
    public void loginUserErrorTest() {
        LoginUser loginUser = new LoginUser(RandomStringUtils.randomAlphabetic(10) + "@mail.ru", RandomStringUtils.randomAlphabetic(6));
        Response response = steps.loginUser(loginUser);
        response.then().assertThat().body("success", is(false)).body("message", is("email or password are incorrect"))
                .and()
                .statusCode(401);
    }

    @AfterClass
    public static void deleteUser() {
            steps.deleteUser(new DeleteUser(email, name), token).then().statusCode(202);
    }
}
