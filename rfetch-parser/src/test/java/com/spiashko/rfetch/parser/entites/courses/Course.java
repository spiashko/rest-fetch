package com.spiashko.rfetch.parser.entites.courses;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * A Course.
 */
@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "course")
public class Course implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "title", length = 50, nullable = false)
    private String title;

    @Column(name = "image_url", length = 200, nullable = false)
    private String imageUrl;

    @OneToMany(mappedBy = "course")
    private Set<Module> modules = new HashSet<>();

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private User teacher;

}
