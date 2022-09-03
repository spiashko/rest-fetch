package com.spiashko.rfetch.parser.entites.courses;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

/**
 * A CourseExtraInfo.
 */
@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "course_extra_info")
public class CourseExtraInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private Long id;

    @Lob
    @Column(name = "summary", nullable = false)
    private String summary;

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id")
    private Course course;

}
