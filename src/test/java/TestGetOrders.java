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
import java.util.List;
import static org.hamcrest.Matchers.is;

public class TestGetOrders {

    private static Steps steps;

    private static String token;

    private static CreateUser createUser;
    private static CreateOrder createOrder;

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
        createOrder = new CreateOrder(new String[]{ingredient1, ingredient2});
        for (int i = 0; i <= 51; i = i + 1) {
            steps.createOrder(createOrder, token);
        }

    }

    @Test
    @DisplayName("Get Order")
    @Description("Base test get order for api/order endpoint")
    /*Тест падает, так как находит баг: в документации написано, что по конкретному пользователю возвращается максимум 50 заказов
    но по факту тест выявляет, что их отдается больше :)
     */
    public void GerOrderTest() {
        Response response = steps.getOrders(token);
        response.then().log().all().assertThat().body("success", is(true))
                .and()
                .statusCode(200);
        List<Object> count = response.jsonPath().getList("orders");
        int countList = count.size();
        Assert.assertEquals("Вернуло не 50 заказов", 50, countList);
    }

    @Test
    @DisplayName("Get Order without token")
    @Description("Test for get order api/order endpoint without token")
    public void GerOrderWithOutTest() {
        Response response = steps.getOrder();
        response.then().log().all().assertThat()
                .body("success", is(false))
                .body("message", is("You should be authorised"))
                .and()
                .statusCode(401);
    }

    @AfterClass
    public static void deleteUser() {
        steps.deleteUser(new DeleteUser(email, name), token).then().statusCode(202);
    }
}
