package org.example.clear_solutions.controller;

import org.example.clear_solutions.configurations.AppProperties;
import org.example.clear_solutions.dto.UserRequestDTO;
import org.example.clear_solutions.dto.UserResponseDTO;
import org.example.clear_solutions.models.User;
import org.example.clear_solutions.services.UserService;
import org.example.clear_solutions.services.impl.UserMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.dao.DataIntegrityViolationException;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final AppProperties appProperties;
    private final UserService userService;
    private final UserMapper userMapper;

    public UserController(AppProperties appProperties, UserService userService, UserMapper userMapper) {
        this.appProperties = appProperties;
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @PostMapping
    public ResponseEntity<String> createUser(@Valid @RequestBody UserRequestDTO userRequestDTO) {
        User user = userMapper.dtoToEntity(userRequestDTO);

        int minimumAge = appProperties.getMinimumAge();
        if (!userService.isAdult(user.getDateOfBirth(), minimumAge)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User is not adult");
        }

        try {
            userService.createUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body("Success");
        } catch (DataIntegrityViolationException e) {
            String errorMessage = "User with the provided email already exists.";
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorMessage);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
        }
    }

    @PutMapping("/{userId}/update")
    public ResponseEntity<String> updateUserFields(@PathVariable Long userId, @RequestBody UserRequestDTO userRequestDTO) {
        User userUpdates = userMapper.dtoToEntity(userRequestDTO);

        try {
            userService.updateUserFields(userId, userUpdates);
            return ResponseEntity.status(HttpStatus.OK).body("Success");
        } catch (DataIntegrityViolationException e) {
            String errorMessage = "User with the provided email already exists.";
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorMessage);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
        }
    }

    @DeleteMapping("/{userId}/delete")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        try {
            userService.deleteUser(userId);
        } catch (Exception e) {
            // Logg error
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{userId}/restore")
    public ResponseEntity<Void> restoreUser(@PathVariable Long userId) {
        try {
            userService.restoreUser(userId);
        } catch (Exception e) {
            // Logg error
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserResponseDTO>> searchUsersByBirthDateRange(@RequestParam("from") LocalDate from,
                                                                             @RequestParam("to") LocalDate to) {
        if (!userService.isValidDateRange(from, to)) {
            return ResponseEntity.badRequest().build();
        }

        List<User> users = userService.searchUsersByBirthDateRange(from, to);
        List<UserResponseDTO> userResponseDTOs = userMapper.entitiesToDtos(users);

        return ResponseEntity.ok(userResponseDTOs);
    }
}
