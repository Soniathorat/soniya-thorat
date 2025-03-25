package TestPack;

import org.testng.annotations.BeforeClass;
import io.restassured.RestAssured;

public class TryTest {
	
	@BeforeClass
	public void Setup() {
		RestAssured.baseURI = "https://petstore3.swagger.io/api/v3/";
	}
	
}
