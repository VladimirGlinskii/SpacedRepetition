package net.thumbtack.spaced.repetition.repos;

import net.thumbtack.spaced.repetition.model.Statistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface StatisticsRepository extends JpaRepository<Statistics, Integer> {

    List<Statistics> findAllByUserIdAndDateGreaterThanEqualOrderByDateAsc(int userId, LocalDate date);

    Optional<Statistics> findByUserIdAndDate(int userId, LocalDate date);

    @Transactional
    void deleteStatisticsByDateBefore(LocalDate date);
}
