package com.yuranium.authservice.controller;

import com.yuranium.authservice.models.dto.*;
import com.yuranium.authservice.service.UserService;
import com.yuranium.authservice.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController
{
    private final UserService userService;

    private final AuthenticationManager authenticationManager;

    private final JwtUtil jwtUtil;

    @PostMapping("/validate")
    public ResponseEntity<?> validateToken(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader)
    {
        return new ResponseEntity<>(
                jwtUtil.isValidToken(authHeader),
                HttpStatus.OK
        );
    }

    @PostMapping("/login")
    public ResponseEntity<?> createAuthToken(@RequestBody UserLoginDto userLogin)
    {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                userLogin.username(), userLogin.password()
        ));

        UserDetails user = userService.loadUserByUsername(userLogin.username());
        return new ResponseEntity<>(
                Map.of("token", jwtUtil.generateToken(user)),
                HttpStatus.OK);
    }

    @PostMapping("/registration")
    public ResponseEntity<UserDto> registerUser(@ModelAttribute UserInputDto userDto)
    {
        return new ResponseEntity<>(
                userService.createUser(userDto),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<UserInfoDto> getUser(@PathVariable Long id)
    {
        return new ResponseEntity<>(
                userService.getUser(id),
                HttpStatus.OK
        );
    }

    @PatchMapping("/user/update/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id,
                                        @ModelAttribute UserUpdateDto userDto)
    {
        return new ResponseEntity<>(
                userService.updateUser(id, userDto),
                HttpStatus.OK
        );
    }

    @PatchMapping("/user/{id}/update-avatar")
    public ResponseEntity<?> updateUserAvatar(@PathVariable Long id,
                                              @RequestPart MultipartFile file)
    {
        userService.updateUserAvatar(id, file);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/user/delete/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id)
    {
        userService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}