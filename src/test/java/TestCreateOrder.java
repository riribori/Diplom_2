import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.example.CreateOrder;
import org.example.CreateUser;
import org.example.Steps;
import org.example.UpdateUser;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.hamcrest.Matchers.is;

public class TestCreateOrder {

    private static Steps steps;
    private static String token;
    private static CreateUser createUser;
    private static String email;
    private static String name;

    private static String ingredient2;
    private static String ingredient1;


    @BeforeClass
    public static void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
        steps = new Steps();
        createUser = new CreateUser(RandomStringUtils.randomAlphabetic(10), RandomStringUtils.randomAlphabetic(10) + "@mail.ru", RandomStringUtils.randomAlphabetic(6));
        Response response = steps.createNewUser(createUser);
        email = createUser.getEmail();
        name = createUser.getName();
        token = response.jsonPath().getString("accessToken");
        response = steps.getIngredients();
        ingredient1 = response.jsonPath().getString("data[0]._id");
        ingredient2 = response.jsonPath().getString("data[1]._id");
    }

    @Test
    @DisplayName("Сreate Order")
    @Description("Basic test for api/order endpoint")
    public void createOrderTest() {
        CreateOrder createOrder = new CreateOrder(new String[]{ingredient1,ingredient2});
        Response response = steps.createOrder(createOrder, token);
        response.then()
                .assertThat().body("success", is(true))
                .body("order.ingredients[0]._id", is(ingredient1))
                .body("order.ingredients[1]._id", is(ingredient2))
                .and()
                .statusCode(200);
    }

    @Test
    @DisplayName("Сreate Order without token")
    @Description("Test for api/order endpoint endpoint without token")
    // данный тест специально падает,так как найдет баг, можно создать заказ без авторизации
    //хотя в доке указано, что без авторизации сделать это нельзя!!!!!!!!!1
    public void CreateOrderWithoutTokenTest() {
        CreateOrder createOrder = new CreateOrder(new String[]{ingredient1,ingredient2});
        Response response = steps.createOrder(createOrder );
        response.then()
                .assertThat().body("success", is(false))
                .body("message", is("You should be authorised"))
                .and()
                .statusCode(401);
    }

    @Test
    @DisplayName("Сreate Order without ingredients")
    @Description("Test order without ingredients for api/order endpoint ")
    public void createOrderWithoutIngredientsTest() {
        CreateOrder createOrder = new CreateOrder(new String[]{});
        Response response = steps.createOrder(createOrder, token);
        response.then()
                .assertThat().body("success", is(false))
                .body("message", is("Ingredient ids must be provided"))
                .and()
                .statusCode(400);
    }

    @Test
    @DisplayName("Сreate Order with invalid ingredient")
    @Description("Test order with invaled ingredient for api/order endpoint ")
    public void createOrderWithInvalidIngredientTest() {
        String ingredientInvalid = RandomStringUtils.randomAlphabetic(10);
        CreateOrder createOrder = new CreateOrder(new String[]{ingredientInvalid});
        Response response = steps.createOrder(createOrder, token);
        response.then()
                .assertThat()
                .statusCode(500);
    }

}
