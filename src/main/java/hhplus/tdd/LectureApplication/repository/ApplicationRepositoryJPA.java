package hhplus.tdd.LectureApplication.repository;

import hhplus.tdd.LectureApplication.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicationRepositoryJPA extends JpaRepository<Application, Long> {
	List<Application> findAllByStudentId(Long studentId);
}
