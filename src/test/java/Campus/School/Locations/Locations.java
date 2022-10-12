package Campus.School.Locations;

import Campus.School.Locations.Model.Country;
import io.restassured.http.ContentType;
import io.restassured.http.Cookies;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class Locations {

    Cookies cookies;

    @Test
    public void Login() {
        baseURI = "https://demo.mersys.io/";

        Map<String, String> account = new HashMap<>();
        account.put("username", "richfield.edu");
        account.put("password", "Richfield2020!");
        account.put("rememberMe", "true");


        cookies =
                given()
                        .contentType(ContentType.JSON)
                        .body(account)

                        .when()
                        .post("auth/login")


                        .then()
                        .statusCode(200)
                        .extract().response().getDetailedCookies()
        ;
    }

    public String getRandomName() {
        return RandomStringUtils.randomAlphabetic(8).toLowerCase();
    }

    public String getRandomCode() {
        return RandomStringUtils.randomAlphabetic(3).toLowerCase();
    }

    public String getRandomCapacity() {
        return RandomStringUtils.randomNumeric(4);
    }

    String countryName;
    String countryCode;
    String countryID;
    String countryCapacity;
    String type="CLASS";

    public Map<String,String> school(){
        Map<String, String> school = new HashMap<>();
        school.put("id","5fe07e4fb064ca29931236a5");

        return school;
    }


    @Test(dependsOnMethods = "Login")
    public void createCountry() {

        countryName = getRandomName();
        countryCode = getRandomCode();
        countryCapacity = getRandomCapacity();

        Country country = new Country();
        country.setName(countryName);
        country.setCode(countryCode);
        country.setCapacity(countryCapacity);
        country.setSchool(school());
        country.setType(type);


        countryID =
                given()

                        .cookies(cookies)
                        .contentType(ContentType.JSON)
                        .body(country)
                        .when()
                        .post("school-service/api/location")

                        .then()
                        .log().body()
                        .statusCode(201)
                        .extract().jsonPath().getString("id")
        ;
    }

    @Test(dependsOnMethods = "createCountry")
    public void createCountryNegative() {

        Country country = new Country();
        country.setName(countryName);
        country.setCode(countryCode);
        country.setCapacity(countryCapacity);
        country.setSchool(school());
        country.setType(type);


        given()
                .cookies(cookies)
                .contentType(ContentType.JSON)
                .body(country)

                .when()
                .post("school-service/api/location")

                .then()
                .log().body()
                .statusCode(400)
        ;
    }

    @Test(dependsOnMethods = "createCountryNegative")
    public void updateCountry() {

        countryName = getRandomName();
        Country country = new Country();
        country.setId(countryID);
        country.setName(countryName);
        country.setCode(countryCode);
        country.setCapacity(countryCapacity);
        country.setSchool(school());
        country.setType(type);

        given()

                .cookies(cookies)
                .contentType(ContentType.JSON)
                .body(country)

                .when()
                .put("school-service/api/location")

                .then()
                .statusCode(200)
                .body("name", equalTo(countryName))
        ;
    }

    @Test(dependsOnMethods = "updateCountry")
    public void deleteCountry() {

        given()
                .cookies(cookies)
                .pathParam("countryID", countryID)


                .when()
                .delete("school-service/api/location/{countryID}")

                .then()
                .statusCode(200)
        ;
    }

    @Test(dependsOnMethods = "deleteCountry")
    public void deleteCountryNegative() {

        given()
                .cookies(cookies)
                .pathParam("countryID", countryID)

                .when()
                .delete("school-service/api/location/{countryID}")

                .then()
                .statusCode(400)
        ;
    }

    @Test(dependsOnMethods = "deleteCountryNegative")
    public void updateCountryNegative() {

        countryName = getRandomName();
        Country country = new Country();
        country.setId(countryID);
        country.setName(countryName);
        country.setCode(countryCode);
        country.setCapacity(countryCapacity);
        country.setSchool(school());
        country.setType(type);


        given()
                .cookies(cookies)
                .contentType(ContentType.JSON)
                .body(country)

                .when()
                .put("school-service/api/location")

                .then()
                .statusCode(400)
        ;
    }
}
