import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

/**
 * Client Connection Management API Interface
 * 
 * Provides APIs for managing client connections to target environments.
 */
public interface ClientConnectionAPI {

    /**
     * Create a new connection
     * 
     * @param connectRequest Connection request with targetEnv, expiryMins, and owner
     * @return Connection response with token
     */
    @PostMapping("/connect")
    ResponseEntity<ClientConnectionModels.ApiResponse<ClientConnectionModels.ConnectionResponse>> connect(
            @RequestBody ClientConnectionModels.Connect connectRequest);

    /**
     * Submit a request to an existing connection
     * 
     * @param request Request with token and DSL
     * @return Response indicating success or failure
     */
    @PostMapping("/request")
    ResponseEntity<ClientConnectionModels.ApiResponse<String>> submitRequest(
            @RequestBody ClientConnectionModels.Request request);

    /**
     * Disconnect an existing connection
     * 
     * @param disconnectRequest Disconnect request with token
     * @return Response indicating success or failure
     */
    @PostMapping("/disconnect")
    ResponseEntity<ClientConnectionModels.ApiResponse<Void>> disconnect(
            @RequestBody ClientConnectionModels.Disconnect disconnectRequest);

    /**
     * Get connection status
     * 
     * @param token Connection token
     * @return Connection details if active
     */
    @GetMapping("/{token}")
    ResponseEntity<ClientConnectionModels.ApiResponse<ClientConnectionModels.ConnectionResponse>> getConnectionStatus(
            @PathVariable String token);
}
