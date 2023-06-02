package dc.codecademy.miniproject.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import dc.codecademy.miniproject.models.CustomException;
import dc.codecademy.miniproject.models.ErrorResponse;
import dc.codecademy.miniproject.models.LoginRequest;
import dc.codecademy.miniproject.models.SecurityUser;
import dc.codecademy.miniproject.models.User;
import dc.codecademy.miniproject.services.JPAUserDetailsService;
import dc.codecademy.miniproject.services.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.log4j.Log4j2;

@RestController
@Log4j2
@CrossOrigin(originPatterns = "*", methods = { RequestMethod.POST })
@Tag(name = "Auth Operations", description = "API's for signup and login for the users.")
@RequestMapping("api/v1/auth")
public class AuthController {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private JPAUserDetailsService userDetailsService;

    @PostMapping("/login")
    @Operation(summary = "Login", description = "Generates a JWT token for the given username and password.", responses = {
            @ApiResponse(responseCode = "200", description = "Success", content = @Content(schema = @Schema(implementation = String.class), mediaType = MediaType.TEXT_PLAIN_VALUE)),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Void.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(implementation = Void.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = MediaType.APPLICATION_JSON_VALUE))
    })
    public String getJwtToken(@RequestBody LoginRequest request) {
        var authentication = authManager
                .authenticate(new UsernamePasswordAuthenticationToken(request.username(), request.password()));
        return tokenService.generateToken((SecurityUser) authentication.getPrincipal());
    }

    @PostMapping("/signup")
    @Operation(summary = "Sign Up", description = "Creates a new user with the given username and password.", responses = {
            @ApiResponse(responseCode = "201", description = "Success", content = @Content(schema = @Schema(implementation = User.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "409", description = "Conflict", content = @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = MediaType.APPLICATION_JSON_VALUE))
    })

    public ResponseEntity<User> createUser(@RequestBody LoginRequest request) throws CustomException {
        final String username = request.username();
        if (userDetailsService.findByUsername(username).isPresent()) {
            throw new CustomException("A user with same username exists", HttpStatus.CONFLICT);
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userDetailsService.saveUser(username, request.password()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleSQLExceptions(Exception exe) {
        log.error(exe.getMessage(), exe);
        if (CustomException.class.isAssignableFrom(exe.getClass())) {
            CustomException cse = (CustomException) exe;
            return ResponseEntity.status(cse.getStatusCode())
                    .body(new ErrorResponse(exe.getMessage()));
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(exe.getMessage()));
    }

}
