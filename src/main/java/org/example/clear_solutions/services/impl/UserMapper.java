package org.example.clear_solutions.services.impl;

import org.example.clear_solutions.dto.UserRequestDTO;
import org.example.clear_solutions.dto.UserResponseDTO;
import org.example.clear_solutions.models.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserMapper {

    public User dtoToEntity(UserRequestDTO dto) {
        return User.builder()
                .email(dto.getEmail())
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .dateOfBirth(dto.getDateOfBirth())
                .address(dto.getAddress())
                .phoneNumber(dto.getPhoneNumber())
                .build();
    }

    public UserResponseDTO entityToDto(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .dateOfBirth(user.getDateOfBirth())
                .address(user.getAddress())
                .phoneNumber(user.getPhoneNumber())
                .build();
    }

    public List<UserResponseDTO> entitiesToDtos(List<User> users) {
        return users.stream()
                .map(this::entityToDto)
                .collect(Collectors.toList());
    }
}