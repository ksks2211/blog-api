package org.example.postapi.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.postapi.user.dto.UserRegisterRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static org.example.postapi.user.AppUserConstants.ACCOUNT_URL_PREFIX;

/**
 * @author rival
 * @since 2025-01-16
 */

@RestController
@RequestMapping(ACCOUNT_URL_PREFIX)
@RequiredArgsConstructor
@Slf4j
public class AppUserController {

    private final AppUserService appUserService;


    //  POST /api/accounts/signup   { email : "example123@example.com", password : "password" }
    @PostMapping("/signup")
    @PreAuthorize("!isAuthenticated()")
    public ResponseEntity<?> signUp(@RequestBody @Valid final UserRegisterRequest request) {
        appUserService.createAppUser(request.getEmail(), request.getPassword());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }



    @GetMapping("/who-am-i")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> whoAmI(@AuthenticationPrincipal final Object principal) {
        return ResponseEntity.ok(principal);
    }


}
