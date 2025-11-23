import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.Header;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.Matchers.*;

public class Api {

    private static final UserJson USER = new UserJson("morpheus", "leader");
    private static final UserJson USER_PUT = new UserJson("Samson", "lead");
    private static final int SUCCESS_CODE = 200;
    private static final int SUCCESS_CODE_CREATION = 201;
    private static final int DATA_ABSENCE = 204;
    private static final String URL = "/api/users";
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
}
