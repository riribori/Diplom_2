import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.example.CreateUser;
import org.example.DeleteUser;
import org.example.Steps;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.hamcrest.Matchers.is;

public class TestCreateUser {

    private Steps steps;

    private String token;
    private String email;
    private String name;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
        steps = new Steps();

    }

    @Test
    @DisplayName("Сreate New User")
    @Description("Basic test for api/auth/register endpoint")
    public void createNewUserTest() {
        CreateUser createUser = new CreateUser(RandomStringUtils.randomAlphabetic(10), RandomStringUtils.randomAlphabetic(10) + "@mail.ru" , RandomStringUtils.randomAlphabetic(6));
        Response response = steps.createNewUser(createUser);
                 response.then()
                .assertThat().body("success", is(true))
                .and()
                .statusCode(200);// тест на код
        token = response.jsonPath().getString("accessToken");
        email = createUser.getEmail();
        name = createUser.getName();

    }

    @Test
    @DisplayName("Сreate New User without name")
    @Description("Test for api/auth/register endpoint without name")
    public void createNewUserWithoutNameTest() {
        CreateUser createUser = new CreateUser(null, RandomStringUtils.randomAlphabetic(10) + "@mail.ru", RandomStringUtils.randomAlphabetic(6));
        steps.createNewUser(createUser).then().assertThat().body("message", is("Email, password and name are required fields"))
                .and()
                .statusCode(403);
    }

    @Test
    @DisplayName("Сreate Double User")
    @Description("Test for api/auth/register endpoint with double params")
    public void createDoubleUserTest() {
        CreateUser createUser = new CreateUser(RandomStringUtils.randomAlphabetic(10), RandomStringUtils.randomAlphabetic(10) + "@mail.ru", RandomStringUtils.randomAlphabetic(6));
        steps.createNewUser(createUser);
        steps.createNewUser(createUser).then().assertThat().body("message", is("User already exists"))
                .and()
                .statusCode(403);
    }

   @After
   public void deleteUser() {
     if (name != null && email != null) {
       steps.deleteUser(new DeleteUser(email, name), token).then().statusCode(202);
       }
   }
}
