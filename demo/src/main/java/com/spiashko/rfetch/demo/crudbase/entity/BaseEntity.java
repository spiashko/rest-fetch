package com.spiashko.rfetch.demo.crudbase.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.spiashko.rfetch.demo.crudbase.View;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Persistable;

import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.Transient;
import java.util.UUID;

@Getter
@Setter
@MappedSuperclass
public abstract class BaseEntity implements Persistable<UUID> {

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
    public abstract UUID getId();

    public abstract void setId(UUID id);

    @JsonIgnore
    @Transient
    @Override
    public boolean isNew() {
        return null == getId();
    }

    @PrePersist
    public void autofill() {
        if (getId() == null) {
            this.setId(UUID.randomUUID());
        }
    }
}
