package dev.openfeature.sdk;

import static dev.openfeature.sdk.Value.objectToValue;

import dev.openfeature.sdk.exceptions.ValueNotConvertableError;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * {@link Structure} represents a potentially nested object type which is used to represent
 * structured data.
 */
@SuppressWarnings("PMD.BeanMembersShouldSerialize")
public interface Structure {

    /**
     * Boolean indicating if this structure is empty.
     *
     * @return boolean for emptiness
     */
    boolean isEmpty();

    /**
     * Get all keys.
     *
     * @return the set of keys
     */
    Set<String> keySet();

    /**
     * Get the value indexed by key.
     *
     * @param key String the key.
     * @return the Value
     */
    Value getValue(String key);

    /**
     * Get all values, as a map of Values.
     *
     * @return all attributes on the structure into a Map
     */
    Map<String, Value> asMap();

    /**
     * Get all values, as a map of Values.
     *
     * @return all attributes on the structure into a Map
     */
    Map<String, Value> asUnmodifiableMap();

    /**
     * Get all values, with as a map of Object.
     *
     * @return all attributes on the structure into a Map
     */
    Map<String, Object> asObjectMap();

    /**
     * Converts the Value into its equivalent primitive type.
     *
     * @param value - Value object to convert
     * @return an Object containing the primitive type, or null.
     */
    default Object convertValue(Value value) {

        if (value == null || value.isNull()) {
            return null;
        }

        if (value.isBoolean()) {
            return value.asBoolean();
        }

        if (value.isNumber() && !value.isNull()) {
            Number numberValue = (Number) value.asObject();
            if (numberValue instanceof Double) {
                return numberValue.doubleValue();
            } else if (numberValue instanceof Integer) {
                return numberValue.intValue();
            } else if (numberValue instanceof Long) {
                return numberValue.longValue();
            }
        }

        if (value.isString()) {
            return value.asString();
        }

        if (value.isInstant()) {
            return value.asInstant();
        }

        if (value.isList()) {
            List<Value> listValue = value.asList();
            List<Object> converted = new ArrayList<>(listValue.size());
            for (Value element : listValue) {
                converted.add(convertValue(element));
            }
            return converted;
        }

        if (value.isStructure()) {
            Map<String, Value> structureAttributes = value.asStructure().asUnmodifiableMap();
            Map<String, Object> converted = HashMapUtils.forEntries(structureAttributes.size());
            for (Entry<String, Value> entry : structureAttributes.entrySet()) {
                converted.put(entry.getKey(), convertValue(entry.getValue()));
            }
            return converted;
        }

        throw new ValueNotConvertableError();
    }

    /**
     * Transform an object map to a {@link Structure} type.
     *
     * @param map map of objects
     * @return a Structure object in the SDK format
     */
    static Structure mapToStructure(Map<String, Object> map) {
        Map<String, Value> attributes = HashMapUtils.forEntries(map.size());
        for (Entry<String, Object> entry : map.entrySet()) {
            attributes.put(entry.getKey(), objectToValue(entry.getValue()));
        }
        return new MutableStructure(attributes);
    }
}
