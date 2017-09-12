package com.paypal.butterfly.extensions.api.utilities;

import com.paypal.butterfly.extensions.api.ExecutionResult;
import com.paypal.butterfly.extensions.api.TUExecutionResult;
import com.paypal.butterfly.extensions.api.TransformationContext;
import com.paypal.butterfly.extensions.api.TransformationUtility;
import com.paypal.butterfly.extensions.api.exception.TransformationUtilityException;

import java.io.File;
import java.util.Map;

/**
 * Obtains a specific entry from a {@link Map} object stored in the {@link TransformationContext},
 * and store its value as a new attribute in the transformation context. The name of the transformation
 * context attribute that holds the map object, and the key used to get the map entry, have to be specified.
 * <br>
 * The new attribute saved into the transformation context is named based on the utility name, unless
 * {@link #setContextAttributeName(String)} is called, as usual, nothing new here.
 *
 * @author facarvalho
 */
public class MapValue extends TransformationUtility<MapValue> {

    private static final String DESCRIPTION = "Get the entry's value whose key is %s in map %s";

    // The name of the transformation context attribute that holds the map object
    private String mapName;

    // The key used to get the map entry
    private Object key;

    /**
     * This utility obtains a specific entry from a {@link Map} object stored in the transformation context,
     * and store its value as a new attribute in the transformation context. The name of the transformation
     * context attribute that holds the map object, and the key used to get the map entry, have to be specified.
     * <br>
     * The new attribute saved into the transformation context is named based on the utility name, unless
     * {@link #setContextAttributeName(String)} is called, as usual, nothing new here.
     */
    public MapValue() {
    }

    /**
     * This utility obtains a specific entry from a {@link Map} object stored in the transformation context,
     * and store its value as a new attribute in the transformation context. The name of the transformation
     * context attribute that holds the map object, and the key used to get the map entry, have to be specified.
     * <br>
     * The new attribute saved into the transformation context is named based on the utility name, unless
     * {@link #setContextAttributeName(String)} is called, as usual, nothing new here.
     *
     * @param mapName the name of the transformation context attribute that holds the map object
     * @param key the key used to get the map entry
     */
    public MapValue(String mapName, Object key) {
        setMapName(mapName);
        setKey(key);
    }

    /**
     * Set the name of the transformation context attribute that holds the map object
     *
     * @param mapName the name of the transformation context attribute that holds the map object
     * @return this utility instance
     */
    public MapValue setMapName(String mapName) {
        checkForBlankString("mapName", mapName);
        this.mapName = mapName;
        return this;
    }

    /**
     * Set the key used to get the map entry
     *
     * @param key the key used to get the map entry
     * @return this utility instance
     */
    public MapValue setKey(Object key) {
        checkForNull("key", key);
        this.key = key;
        return this;
    }

    /**
     * Return the name of the transformation context attribute that holds the map object
     *
     * @return the name of the transformation context attribute that holds the map object
     */
    public String getMapName() {
        return mapName;
    }

    /**
     * Returns he key used to get the map entry
     *
     * @return the key used to get the map entry
     */
    public Object getKey() {
        return key;
    }

    @Override
    public String getDescription() {
        return String.format(DESCRIPTION, key, mapName);
    }

    @Override
    protected ExecutionResult execution(File transformedAppFolder, TransformationContext transformationContext) {
        ExecutionResult executionResult;

        Map<Object, ?> map = (Map<Object, ?>) transformationContext.get(mapName);
        if (map == null) {
            TransformationUtilityException e = new TransformationUtilityException("There is not a transformation context attribute named " + mapName);
            executionResult = TUExecutionResult.error(this, e);
        } else {
            if (!map.containsKey(key)) {
                String exceptionMessage = String.format("Map %s does not contain key %s", mapName, key);
                TransformationUtilityException e = new TransformationUtilityException(exceptionMessage);
                executionResult = TUExecutionResult.error(this, e);
            } else {
                Object value = map.get(key);
                executionResult = TUExecutionResult.value(this, value);
            }
        }

        return executionResult;
    }

}
