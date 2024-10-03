package hhplus.tdd.LectureApplication.entity;

import lombok.*;
import jakarta.persistence.*;

@Entity
@Data
@NoArgsConstructor
public class Application {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long studentId;

	@ManyToOne
	@JoinColumn(name = "lecture_id")
	private Lecture lecture;
}
