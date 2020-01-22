import au.com.dius.pact.consumer.Pact;
import au.com.dius.pact.consumer.PactProviderRuleMk2;
import au.com.dius.pact.consumer.PactVerification;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.model.RequestResponsePact;
import io.pactfoundation.consumer.dsl.LambdaDsl;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE,
properties = "user-service.base-url:http://localhost:8080",
classes = UserServiceClient.class)
public class UserServiceContractTest {

    @Rule
    public PactProviderRuleMk2 provider = new PactProviderRuleMk2("Provider", null, 8080, this);

    @Autowired
    private UserServiceClient userServiceClient;

    @PactVerification(fragment = "pactUserExists")
    @Test
    public void userExists() {
        User user = userServiceClient.getUser("1");
        assertEquals(user.getName(), "user name for CDC");
    }

    @Pact(consumer = "Consumer")
    public RequestResponsePact pactUserExists(PactDslWithProvider builder) {
        return builder.given("User 1 exists")
                .uponReceiving("A request to /users/1")
                .path("/users/1")
                .method("GET")
                .willRespondWith()
                .status(200)
                .body(LambdaDsl.newJsonBody(o -> o
                    .stringType("name", "Pepe")
                    ).build())
                .toPact();
    }

}
