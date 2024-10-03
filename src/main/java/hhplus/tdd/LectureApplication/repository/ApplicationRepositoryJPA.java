package hhplus.tdd.LectureApplication.repository;

import hhplus.tdd.LectureApplication.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepositoryJPA extends JpaRepository<Application, Long> {
	List<Application> findAllByStudentId(Long studentId);
	Optional<Application> findByStudentIdAndLectureId(Long studentId, Long lectureId);
}
