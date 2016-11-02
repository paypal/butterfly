package com.paypal.butterfly.utilities.script;

import com.paypal.butterfly.extensions.api.ExecutionResult;
import com.paypal.butterfly.extensions.api.TUExecutionResult;
import com.paypal.butterfly.extensions.api.TransformationContext;
import com.paypal.butterfly.extensions.api.TransformationUtility;
import com.paypal.butterfly.extensions.api.exception.TransformationDefinitionException;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.File;

/**
 * This utility executes a script and saves the result after evaluating.
 * The scripting language can be chosen, and Java is the default one.
 * Additionally, one or more transformation context attributes can be set in the script
 * during transformation time. Transformation context attributes placeholders can be put
 * in the script using "{}".
 *
 * @author facarvalho
 */
public class RunScript extends TransformationUtility<RunScript> {

    private static final String DESCRIPTION = "Executes script '%s' and saves its evaluation result";

    private static final String KEY_NAME = "a%s";

    private String script;
    private String language = "js";
    private String[] attributeNames;

    public RunScript() {
    }

    public RunScript(String script) {
        setScript(script);
    }

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

    public RunScript setAttributeNames(String... attributeNames) {
        if(attributeNames == null || attributeNames.length == 0){
            throw new TransformationDefinitionException("Attribute names cannot be null or empty");
        }
        this.attributeNames = attributeNames;
        return this;
    }

    public String getScript() {
        return script;
    }

    public String getLanguage() {
        return language;
    }

    public String[] getAttributeNames() {
        return attributeNames.clone();
    }

    @Override
    public String getDescription() {
        return String.format(DESCRIPTION, script);
    }

    @Override
    protected ExecutionResult execution(File transformedAppFolder, TransformationContext transformationContext) {
        TUExecutionResult result = null;

        try {
            ScriptEngineManager mgr = new ScriptEngineManager();
            ScriptEngine engine = mgr.getEngineByName(language);

            String key;
            Object value;

            for (int i = 0; i < attributeNames.length; i++) {
                key = String.format(KEY_NAME, i + 1);
                value = transformationContext.get(attributeNames[i]);
                engine.put(key, value);
            }

            Object evalResult = engine.eval(script);
            result = TUExecutionResult.value(this, evalResult);
        } catch (ScriptException e) {
            result = TUExecutionResult.error(this, e);
        }

        return result;
    }

}
