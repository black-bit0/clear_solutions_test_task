package org.example.clear_solutions.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.clear_solutions.configurations.AppProperties;
import org.example.clear_solutions.dto.UserRequestDTO;
import org.example.clear_solutions.dto.UserResponseDTO;
import org.example.clear_solutions.models.User;
import org.example.clear_solutions.services.UserService;
import org.example.clear_solutions.services.impl.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UserControllerTest {
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private UserService userService;

    @Mock
    private AppProperties appProperties;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        objectMapper = new ObjectMapper();
        appProperties.setMinimumAge(18);
    }

    @Test
    void createUser() throws Exception {
        // Create a UserRequestDTO object
        UserRequestDTO userRequestDTO = new UserRequestDTO();
        userRequestDTO.setEmail("test@example.com");
        userRequestDTO.setFirstName("John");
        userRequestDTO.setLastName("Doe");
        userRequestDTO.setDateOfBirth(LocalDate.of(2000, 1, 1));

        String serialisedUser = objectMapper.writeValueAsString(userRequestDTO);

        // Mock userService and userMapper behavior
        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setDateOfBirth(LocalDate.of(2000, 1, 1));

        when(userMapper.dtoToEntity(any(UserRequestDTO.class))).thenReturn(user);
        when(userService.isAdult(any(LocalDate.class), anyInt())).thenReturn(true);
        when(userService.createUser(any(User.class))).thenReturn(user);
        when(userMapper.entityToDto(any(User.class))).thenReturn(null);

        // Perform POST request
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(serialisedUser))
                .andExpect(status().isCreated())
                .andReturn();
    }

    @Test
    void updateUserFields() throws Exception {
        // Mock user updates
        UserRequestDTO userRequestDTO = new UserRequestDTO();
        userRequestDTO.setFirstName("John");

        String serialisedUser = objectMapper.writeValueAsString(userRequestDTO);

        // Mock updated user
        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setFirstName("John");

        // Perform request
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put("/users/" + updatedUser.getId() + "/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(serialisedUser);

        ResultActions resultActions = mockMvc.perform(requestBuilder);

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void deleteUser() throws Exception {
        // Mock userId
        Long userId = 1L;

        // Perform DELETE request
        mockMvc.perform(MockMvcRequestBuilders.delete("/users/{userId}/delete", userId))
                .andExpect(status().isNoContent())
                .andReturn();
    }

    @Test
    void restoreUser() throws Exception {
        // Mock userId
        Long userId = 1L;

        // Perform PUT request to restore user
        mockMvc.perform(MockMvcRequestBuilders.put("/users/{userId}/restore", userId))
                .andExpect(MockMvcResultMatchers.status().isNoContent())
                .andReturn();
    }


    @Test
    void searchUsersByBirthDateRange() throws Exception {
        // Arrange
        LocalDate from = LocalDate.of(1990, 1, 1);
        LocalDate to = LocalDate.of(2000, 12, 31);

        // Mock userService behavior
        List<User> users = new ArrayList<>();

        when(userService.isValidDateRange(from, to)).thenReturn(true);
        when(userService.searchUsersByBirthDateRange(from, to)).thenReturn(users);

        // Perform GET request
        MvcResult result = mockMvc.perform(get("/users/search")
                        .param("from", from.toString())
                        .param("to", to.toString()))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        List<UserResponseDTO> userResponseDTOs = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
    }
}