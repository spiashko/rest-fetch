package com.spiashko.rfetch.demo.person;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import com.spiashko.rfetch.demo.cat.Cat;
import com.spiashko.rfetch.demo.crudbase.View;
import com.spiashko.rfetch.demo.crudbase.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Set;
import java.util.UUID;

@FieldNameConstants
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "person")
public class Person extends BaseEntity {

    @Id
    @Column(name = "id")
    private UUID id;

    @JsonView({View.Retrieve.class})
    @NotEmpty
    @Column(name = "name")
    private String name;

    @JsonView({View.Retrieve.class})
    @JsonIgnoreProperties(Cat.Fields.owner)
    @OneToMany(mappedBy = Cat.Fields.owner, fetch = FetchType.LAZY)
    private Set<Cat> kittens;

    @JsonView({View.Retrieve.class})
    @NotNull
    @JsonIgnoreProperties(Person.Fields.bestFriendForPeople)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_best_friend")
    private Person bestFriend;

    @JsonView({View.Retrieve.class})
    @JsonIgnoreProperties(Person.Fields.bestFriend)
    @OneToMany(mappedBy = Person.Fields.bestFriend, fetch = FetchType.LAZY)
    private Set<Person> bestFriendForPeople;

}
