package net.thumbtack.spaced.repetition.repos;

import net.thumbtack.spaced.repetition.model.Exercise;
import net.thumbtack.spaced.repetition.model.enums.ExerciseStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ExerciseRepository extends JpaRepository<Exercise, UUID> {

    Exercise findTopByUserIdAndStatus(int userId, ExerciseStatus status);

}
