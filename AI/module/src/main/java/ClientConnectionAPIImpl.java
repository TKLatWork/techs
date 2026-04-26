import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Client Connection Management API Implementation
 */
@RestController
@RequestMapping("/api/connections")
public class ClientConnectionAPIImpl implements ClientConnectionAPI {

    // In-memory storage for active connections (token -> connection details)
    private final Map<String, ClientConnectionModels.ConnectionResponse> activeConnections = new ConcurrentHashMap<>();
    
    // Track booked environments
    private final Map<String, String> bookedEnvironments = new ConcurrentHashMap<>();

    @Override
    public ResponseEntity<ClientConnectionModels.ApiResponse<ClientConnectionModels.ConnectionResponse>> connect(
            @RequestBody ClientConnectionModels.Connect connectRequest) {
        
        try {
            // Validate input
            if (StringUtils.isBlank(connectRequest.getTargetEnv())) {
                return ResponseEntity.badRequest().body(
                    new ClientConnectionModels.ApiResponse<>(false, "targetEnv is required", null)
                );
            }
            
            if (connectRequest.getExpiryMins() == null || connectRequest.getExpiryMins() < 1) {
                return ResponseEntity.badRequest().body(
                    new ClientConnectionModels.ApiResponse<>(false, "expiryMins must be at least 1 minute", null)
                );
            }
            
            if (StringUtils.isBlank(connectRequest.getOwner())) {
                return ResponseEntity.badRequest().body(
                    new ClientConnectionModels.ApiResponse<>(false, "owner is required", null)
                );
            }
            
            // Check if target environment is already booked
            if (bookedEnvironments.containsKey(connectRequest.getTargetEnv())) {
                return ResponseEntity.status(409).body(
                    new ClientConnectionModels.ApiResponse<>(false, 
                        "Target environment '" + connectRequest.getTargetEnv() + "' is already booked", null)
                );
            }
            
            // Generate token
            String token = UUID.randomUUID().toString();
            
            // Create connection response
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime expiresAt = now.plusMinutes(connectRequest.getExpiryMins());
            
            ClientConnectionModels.ConnectionResponse connectionResponse = new ClientConnectionModels.ConnectionResponse();
            connectionResponse.setToken(token);
            connectionResponse.setTargetEnv(connectRequest.getTargetEnv());
            connectionResponse.setOwner(connectRequest.getOwner());
            connectionResponse.setCreatedAt(now);
            connectionResponse.setExpiresAt(expiresAt);
            connectionResponse.setStatus("ACTIVE");
            
            // Store connection
            activeConnections.put(token, connectionResponse);
            bookedEnvironments.put(connectRequest.getTargetEnv(), token);
            
            return ResponseEntity.ok(
                new ClientConnectionModels.ApiResponse<>(true, "Connection created successfully", connectionResponse)
            );
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                new ClientConnectionModels.ApiResponse<>(false, "Internal server error: " + e.getMessage(), null)
            );
        }
    }

    @Override
    public ResponseEntity<ClientConnectionModels.ApiResponse<String>> submitRequest(
            @RequestBody ClientConnectionModels.Request request) {
        
        try {
            // Validate input
            if (StringUtils.isBlank(request.getToken())) {
                return ResponseEntity.badRequest().body(
                    new ClientConnectionModels.ApiResponse<>(false, "token is required", null)
                );
            }
            
            if (StringUtils.isBlank(request.getDsl())) {
                return ResponseEntity.badRequest().body(
                    new ClientConnectionModels.ApiResponse<>(false, "dsl is required", null)
                );
            }
            
            // Check if connection exists
            ClientConnectionModels.ConnectionResponse connection = activeConnections.get(request.getToken());
            if (connection == null) {
                return ResponseEntity.notFound().build();
            }
            
            // Check if connection has expired
            if (LocalDateTime.now().isAfter(connection.getExpiresAt())) {
                disconnectConnection(request.getToken());
                return ResponseEntity.status(410).body(
                    new ClientConnectionModels.ApiResponse<>(false, "Connection has expired", null)
                );
            }
            
            // Process the DSL request (placeholder for actual implementation)
            // TODO: Implement actual DSL processing logic
            
            return ResponseEntity.ok(
                new ClientConnectionModels.ApiResponse<>(true, "Request processed successfully", 
                    "DSL executed on environment: " + connection.getTargetEnv())
            );
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                new ClientConnectionModels.ApiResponse<>(false, "Internal server error: " + e.getMessage(), null)
            );
        }
    }

    @Override
    public ResponseEntity<ClientConnectionModels.ApiResponse<Void>> disconnect(
            @RequestBody ClientConnectionModels.Disconnect disconnectRequest) {
        
        try {
            // Validate input
            if (StringUtils.isBlank(disconnectRequest.getToken())) {
                return ResponseEntity.badRequest().body(
                    new ClientConnectionModels.ApiResponse<>(false, "token is required", null)
                );
            }
            
            boolean disconnected = disconnectConnection(disconnectRequest.getToken());
            
            if (disconnected) {
                return ResponseEntity.ok(
                    new ClientConnectionModels.ApiResponse<>(true, "Connection disconnected successfully", null)
                );
            } else {
                return ResponseEntity.notFound().build();
            }
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                new ClientConnectionModels.ApiResponse<>(false, "Internal server error: " + e.getMessage(), null)
            );
        }
    }

    @Override
    public ResponseEntity<ClientConnectionModels.ApiResponse<ClientConnectionModels.ConnectionResponse>> getConnectionStatus(
            @PathVariable String token) {
        
        ClientConnectionModels.ConnectionResponse connection = activeConnections.get(token);
        
        if (connection == null) {
            return ResponseEntity.notFound().build();
        }
        
        // Check if expired
        if (LocalDateTime.now().isAfter(connection.getExpiresAt())) {
            disconnectConnection(token);
            return ResponseEntity.status(410).body(
                new ClientConnectionModels.ApiResponse<>(false, "Connection has expired", null)
            );
        }
        
        return ResponseEntity.ok(
            new ClientConnectionModels.ApiResponse<>(true, "Connection is active", connection)
        );
    }

    /**
     * Helper method to disconnect a connection
     */
    private boolean disconnectConnection(String token) {
        ClientConnectionModels.ConnectionResponse connection = activeConnections.remove(token);
        if (connection != null) {
            bookedEnvironments.remove(connection.getTargetEnv());
            return true;
        }
        return false;
    }
}
