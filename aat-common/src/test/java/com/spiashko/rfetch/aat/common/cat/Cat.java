package com.spiashko.rfetch.aat.common.cat;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.spiashko.rfetch.aat.common.crudbase.BaseEntity;
import com.spiashko.rfetch.aat.common.person.Person;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Set;

@FieldNameConstants
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cat")
public class Cat extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(name = "dob")
    private LocalDate dob;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_owner")
    private Person owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_father", updatable = false)
    private Cat father;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_mother", updatable = false)
    private Cat mother;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;

    @OneToMany(mappedBy = Fields.mother, fetch = FetchType.LAZY)
    private Set<Cat> motherForKids;

    @OneToMany(mappedBy = Fields.father, fetch = FetchType.LAZY)
    private Set<Cat> fatherForKids;

}
