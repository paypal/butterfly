package com.paypal.butterfly.extensions.api.utilities;

import com.paypal.butterfly.extensions.api.ExecutionResult;
import com.paypal.butterfly.extensions.api.TUExecutionResult;
import com.paypal.butterfly.extensions.api.TransformationContext;
import com.paypal.butterfly.extensions.api.TransformationUtility;
import com.paypal.butterfly.extensions.api.exception.TransformationUtilityException;

import java.io.File;

/**
 * Aborts the transformation immediately.
 * An abort message may be specified. The abort
 * can also be conditional by using {@link #executeIf(String)}
 * or {@link #executeUnless(String)}
 *
 * @author facarvalho
 */
public class Abort extends TransformationUtility<Abort> {

    private static final String DESCRIPTION = "Abort the transformation";

    /**
     * This transformation utility abort the transformation.
     * A reason for abortion may be specified. The abortion
     * can also be conditional by using {@link #executeIf(String)}
     * or {@link #executeUnless(String)}
     */
    public Abort() {
        abortOnFailure(true);
    }

    /**
     * This transformation utility abort the transformation.
     * An abortion message may be specified. The abortion
     * can also be conditional by using {@link #executeIf(String)}
     * or {@link #executeUnless(String)}
     *
     * @param abortionMessage the reason to abort the transformation;
     */
    public Abort(String abortionMessage) {
        setAbortionMessage(abortionMessage);
    }

    /**
     * Set the reason to abort the transformation
     *
     * @param abortionMessage the reason to abort the transformation
     * @return this transformation utility instance
     */
    public Abort setAbortionMessage(String abortionMessage) {
        abortOnFailure(true, abortionMessage);
        return this;
    }

    @Override
    public String getDescription() {
        return String.format("%s", DESCRIPTION);
    }

    @Override
    protected ExecutionResult execution(File transformedAppFolder, TransformationContext transformationContext) {
        return TUExecutionResult.error(this, new TransformationUtilityException("Abort transformation utility has been executed"));
    }

}
