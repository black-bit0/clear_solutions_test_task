package org.example.clear_solutions.services;

import java.time.LocalDate;
import java.util.List;
import org.example.clear_solutions.models.User;

public interface UserService {
    User createUser(User user);

    void updateUserFields(Long id, User user);

    boolean isValidDateRange(LocalDate from, LocalDate to);

    List<User> searchUsersByBirthDateRange(LocalDate from, LocalDate to);

    void deleteUser(Long id);

    void restoreUser(Long id);

    boolean isAdult(LocalDate birthDate, Integer age);
}
