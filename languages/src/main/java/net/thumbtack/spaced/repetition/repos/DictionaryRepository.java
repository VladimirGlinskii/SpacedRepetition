package net.thumbtack.spaced.repetition.repos;

import net.thumbtack.spaced.repetition.model.Dictionary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DictionaryRepository extends JpaRepository<Dictionary, Integer> {

    Optional<Dictionary> findByNameIgnoreCase(String name);

    Page<Dictionary> findAllByNameStartsWithIgnoreCase(Pageable pageable, String filter);

}
