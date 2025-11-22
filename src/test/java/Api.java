import io.restassured.RestAssured;
import io.restassured.http.Header;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.Matchers.*;

public class Api {

    private static final Header HEADER = new Header("x-api-key", "reqres-free-v1");
    private static final String BODY_ADD = "{ \"name\": \"morpheus\", \"job\": \"leader\" }";
    private static final String BODY_PUT = "{ \"name\": \"Samson\", \"job\": \"lead\" }";
    private static final int SUCCESS_CODE = 200;
    private static final int SUCCESS_CODE_CREATION = 201;
    private static final int DATA_ABSENCE = 204;


    @BeforeAll
    static void setup() {
        RestAssured.baseURI = "https://reqres.in";
    }

    @Test
    void shouldReturnCorrectEmailOfFirstUser() {
        given()
                .header(HEADER)
                .log().uri()
                .when()
                .get("/api/users")
                .then()
                .log().body()
                .statusCode(SUCCESS_CODE)
                .body("data[0].email", is("george.bluth@reqres.in"));

    }

    @Test
    void sizeOfArrayShouldBeCorrect() {
        given()
                .header(HEADER)
                .log().method()
                .when()
                .get("/api/users")
                .then()
                .log().body()
                .statusCode(SUCCESS_CODE)
                .body("data", hasSize(6));

    }

    @Test
    void shouldAddUser() {
        given()
                .header(HEADER)
                .body(BODY_ADD)
                .contentType(JSON)
                .log().body()
                .when()
                .post("/api/users")
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
                .header(HEADER)
                .log().uri()
                .when()
                .delete("/api/users/2")
                .then()
                .log().status()
                .statusCode(DATA_ABSENCE);

    }

    @Test
    void shouldUpdateUser() {

        given()
                .header(HEADER)
                .body(BODY_PUT)
                .contentType(JSON)
                .log().body()
                .when()
                .put("/api/users/2")
                .then()
                .log().status()
                .statusCode(SUCCESS_CODE)
                .body("name", equalTo("Samson"))
                .body("job", equalTo("lead"))
                .body("updatedAt", notNullValue());


    }
}
