package com.gitblit.reactor.conf;

import fathom.test.FathomIntegrationTest;
import org.junit.Test;

import static com.jayway.restassured.RestAssured.get;

/**
 * Tests that our index page is generated.
 * Each unit test starts an instance of our Fathom app in TEST mode.
 */
public class RoutesTest extends FathomIntegrationTest {

    @Test
    public void testIndex() {

        get("/").then().assertThat().statusCode(200);

    }

}