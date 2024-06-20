import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.example.*;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;


import static org.hamcrest.Matchers.is;

public class TestUpdateUser {

    private static Steps steps;
    private static String token;
    private static CreateUser createUser;
    private static String email;
    private static String name;
    private static String password;

    @BeforeClass
    public static void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
        steps = new Steps();
        createUser = new CreateUser(RandomStringUtils.randomAlphabetic(10), RandomStringUtils.randomAlphabetic(10) + "@mail.ru", RandomStringUtils.randomAlphabetic(6));
        Response response = steps.createNewUser(createUser);
        email = createUser.getEmail();
        name = createUser.getName();
        password = createUser.getPassword();
        token = response.jsonPath().getString("accessToken");
    }

    @Test
    @DisplayName("Update User")
    @Description("Base for api/auth/user endpoint")
    public void UpdateUserTest() {
        UpdateUser updateUser = new UpdateUser(RandomStringUtils.randomAlphabetic(10) + "@mail.ru", RandomStringUtils.randomAlphabetic(10), RandomStringUtils.randomAlphabetic(6));
        email = updateUser.getEmail();
        name = updateUser.getName();
        password = updateUser.getPassword();
        Response response = steps.updateUser(updateUser, token);
        response.then().assertThat().body("success", is(true))
                .body("user.email", is(email.toLowerCase()))
                .body("user.name", is(name))
                .and()
                .statusCode(200);
        LoginUser loginUser = new LoginUser(email, password);
        steps.loginUser(loginUser).then().assertThat().statusCode(200);
    }

    @Test
    @DisplayName("Update User with broken token")
    @Description("Test for api/auth/user endpoint with broken token")
    public void UpdateUserWithBrokenTokenTest() {
        UpdateUser updateUser = new UpdateUser(RandomStringUtils.randomAlphabetic(10), RandomStringUtils.randomAlphabetic(10) + "@mail.ru", RandomStringUtils.randomAlphabetic(6));
        Response response = steps.updateUser(updateUser, token + RandomStringUtils.randomAlphabetic(10));
        response.then().assertThat().body("success", is(false))
                .body("message", is("invalid signature"))
                .and()
                .statusCode(403);
    }

    @Test
    @DisplayName("Update User without token")
    @Description("Test for api/auth/user endpoint without token")
    public void UpdateUserWithoutTokenTest() {
        UpdateUser updateUser = new UpdateUser(RandomStringUtils.randomAlphabetic(10), RandomStringUtils.randomAlphabetic(10) + "@mail.ru", RandomStringUtils.randomAlphabetic(6));
        Response response = steps.updateUser(updateUser);
        response.then().assertThat().body("success", is(false))
                .body("message", is("You should be authorised"))
                .and()
                .statusCode(401);
    }

    @AfterClass
    public static void deleteUser() {
        steps.deleteUser(new DeleteUser(email, name), token).then().statusCode(202);
    }
}
