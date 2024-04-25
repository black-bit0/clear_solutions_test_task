package org.example.clear_solutions.repositories;

import java.time.LocalDate;
import java.util.List;
import jakarta.transaction.Transactional;
import org.example.clear_solutions.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Transactional
    @Modifying
    @Query("UPDATE User u SET " +
            "u.email = CASE WHEN :#{#user.email} IS NOT NULL THEN :#{#user.email} ELSE u.email END, " +
            "u.firstName = CASE WHEN :#{#user.firstName} IS NOT NULL THEN :#{#user.firstName} ELSE u.firstName END, " +
            "u.lastName = CASE WHEN :#{#user.lastName} IS NOT NULL THEN :#{#user.lastName} ELSE u.lastName END, " +
            "u.address = CASE WHEN :#{#user.address} IS NOT NULL THEN :#{#user.address} ELSE u.address END, " +
            "u.phoneNumber = CASE WHEN :#{#user.phoneNumber} IS NOT NULL THEN :#{#user.phoneNumber} ELSE u.phoneNumber END " +
            "WHERE u.id = :userId")
    void updateUserFields(@Param("userId") Long userId, @Param("user") User user);

    List<User> findByDateOfBirthBetween(LocalDate from, LocalDate to);

    void deleteByIsDeletedAndDateOfDeletingBefore(boolean isDeleted, LocalDate date);
}
