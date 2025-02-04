package org.example.postapi.domain.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.postapi.common.dto.ApiResponse;
import org.example.postapi.security.AuthUser;
import org.example.postapi.domain.user.dto.UserRegisterRequest;
import org.example.postapi.domain.user.dto.UserRegisterResponse;
import org.example.postapi.domain.user.dto.UserStateResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * @author rival
 * @since 2025-01-16
 */

@RestController
@RequestMapping(AppUserConstants.ACCOUNT_URL_PREFIX)
@RequiredArgsConstructor
@Slf4j
public class AppUserController {

    private final AppUserService appUserService;


    //  POST /api/accounts/signup   { email : "example123@example.com", password : "password" }
    @PostMapping("/signup")
    @PreAuthorize("!isAuthenticated()")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> signUp(@RequestBody @Valid final UserRegisterRequest request) {
        appUserService.createAppUser(request.getEmail(), request.getPassword());
        UserRegisterResponse data = new UserRegisterResponse(request.getEmail());
        var body = ApiResponse.success("New user created", data);
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }



    @GetMapping("/me")
    public ResponseEntity<?> whoAmI(@AuthenticationPrincipal final Object principal) {
        var userState = new UserStateResponse();
        if(principal instanceof AuthUser authUser){
            userState.setLoggedIn(true);
            userState.setNickname(authUser.getNickname());
        }

        var body = ApiResponse.success("User information", userState);
        return ResponseEntity.ok(body);
    }


}
