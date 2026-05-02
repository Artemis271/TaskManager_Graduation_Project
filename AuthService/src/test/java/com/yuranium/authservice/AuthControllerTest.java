package com.yuranium.authservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.yuranium.authservice.controller.AuthController;
import com.yuranium.authservice.enums.RoleType;
import com.yuranium.authservice.models.AuthValidationResponse;
import com.yuranium.authservice.models.dto.*;
import com.yuranium.authservice.service.UserService;
import com.yuranium.authservice.util.JwtUtil;
import com.yuranium.authservice.util.RestExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class AuthControllerTest
{

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthController authController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp()
    {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
        objectMapper = new ObjectMapper();

        objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        MappingJackson2HttpMessageConverter jacksonConverter =
                new MappingJackson2HttpMessageConverter(objectMapper);

        mockMvc = MockMvcBuilders
                .standaloneSetup(authController)
                .setControllerAdvice(new RestExceptionHandler())
                .setMessageConverters(jacksonConverter)
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();
    }

    @Test
    void validateToken_Valid() throws Exception
    {
        String token = "Bearer valid-token";
        AuthValidationResponse response = AuthValidationResponse.builder()
                .valid(false)
                .build();
        given(jwtUtil.isValidToken(token)).willReturn(response);

        mockMvc.perform(post("/auth/validate")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    void createAuthToken_Success() throws Exception
    {
        UserLoginDto loginDto = new UserLoginDto("john", "pass");
        UserDetails userDetails = User.withUsername("john").password("pass").roles("USER").build();
        given(jwtUtil.generateToken(userDetails)).willReturn("jwt-token");
        given(userService.loadUserByUsername("john")).willReturn(userDetails);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"));
    }

    @Test
    void createAuthToken_Failure() throws Exception
    {
        UserLoginDto loginDto = new UserLoginDto("john", "wrong");
        doThrow(new BadCredentialsException("Bad creds"))
                .when(authenticationManager)
                .authenticate(any(UsernamePasswordAuthenticationToken.class));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void registerUser() throws Exception
    {
        UserInputDto inputDto = new UserInputDto("john", "John", "Doe", "pass", "john@example.com", List.of());
        UserDto created = new UserDto(1L, "john", "john@example.com");
        given(userService.createUser(any(UserInputDto.class))).willReturn(created);

        mockMvc.perform(multipart("/auth/registration")
                        .file(new MockMultipartFile("avatars", new byte[0]))
                        .param("username", inputDto.username())
                        .param("name", inputDto.name())
                        .param("lastName", inputDto.lastName())
                        .param("password", inputDto.password())
                        .param("email", inputDto.email())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(created)));
    }

    @Test
    void getUser() throws Exception
    {
        UserInfoDto info = new UserInfoDto(1L, "john", "John", "Doe",
                "john@example.com", LocalDateTime.now(), true, null, Set.of());
        given(userService.getUser(1L)).willReturn(info);

        mockMvc.perform(get("/auth/user/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(info)));
    }

    @Test
    void updateUser() throws Exception
    {
        UserUpdateDto updateDto = new UserUpdateDto("john", "John", "Doe", "newpass",
                true, null, Set.of(new RoleDto(1, RoleType.ROLE_USER)));

        UserDto updated = new UserDto(1L, "john", "john@example.com");

        given(userService.updateUser(1L, updateDto)).willReturn(updated);

        mockMvc.perform(multipart("/auth/user/update/1")
                        .with(request -> {
                            request.setMethod("PATCH");
                            return request;
                        })
                        .param("username", updateDto.username())
                        .param("name", updateDto.name())
                        .param("lastName", updateDto.lastName())
                        .param("password", updateDto.password())
                        .param("activity", updateDto.activity().toString()))
                .andExpect(status().isOk());
    }

    @Test
    void updateUserAvatar() throws Exception
    {
        MockMultipartFile file = new MockMultipartFile("file",
                "avatar.png", "image/png", "data".getBytes(StandardCharsets.UTF_8));
        doNothing().when(userService).updateUserAvatar(1L, file);

        mockMvc.perform(multipart("/auth/user/4/update-avatar")
                        .file(file)
                        .with(request -> {
                            request.setMethod("PATCH");
                            return request;
                        }))
                .andExpect(status().isOk());
    }

    @Test
    void deleteUser() throws Exception
    {
        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/auth/user/delete/1"))
                .andExpect(status().isNoContent());
    }
}