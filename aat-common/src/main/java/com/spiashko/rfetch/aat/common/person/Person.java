package com.spiashko.rfetch.aat.common.person;

import com.spiashko.rfetch.aat.common.cat.Cat;
import com.spiashko.rfetch.aat.common.crudbase.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import javax.persistence.*;
import java.util.Set;

@FieldNameConstants
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "person")
public class Person extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

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
