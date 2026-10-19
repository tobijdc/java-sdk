package dev.openfeature.sdk;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

@SuppressWarnings({"PMD.BeanMembersShouldSerialize", "checkstyle:MissingJavadocType"})
abstract class AbstractStructure implements Structure {

    protected final Map<String, Value> attributes;

    @Override
    public boolean isEmpty() {
        return attributes == null || attributes.isEmpty();
    }

    AbstractStructure() {
        this.attributes = new HashMap<>();
    }

    AbstractStructure(Map<String, Value> attributes) {
        this.attributes = attributes;
    }

    /**
     * Returns an unmodifiable representation of the internal attribute map.
     *
     * @return immutable map
     */
    public Map<String, Value> asUnmodifiableMap() {
        if (attributes == null || attributes.isEmpty()) {
            return Collections.emptyMap();
        }
        return Collections.unmodifiableMap(attributes);
    }

    /**
     * Get all values as their underlying primitives types.
     *
     * @return all attributes on the structure into a Map
     */
    @Override
    public Map<String, Object> asObjectMap() {
        Map<String, Object> objectMap = HashMapUtils.forEntries(attributes.size());
        for (Entry<String, Value> entry : attributes.entrySet()) {
            objectMap.put(entry.getKey(), convertValue(entry.getValue()));
        }
        return objectMap;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof AbstractStructure)) {
            return false;
        }
        AbstractStructure that = (AbstractStructure) object;
        return Objects.equals(attributes, that.attributes);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(attributes);
    }
}
