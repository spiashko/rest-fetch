package com.spiashko.rfetch.aat.common.person;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.spiashko.rfetch.aat.common.crudbase.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.Immutable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@FieldNameConstants
@Getter
@NoArgsConstructor
@Immutable
@Entity
@Table(name = "person")
public class PersonNumberOfKittensFormula extends BaseEntity {

    @JsonIgnore
    @Id
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @Formula(value = "(select count(1) from cat c where c.fk_owner = id)")
    private Integer value;

}
