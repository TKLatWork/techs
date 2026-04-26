import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

/**
 * Client Connection Management Test Cases
 */
public class ClientConnectionTest {

    private ClientConnectionAPIImpl api;

    @BeforeEach
    void setUp() {
        api = new ClientConnectionAPIImpl();
    }

    @Test
    @DisplayName("Test 1: Create a new connection successfully")
    void testCreateConnectionSuccess() {
        // Arrange
        ClientConnectionModels.Connect connectRequest = new ClientConnectionModels.Connect();
        connectRequest.setTargetEnv("production");
        connectRequest.setExpiryMins(30);
        connectRequest.setOwner("john.doe");

        // Act
        var response = api.connect(connectRequest);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().isSuccess());
        assertNotNull(response.getBody().getData());
        assertNotNull(response.getBody().getData().getToken());
        assertEquals("production", response.getBody().getData().getTargetEnv());
        assertEquals("john.doe", response.getBody().getData().getOwner());
        assertEquals("ACTIVE", response.getBody().getData().getStatus());
    }

    @Test
    @DisplayName("Test 2: Create connection with missing targetEnv should fail")
    void testCreateConnectionMissingTargetEnv() {
        // Arrange
        ClientConnectionModels.Connect connectRequest = new ClientConnectionModels.Connect();
        connectRequest.setTargetEnv("");
        connectRequest.setExpiryMins(30);
        connectRequest.setOwner("john.doe");

        // Act
        var response = api.connect(connectRequest);

        // Assert
        assertEquals(400, response.getStatusCodeValue());
        assertFalse(response.getBody().isSuccess());
        assertTrue(response.getBody().getMessage().contains("targetEnv"));
    }

    @Test
    @DisplayName("Test 3: Create connection with invalid expiryMins should fail")
    void testCreateConnectionInvalidExpiryMins() {
        // Arrange
        ClientConnectionModels.Connect connectRequest = new ClientConnectionModels.Connect();
        connectRequest.setTargetEnv("production");
        connectRequest.setExpiryMins(0);
        connectRequest.setOwner("john.doe");

        // Act
        var response = api.connect(connectRequest);

        // Assert
        assertEquals(400, response.getStatusCodeValue());
        assertFalse(response.getBody().isSuccess());
    }

    @Test
    @DisplayName("Test 4: Create connection with missing owner should fail")
    void testCreateConnectionMissingOwner() {
        // Arrange
        ClientConnectionModels.Connect connectRequest = new ClientConnectionModels.Connect();
        connectRequest.setTargetEnv("production");
        connectRequest.setExpiryMins(30);
        connectRequest.setOwner("");

        // Act
        var response = api.connect(connectRequest);

        // Assert
        assertEquals(400, response.getStatusCodeValue());
        assertFalse(response.getBody().isSuccess());
    }

    @Test
    @DisplayName("Test 5: Create connection when targetEnv is already booked should fail")
    void testCreateConnectionDuplicateEnvironment() {
        // Arrange - Create first connection
        ClientConnectionModels.Connect connectRequest1 = new ClientConnectionModels.Connect();
        connectRequest1.setTargetEnv("staging");
        connectRequest1.setExpiryMins(30);
        connectRequest1.setOwner("user1");
        
        var response1 = api.connect(connectRequest1);
        assertEquals(200, response1.getStatusCodeValue());

        // Act - Try to create second connection with same environment
        ClientConnectionModels.Connect connectRequest2 = new ClientConnectionModels.Connect();
        connectRequest2.setTargetEnv("staging");
        connectRequest2.setExpiryMins(30);
        connectRequest2.setOwner("user2");
        
        var response2 = api.connect(connectRequest2);

        // Assert
        assertEquals(409, response2.getStatusCodeValue());
        assertFalse(response2.getBody().isSuccess());
        assertTrue(response2.getBody().getMessage().contains("already booked"));
    }

    @Test
    @DisplayName("Test 6: Submit request with valid token should succeed")
    void testSubmitRequestSuccess() {
        // Arrange - Create connection first
        ClientConnectionModels.Connect connectRequest = new ClientConnectionModels.Connect();
        connectRequest.setTargetEnv("development");
        connectRequest.setExpiryMins(60);
        connectRequest.setOwner("jane.smith");
        
        var connectResponse = api.connect(connectRequest);
        String token = connectResponse.getBody().getData().getToken();

        // Act - Submit request
        ClientConnectionModels.Request request = new ClientConnectionModels.Request();
        request.setToken(token);
        request.setDsl("SELECT * FROM users");
        
        var response = api.submitRequest(request);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().isSuccess());
    }

    @Test
    @DisplayName("Test 7: Submit request with invalid token should fail")
    void testSubmitRequestInvalidToken() {
        // Arrange
        ClientConnectionModels.Request request = new ClientConnectionModels.Request();
        request.setToken("invalid-token");
        request.setDsl("SELECT * FROM users");

        // Act
        var response = api.submitRequest(request);

        // Assert
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    @DisplayName("Test 8: Submit request with missing token should fail")
    void testSubmitRequestMissingToken() {
        // Arrange
        ClientConnectionModels.Request request = new ClientConnectionModels.Request();
        request.setToken("");
        request.setDsl("SELECT * FROM users");

        // Act
        var response = api.submitRequest(request);

        // Assert
        assertEquals(400, response.getStatusCodeValue());
        assertFalse(response.getBody().isSuccess());
    }

    @Test
    @DisplayName("Test 9: Submit request with missing DSL should fail")
    void testSubmitRequestMissingDSL() {
        // Arrange - Create connection first
        ClientConnectionModels.Connect connectRequest = new ClientConnectionModels.Connect();
        connectRequest.setTargetEnv("test");
        connectRequest.setExpiryMins(30);
        connectRequest.setOwner("tester");
        
        var connectResponse = api.connect(connectRequest);
        String token = connectResponse.getBody().getData().getToken();

        // Act - Submit request without DSL
        ClientConnectionModels.Request request = new ClientConnectionModels.Request();
        request.setToken(token);
        request.setDsl("");
        
        var response = api.submitRequest(request);

        // Assert
        assertEquals(400, response.getStatusCodeValue());
        assertFalse(response.getBody().isSuccess());
    }

    @Test
    @DisplayName("Test 10: Disconnect connection successfully")
    void testDisconnectSuccess() {
        // Arrange - Create connection first
        ClientConnectionModels.Connect connectRequest = new ClientConnectionModels.Connect();
        connectRequest.setTargetEnv("qa");
        connectRequest.setExpiryMins(30);
        connectRequest.setOwner("qa.user");
        
        var connectResponse = api.connect(connectRequest);
        String token = connectResponse.getBody().getData().getToken();

        // Act - Disconnect
        ClientConnectionModels.Disconnect disconnectRequest = new ClientConnectionModels.Disconnect();
        disconnectRequest.setToken(token);
        
        var response = api.disconnect(disconnectRequest);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().isSuccess());
    }

    @Test
    @DisplayName("Test 11: Disconnect with invalid token should fail")
    void testDisconnectInvalidToken() {
        // Arrange
        ClientConnectionModels.Disconnect disconnectRequest = new ClientConnectionModels.Disconnect();
        disconnectRequest.setToken("non-existent-token");

        // Act
        var response = api.disconnect(disconnectRequest);

        // Assert
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    @DisplayName("Test 12: Disconnect with missing token should fail")
    void testDisconnectMissingToken() {
        // Arrange
        ClientConnectionModels.Disconnect disconnectRequest = new ClientConnectionModels.Disconnect();
        disconnectRequest.setToken("");

        // Act
        var response = api.disconnect(disconnectRequest);

        // Assert
        assertEquals(400, response.getStatusCodeValue());
        assertFalse(response.getBody().isSuccess());
    }

    @Test
    @DisplayName("Test 13: After disconnect, submitting request should fail")
    void testRequestAfterDisconnect() {
        // Arrange - Create and disconnect connection
        ClientConnectionModels.Connect connectRequest = new ClientConnectionModels.Connect();
        connectRequest.setTargetEnv("uat");
        connectRequest.setExpiryMins(30);
        connectRequest.setOwner("uat.user");
        
        var connectResponse = api.connect(connectRequest);
        String token = connectResponse.getBody().getData().getToken();
        
        ClientConnectionModels.Disconnect disconnectRequest = new ClientConnectionModels.Disconnect();
        disconnectRequest.setToken(token);
        api.disconnect(disconnectRequest);

        // Act - Try to submit request
        ClientConnectionModels.Request request = new ClientConnectionModels.Request();
        request.setToken(token);
        request.setDsl("SELECT * FROM orders");
        
        var response = api.submitRequest(request);

        // Assert
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    @DisplayName("Test 14: After disconnect, environment should be available for new connection")
    void testEnvironmentAvailableAfterDisconnect() {
        // Arrange - Create and disconnect connection
        ClientConnectionModels.Connect connectRequest1 = new ClientConnectionModels.Connect();
        connectRequest1.setTargetEnv("integration");
        connectRequest1.setExpiryMins(30);
        connectRequest1.setOwner("user1");
        
        var connectResponse1 = api.connect(connectRequest1);
        String token1 = connectResponse1.getBody().getData().getToken();
        
        ClientConnectionModels.Disconnect disconnectRequest = new ClientConnectionModels.Disconnect();
        disconnectRequest.setToken(token1);
        api.disconnect(disconnectRequest);

        // Act - Create new connection with same environment
        ClientConnectionModels.Connect connectRequest2 = new ClientConnectionModels.Connect();
        connectRequest2.setTargetEnv("integration");
        connectRequest2.setExpiryMins(30);
        connectRequest2.setOwner("user2");
        
        var connectResponse2 = api.connect(connectRequest2);

        // Assert
        assertEquals(200, connectResponse2.getStatusCodeValue());
        assertTrue(connectResponse2.getBody().isSuccess());
    }

    @Test
    @DisplayName("Test 15: Get connection status for active connection")
    void testGetConnectionStatusActive() {
        // Arrange - Create connection
        ClientConnectionModels.Connect connectRequest = new ClientConnectionModels.Connect();
        connectRequest.setTargetEnv("prod-backup");
        connectRequest.setExpiryMins(120);
        connectRequest.setOwner("admin");
        
        var connectResponse = api.connect(connectRequest);
        String token = connectResponse.getBody().getData().getToken();

        // Act
        var response = api.getConnectionStatus(token);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().isSuccess());
        assertEquals("ACTIVE", response.getBody().getData().getStatus());
    }

    @Test
    @DisplayName("Test 16: Get connection status for non-existent connection")
    void testGetConnectionStatusNonExistent() {
        // Act
        var response = api.getConnectionStatus("fake-token");

        // Assert
        assertEquals(404, response.getStatusCodeValue());
    }
}
