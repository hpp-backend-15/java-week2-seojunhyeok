package hhplus.tdd.LectureApplication.repository;

import hhplus.tdd.LectureApplication.entity.Lecture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LectureRepositoryJPA extends JpaRepository<Lecture, Long> {
	List<Lecture> findAllByDateGreaterThanEqual(LocalDate date);
}
