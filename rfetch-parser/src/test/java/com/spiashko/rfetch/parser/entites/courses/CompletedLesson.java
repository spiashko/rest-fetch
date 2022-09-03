package com.spiashko.rfetch.parser.entites.courses;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

/**
 * A CompletedLesson.
 */
@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "completed_lesson")
public class CompletedLesson implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Lesson lesson;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Enrollment enrollment;

}
