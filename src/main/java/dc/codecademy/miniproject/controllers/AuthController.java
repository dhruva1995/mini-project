package dc.codecademy.miniproject.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dc.codecademy.miniproject.models.LoginRequest;
import dc.codecademy.miniproject.models.SecurityUser;
import dc.codecademy.miniproject.services.TokenService;

@RestController
@RequestMapping("api/v1/auth")
public class AuthController {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private AuthenticationManager authManager;

    @PostMapping("/login")
    public String token(@RequestBody LoginRequest request) {
        var authentication = authManager
                .authenticate(new UsernamePasswordAuthenticationToken(request.username(), request.password()));
        return tokenService.generateToken((SecurityUser) authentication.getPrincipal());
    }

    @PostMapping("/signup")
    public String token(@RequestBody LoginRequest request) {

    }

}
