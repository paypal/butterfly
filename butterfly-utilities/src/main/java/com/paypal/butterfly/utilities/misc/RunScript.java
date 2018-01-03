package com.paypal.butterfly.utilities.misc;

import com.paypal.butterfly.extensions.api.TUExecutionResult;
import com.paypal.butterfly.extensions.api.TransformationContext;
import com.paypal.butterfly.extensions.api.TransformationUtility;
import com.paypal.butterfly.extensions.api.exception.TransformationUtilityException;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Executes a script and saves the result after evaluating it.
 * The scripting language can be chosen, and "js" is the default one.
 * Additionally, one or more objects, and/or transformation context attributes,
 * can be used in the script during transformation time.
 *
 * @author facarvalho
 */
public class RunScript extends TransformationUtility<RunScript> {

    private static final String DESCRIPTION = "Executes script '%s' and saves its evaluation result";

    private String script;
    private String language = "js";
    private Map<String, String> attributes = new HashMap<>();
    private Map<String, Object> objects = new HashMap<>();

    /**
     * Executes a script and saves the result after evaluating it.
     * The scripting language can be chosen, and "js" is the default one.
     * Additionally, one or more objects, and/or transformation context attributes,
     * can be used in the script during transformation time.
     */
    public RunScript() {
    }

    /**
     * Executes a script and saves the result after evaluating it.
     * The scripting language can be chosen, and "js" is the default one.
     * Additionally, one or more objects, and/or transformation context attributes,
     * can be used in the script during transformation time.
     *
     * @param script the script to be executed and evaluated
     */
    public RunScript(String script) {
        setScript(script);
    }

    /**
     * Sets the script to be executed and evaluated
     *
     * @param script the script to be executed and evaluated
     * @return this utility instance
     */
    public RunScript setScript(String script) {
        checkForBlankString("script", script);
        this.script = script;
        return this;
    }

    public RunScript setLanguage(String language) {
        checkForEmptyString("script", script);
        this.language = language;
        return this;
    }

    public RunScript addAttribute(String key, String attributeName) {
        checkForBlankString("key", key);
        checkForBlankString("attributeName", attributeName);
        attributes.put(key, attributeName);
        return this;
    }

    public RunScript addObject(String key, Object object) {
        checkForBlankString("key", key);
        checkForNull("object", object);
        objects.put(key, object);
        return this;
    }

    public String getScript() {
        return script;
    }

    public String getLanguage() {
        return language;
    }

    public Map<String, String> getAttributes() {
        return Collections.unmodifiableMap(attributes);
    }

    public Map<String, Object> getObjects() {
        return Collections.unmodifiableMap(objects);
    }

    @Override
    public String getDescription() {
        return String.format(DESCRIPTION, script);
    }

    @Override
    protected TUExecutionResult execution(File transformedAppFolder, TransformationContext transformationContext) {
        TUExecutionResult result = null;

        try {
            ScriptEngineManager mgr = new ScriptEngineManager();
            ScriptEngine engine = mgr.getEngineByName(language);

            //No js engine for Open JDK 7 Version
            if(engine == null) {
                String exceptionMessage = String.format("Script engine named %s could not be found", language);
                TransformationUtilityException e = new TransformationUtilityException(exceptionMessage);
                result = TUExecutionResult.error(this, e);
                return result;
            }

            String key;
            Object value;

            // Adding attributes
            for (Object attributeKey : attributes.keySet().toArray()) {
                key = (String) attributeKey;
                value = transformationContext.get(attributes.get(key));
                engine.put(key, value);
            }

            // Adding objects
            for (Object objectKey : objects.keySet().toArray()) {
                key = (String) objectKey;
                value = objects.get(key);
                engine.put(key, value);
            }

            Object evalResult = engine.eval(script);
            if (evalResult != null) {
                result = TUExecutionResult.value(this, evalResult);
            } else {
                result = TUExecutionResult.nullResult(this);
            }
        } catch (ScriptException e) {
            result = TUExecutionResult.error(this, e);
        }

        return result;
    }

}
