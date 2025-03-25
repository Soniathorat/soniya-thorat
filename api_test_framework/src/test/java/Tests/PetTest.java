package Tests;

import org.testng.Assert;
import org.testng.annotations.Test;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import models.Category;
import models.Pet;
import models.Tag;
import TestPack.TryTest;
import static io.restassured.RestAssured.*;

import java.util.ArrayList;
import java.util.List;

public class PetTest extends TryTest {

    @Test
    public void CreatePet() {
        Category category = new Category(1, "Animal");
        Tag tag = new Tag(1, "Bird");
        List <Tag> tags = new ArrayList();
        tags.add(tag);
        Pet newPet = new Pet(5, "Mouse", "Available", category, tags);

        Response response = given()
                .contentType(ContentType.JSON)
                .body(newPet)
                .when().post("/pet")
                .then().statusCode(200).extract().response();

        System.out.println("Post Method Successful");
        System.out.println(response.asPrettyString());

        Assert.assertEquals(response.jsonPath().getInt("id"), newPet.getId(), "Pet id Mismatch");
        Assert.assertEquals(response.jsonPath().getString("name"), newPet.getName(), "Name mismatch");
        Assert.assertEquals(response.jsonPath().getString("status"), newPet.getStatus(), "Status Mismatch");
    }

    @Test(dependsOnMethods = "CreatePet")
    public void GetpetID() {
        int petId = 5;
        Response response = given()
                .when()
                .get("/pet/" + petId)
                .then().statusCode(200).extract().response();

        System.out.println(response.prettyPrint());
        System.out.println("Get Method Successful");

        Assert.assertNotEquals(response.jsonPath().getString("name"), "Pet name should not be null");
    }

    @Test(dependsOnMethods = "GetpetID")
    public void UpdatePet() {
        Category category = new Category(1, "Animal");
        Tag tag = new Tag(1, "Bird");
        List <Tag> tags = new ArrayList();
        tags.add(tag);
        Pet newPet = new Pet(5, "Horse", "Available", category, tags);

        Response response = given()
                .contentType(ContentType.JSON)
                .body(newPet)
                .when().put("/pet")
                .then().statusCode(200).extract().response();

        System.out.println("Update Method Successful");
        System.out.println(response.asPrettyString());

        Assert.assertEquals(response.jsonPath().getInt("id"), newPet.getId(), "Pet id Mismatch");
        Assert.assertEquals(response.jsonPath().getString("name"), newPet.getName(), "Name mismatch");
        Assert.assertEquals(response.jsonPath().getString("status"), newPet.getStatus(), "Status Mismatch");
    }

    @Test(dependsOnMethods = "UpdatePet")
    public void DeletepetID() {
        int petId = 5;
        Response response = given()
                .when()
                .delete("/pet/" + petId)
                .then().statusCode(200).extract().response();

        System.out.println(response.prettyPrint());
        System.out.println("Delete Method Successful");
    }
}
