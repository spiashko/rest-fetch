package com.spiashko.restpersistence.jacksonjpa.entitybyid;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.DelegatingDeserializer;
import com.fasterxml.jackson.databind.introspect.AnnotatedField;
import com.fasterxml.jackson.databind.node.TextNode;
import org.springframework.data.util.ReflectionUtils;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import java.io.IOException;
import java.util.UUID;

public class EntityByIdDeserializer extends DelegatingDeserializer {

    private final EntityManager entityManager;
    private final BeanDescription beanDescription;

    public EntityByIdDeserializer(JsonDeserializer<?> delegate,
                                  EntityManager entityManager,
                                  BeanDescription beanDescription) {
        super(delegate);
        this.entityManager = entityManager;
        this.beanDescription = beanDescription;
    }

    @Override
    protected JsonDeserializer<?> newDelegatingInstance(JsonDeserializer<?> newDelegatee) {
        return new EntityByIdDeserializer(newDelegatee, entityManager, beanDescription);
    }

    @Override
    public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {

        if (!beanDescription.getClassInfo().hasAnnotation(Entity.class)) {
            return super.deserialize(p, ctxt);
        }

        ObjectMapper mapper = (ObjectMapper) p.getCodec();
        TreeNode treeNode = p.readValueAsTree();
        String originalString = mapper.writeValueAsString(treeNode);

        JsonParser recreatedParser = mapper.getFactory().createParser(originalString);
        mapper.getDeserializationConfig().initialize(recreatedParser);
        // to avoid exception as the first token is null
        recreatedParser.nextToken();

        Object deserializedObject = super.deserialize(recreatedParser, ctxt);
        fillEntities(deserializedObject, treeNode);
        return deserializedObject;
    }

    private void fillEntities(Object deserializedObject, TreeNode treeNode) {
        for (AnnotatedField field : beanDescription.getClassInfo().fields()) {
            if (field.hasAnnotation(EntityByIdDeserialize.class)) {
                try {
                    String propertyName = field.getName();
                    TreeNode valueNode = treeNode.get(propertyName);
                    if (valueNode == null) {
                        continue;
                    }
                    EntityByIdDeserialize annotation = field.getAnnotation(EntityByIdDeserialize.class);
                    TreeNode idValue = valueNode.get(annotation.value());
                    if (idValue == null) {
                        continue;
                    }
                    String idAsString = ((TextNode) idValue).textValue();
                    UUID uuid = UUID.fromString(idAsString);
                    Object reference = entityManager.find(field.getRawType(), uuid);
                    ReflectionUtils.setField(field.getAnnotated(), deserializedObject, reference);
                } catch (Exception e) {
                    throw new RuntimeException("Failed to load entity using @EntityByIdDeserialize annotation in class "
                            + beanDescription.getClassInfo().getName() + " for field " + field, e);
                }
            }
        }
    }
}
