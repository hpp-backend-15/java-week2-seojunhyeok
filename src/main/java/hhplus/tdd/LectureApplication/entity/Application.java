package hhplus.tdd.LectureApplication.entity;

import lombok.*;
import jakarta.persistence.*;

@Entity
@Data
@NoArgsConstructor
@Table(name = "application", uniqueConstraints = {
		@UniqueConstraint(columnNames = {"student_id", "lecture_id"})
})
public class Application {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "student_id", nullable = false)
	private Long studentId;

	@ManyToOne
	@JoinColumn(name = "lecture_id", nullable = false)
	private Lecture lecture;
}