package com.spiashko.rfetch.parser.entites.cats;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import javax.persistence.*;
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

    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = Cat.Fields.owner, fetch = FetchType.LAZY)
    private Set<Cat> kittens;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_best_friend")
    private Person bestFriend;

    @OneToMany(mappedBy = Fields.bestFriend, fetch = FetchType.LAZY)
    private Set<Person> bestFriendForPeople;

}
