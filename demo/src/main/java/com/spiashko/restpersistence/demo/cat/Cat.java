package com.spiashko.restpersistence.demo.cat;


import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import com.spiashko.restpersistence.demo.crudbase.View;
import com.spiashko.restpersistence.demo.crudbase.entity.BaseEntity;
import com.spiashko.restpersistence.demo.person.Person;
import com.spiashko.restpersistence.jacksonjpa.entitybyid.EntityByIdDeserialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.CollectionUtils;

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
public class Cat extends BaseEntity {

    public static final String OWNER = "owner";
    public static final String FATHER = "father";
    public static final String MOTHER = "mother";
    public static final String OWNER_ID = OWNER + "Id";
    public static final String FATHER_ID = FATHER + "Id";
    public static final String MOTHER_ID = MOTHER + "Id";

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

    @EntityByIdDeserialize(FATHER_ID)
    @JsonView({View.Retrieve.class})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_father", updatable = false)
    private Cat father;

    @EntityByIdDeserialize(MOTHER_ID)
    @JsonView({View.Retrieve.class})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_mother", updatable = false)
    private Cat mother;

    @JsonIgnore
    @OneToMany(mappedBy = Cat.MOTHER, fetch = FetchType.LAZY)
    private Set<Cat> mKids;

    @JsonIgnore
    @OneToMany(mappedBy = Cat.FATHER, fetch = FetchType.LAZY)
    private Set<Cat> fKids;

    @JsonIgnoreProperties({Cat.MOTHER, Cat.FATHER})
    @JsonView({View.Retrieve.class})
    @Transient
    private Set<Cat> kids;

    @JsonGetter(OWNER_ID)
    @JsonView({View.Retrieve.class, View.Create.class})
    public UUID getOwnerId() {
        return owner.getId();
    }

    @JsonGetter(FATHER_ID)
    @JsonView({View.Retrieve.class, View.Create.class})
    public UUID getFatherId() {
        if (father == null) {
            return null;
        }
        return father.getId();
    }

    @JsonGetter(MOTHER_ID)
    @JsonView({View.Retrieve.class, View.Create.class})
    public UUID getMotherId() {
        if (mother == null) {
            return null;
        }
        return mother.getId();
    }

    @PostLoad
    private void initializeKids() {
        if (CollectionUtils.isEmpty(mKids)) {
            kids = fKids;
        } else {
            kids = mKids;
        }
    }
}
