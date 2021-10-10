package com.spiashko.restpersistence.demo.cat;


import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import com.spiashko.restpersistence.demo.crudbase.View;
import com.spiashko.restpersistence.demo.crudbase.entity.BaseJournalEntity;
import com.spiashko.restpersistence.demo.person.Person;
import com.spiashko.restpersistence.jacksonjpa.entitybyid.EntityByIdDeserialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cat")
public class Cat extends BaseJournalEntity {

    public static final String OWNER = "owner";
    public static final String PARENT = "parent";
    public static final String OWNER_ID = OWNER + "Id";
    public static final String PARENT_ID = PARENT + "Id";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

    @JsonView({View.Retrieve.class, View.Update.class, View.Create.class})
    @NotEmpty
    @Column(name = "name")
    private String name;

    @JsonView({View.Retrieve.class, View.Update.class, View.Create.class})
    @NotNull
    @Column(name = "dob")
    private LocalDate dob;

    @EntityByIdDeserialize(OWNER_ID)
    @JsonView({View.Retrieve.class})
    @NotNull
    @JsonIgnoreProperties(Person.KITTENS)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_owner")
    private Person owner;

    @EntityByIdDeserialize(PARENT_ID)
    @JsonView({View.Retrieve.class})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_parent", updatable = false)
    private Cat parent;

    @JsonView({View.Retrieve.class})
    @JsonIgnoreProperties(PARENT)
    @OneToMany(mappedBy = Cat.PARENT, fetch = FetchType.LAZY)
    private Set<Cat> kids;

    @JsonGetter(OWNER_ID)
    @JsonView({View.Retrieve.class, View.Create.class})
    public UUID getOwnerId() {
        return owner.getId();
    }

    @JsonGetter(PARENT_ID)
    @JsonView({View.Retrieve.class, View.Create.class})
    public UUID getParentId() {
        if (parent == null) {
            return null;
        }
        return parent.getId();
    }
}
