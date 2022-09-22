package com.spiashko.rfetch.demo.crudbase;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Persistable;

import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

@Getter
@Setter
@MappedSuperclass
public abstract class BaseEntity implements Persistable<Long> {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof BaseEntity))
            return false;

        BaseEntity other = (BaseEntity) o;

        return getId() != null &&
                getId().equals(other.getId());
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return String.format("Entity of type %s with id: %s", this.getClass().getName(), getId());
    }

    @JsonView({View.Retrieve.class})
    @Override
    public abstract Long getId();

    @JsonIgnore
    @Transient
    @Override
    public boolean isNew() {
        return null == getId();
    }
}
