package hhplus.tdd.LectureApplication.repository;

import hhplus.tdd.LectureApplication.entity.Lecture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import jakarta.persistence.LockModeType;


import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface LectureRepositoryJPA extends JpaRepository<Lecture, Long> {
	List<Lecture> findAllByDateGreaterThanEqual(LocalDate date);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT l FROM Lecture l WHERE l.id = :lectureId")
	Optional<Lecture> findByIdWithLock(@Param("lectureId") Long lectureId);
}
