package com.artemis.authservice.controller;

import com.artemis.authservice.models.dto.*;

import java.util.List;
import com.artemis.authservice.service.UserService;
import com.artemis.authservice.util.JwtUtil;
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
        var response = jwtUtil.isValidToken(authHeader);
        if (!Boolean.TRUE.equals(response.valid()))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        return ResponseEntity.ok(response);
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

    @GetMapping("/admin/users")
    public ResponseEntity<List<UserInfoDto>> getAllUsers()
    {
        return new ResponseEntity<>(userService.getAllUsers(), HttpStatus.OK);
    }

    @PatchMapping("/admin/assign/{id}")
    public ResponseEntity<UserDto> assignAdminRole(@PathVariable Long id)
    {
        return new ResponseEntity<>(
                userService.assignAdminRole(id),
                HttpStatus.OK
        );
    }

    @PatchMapping("/admin/revoke/{id}")
    public ResponseEntity<UserDto> revokeAdminRole(@PathVariable Long id)
    {
        return new ResponseEntity<>(
                userService.revokeAdminRole(id),
                HttpStatus.OK
        );
    }

    @PatchMapping("/admin/restore/{id}")
    public ResponseEntity<UserDto> restoreUser(@PathVariable Long id)
    {
        return new ResponseEntity<>(
                userService.restoreUser(id),
                HttpStatus.OK
        );
    }
}