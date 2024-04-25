package org.example.clear_solutions.services.impl;

import java.time.LocalDate;
import java.util.List;
import org.example.clear_solutions.models.User;
import org.example.clear_solutions.repositories.UserRepository;
import org.example.clear_solutions.services.UserService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public User createUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public void updateUserFields(Long id, User user) {
        userRepository.updateUserFields(id, user);
    }

    @Override
    public boolean isValidDateRange(LocalDate from, LocalDate to) {
        return !from.isAfter(to);
    }

    @Override
    public List<User> searchUsersByBirthDateRange(LocalDate from, LocalDate to) {
        return userRepository.findByDateOfBirthBetween(from, to);
    }

    @Override
    public void deleteUser(Long id) {
        updateUserFields(id, User.builder().isDeleted(true).dateOfDeleting(LocalDate.now()).build());
    }

    @Override
    public void restoreUser(Long id) {
        updateUserFields(id, User.builder().isDeleted(false).dateOfDeleting(null).build());
    }

    @Override
    public boolean isAdult(LocalDate birthDate, Integer age) {
        LocalDate currentDate = LocalDate.now();
        LocalDate adultDate = birthDate.plusYears(age);
        return !currentDate.isBefore(adultDate);
    }

    public void deleteUsersMarkedForDeletion() {
        LocalDate thirtyDaysAgo = LocalDate.now().minusDays(30);
        userRepository.deleteByIsDeletedAndDateOfDeletingBefore(true, thirtyDaysAgo);
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void scheduleDeleteUsersMarkedForDeletion() {
        deleteUsersMarkedForDeletion();
    }

}
