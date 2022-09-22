package com.spiashko.rfetch.demo.cat;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import com.spiashko.rfetch.demo.crudbase.View;
import com.spiashko.rfetch.demo.crudbase.BaseEntity;
import com.spiashko.rfetch.demo.person.Person;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
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

    @JsonView({View.Retrieve.class})
    @NotEmpty
    @Column(name = "name")
    private String name;

    @JsonView({View.Retrieve.class})
    @NotNull
    @Column(name = "dob")
    private LocalDate dob;

    @JsonView({View.FullRetrieve.class})
    @NotNull
    @JsonIgnoreProperties(Person.Fields.kittens)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_owner")
    private Person owner;

    @JsonView({View.Retrieve.class})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_father", updatable = false)
    private Cat father;

    @JsonView({View.Retrieve.class})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_mother", updatable = false)
    private Cat mother;

    @Enumerated(EnumType.STRING)
    @JsonView({View.Retrieve.class})
    @NotNull
    @Column(name = "gender")
    private Gender gender;

    @JsonIgnoreProperties({Fields.mother, Fields.father})
    @JsonView({View.Retrieve.class})
    @OneToMany(mappedBy = Fields.mother, fetch = FetchType.LAZY)
    private Set<Cat> motherForKids;

    @JsonIgnoreProperties({Fields.father, Fields.mother})
    @JsonView({View.Retrieve.class})
    @OneToMany(mappedBy = Fields.father, fetch = FetchType.LAZY)
    private Set<Cat> fatherForKids;

}
