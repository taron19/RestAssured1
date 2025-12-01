import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.Matchers.*;

public class ApiTests {

    private static final UserJson USER = new UserJson("morpheus", "leader");
    private static final UserJson USER_PUT = new UserJson("Samson", "lead");
    private static final UserRegistration USER_PUT2 = new UserRegistration("eve.holt@reqres.in", "cityslicka");
    private static final int SUCCESS_CODE = 200;
    private static final int SUCCESS_CODE_CREATION = 201;
    private static final int DATA_ABSENCE = 204;
    private static final int ERROR_CODE = 401;
    private static final String URL = "/api/users";
    private static final String URL_LOGIN = "/api/login";
    private static final String UNKNOWN = "/api/unknown/23";
    private static RequestSpecification requestSpecification;


    @BeforeAll
    static void setup() {
        RestAssured.baseURI = "https://reqres.in";
        requestSpecification = new RequestSpecBuilder().log(LogDetail.ALL)
                .addHeader("x-api-key", "reqres-free-v1")
                .setContentType(JSON)
                .build();
    }

    @Test
    void shouldReturnCorrectEmailOfFirstUser() {
        given()
                .spec(requestSpecification)
                .when()
                .get(URL)
                .then()
                .log().body()
                .statusCode(SUCCESS_CODE)
                .body("data[0].email", is("george.bluth@reqres.in"));

    }

    @Test
    void sizeOfArrayShouldBeCorrect() {
        given()
                .spec(requestSpecification)
                .when()
                .get(URL)
                .then()
                .log().body()
                .statusCode(SUCCESS_CODE)
                .body("data", hasSize(6));

    }

    @Test
    void shouldAddUser() {
        given()
                .spec(requestSpecification)
                .body(USER)
                .when()
                .post(URL)
                .then()
                .log().status()
                .statusCode(SUCCESS_CODE_CREATION)
                .body("id", notNullValue())
                .body("name", equalTo("morpheus"))
                .body("job", equalTo("leader"));

    }

    @Test
    void shouldDeleteUser() {

        given()
                .spec(requestSpecification)
                .when()
                .delete(URL + "/2")
                .then()
                .log().status()
                .statusCode(DATA_ABSENCE);

    }

    @Test
    void shouldUpdateUser() {

        given()
                .spec(requestSpecification)
                .body(USER_PUT)
                .when()
                .put(URL + "/2")
                .then()
                .log().status()
                .statusCode(SUCCESS_CODE)
                .body("name", equalTo("Samson"))
                .body("job", equalTo("lead"))
                .body("updatedAt", notNullValue());
    }


    @Test
    void shoudReturn400Status() {


        given()
                .spec(requestSpecification)
                .body("{\"email\": \"peter@klaven\"}")
                .when()
                .post(URL_LOGIN)
                .then()
                .statusCode(400)
                .body("error", equalTo("Missing password"));
    }

    @Test
    void shoudReturnEmpty404Status() {

        given()
                .spec(requestSpecification)
                .when()
                .get(UNKNOWN)
                .then()
                .statusCode(404)
                .body(equalTo("{}"));
    }


    @Test
    void shoudSuccessfullyLoginWith200Status() {

        given()
                .spec(requestSpecification)
                .body(USER_PUT2)
                .when()
                .post(URL_LOGIN)
                .then()
                .statusCode(SUCCESS_CODE)
                .body("token",equalTo("QpwL5tke4Pnpja7X4"));
    }


    @Test
    void shoudUnsuccessfullyUpdateWith401StatusUsingPatchRequest() {

        given()
                .spec(requestSpecification)
                .body(USER)
                .when()
                .patch(URL+"/2")
                .then()
                .statusCode(ERROR_CODE)
                .body("error",equalTo("api_key_required"))
                .body("message",equalTo("Create your API key at https://app.reqres.in to access the ReqRes API."))
                .body("signup_url",equalTo("https://app.reqres.in"));
    }



}
