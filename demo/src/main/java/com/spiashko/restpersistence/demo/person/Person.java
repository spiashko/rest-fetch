package com.spiashko.restpersistence.demo.person;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import com.spiashko.restpersistence.demo.cat.Cat;
import com.spiashko.restpersistence.demo.crudbase.View;
import com.spiashko.restpersistence.demo.crudbase.entity.BaseJournalEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "person")
public class Person extends BaseJournalEntity {

    public static final String KITTENS = "kittens";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

    @JsonView({View.Retrieve.class, View.Update.class, View.Create.class})
    @NotEmpty
    @Column(name = "name")
    private String name;

    @JsonView({View.Retrieve.class})
    @JsonIgnoreProperties(Cat.OWNER)
    @OneToMany(mappedBy = Cat.OWNER, fetch = FetchType.LAZY)
    private Set<Cat> kittens;

}
