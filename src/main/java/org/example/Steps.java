package org.example;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class Steps {

    @Step("Create User")
    public Response createNewUser (CreateUser createUser){
        Response response =
                given()
                        .header("Content-type", "application/json")
                        .and()
                        .body(createUser)
                        .when()
                        .post("api/auth/register");
        return response;

    }

    @Step("Delete User")
    public Response deleteUser (DeleteUser deleteUser, String token){
        Response response =
                given()
                        .header("Content-type", "application/json")
                        .header("Authorization", token)
                        .and()
                        .body(deleteUser)
                        .when()
                        .delete("api/auth/user");
        return response;

    }

    @Step ("Login User")
    public Response loginUser (LoginUser loginUser){
        Response response =
                given()
                        .header("Content-type", "application/json")
                        .and()
                        .body(loginUser)
                        .when()
                        .post("api/auth/login");
        return response;
    }

    @Step("Update User")
    public  Response updateUser (UpdateUser updateUser, String token) {
        RequestSpecification requestSpecification = given()
                .header("Content-type", "application/json");
        if (token != null) {
            requestSpecification.header("Authorization", token);
        }
        Response response =
                requestSpecification
                .and()
                .body(updateUser)
                .when()
                .patch("api/auth/user");
        return response;
    }

    @Step("Update User without Token")
    public  Response updateUser (UpdateUser updateUser) {
        return updateUser(updateUser, null);
    }


    @Step("Get ingredients")
    public  Response getIngredients (){
        Response response =
                given()
                        .header("Content-type", "application/json")
                        .when()
                        .get("api/ingredients");
        return response;
    }

    @Step("Create order")
    public  Response createOrder (CreateOrder createOrder, String token){
        RequestSpecification requestSpecification = given()
                .header("Content-type", "application/json");
        if (token != null) {
            requestSpecification.header("Authorization", token);
        }
        Response response =
                requestSpecification
                        .and()
                        .body(createOrder)
                        .when()
                        .post("api/orders");
        return response;
    }

    @Step("Create order without Token")
    public  Response createOrder (CreateOrder createOrder) {
        return createOrder(createOrder, null);
    }
}

