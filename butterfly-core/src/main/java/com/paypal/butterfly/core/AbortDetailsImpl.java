package com.paypal.butterfly.core;

import com.paypal.butterfly.metrics.AbortDetails;
import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 * POJO describing a transformation abort.
 *
 * @author facarvalho
 */
public class AbortDetailsImpl implements AbortDetails {

    private String utilityName;
    private String abortMessage;
    private String exceptionClass;
    private String exceptionMessage;
    private String exceptionStackTrace;

    public AbortDetailsImpl(Exception ex, String abortMessage, String utilityName) {
        if (ex == null) {
            throw new IllegalArgumentException("Exception object cannot be null");
        }
        if (utilityName == null || utilityName.trim().equals("")) {
            throw new IllegalArgumentException("Utility name cannot be null");
        }

        this.utilityName = utilityName;
        this.abortMessage = abortMessage;
        this.exceptionClass = ex.getClass().getSimpleName();
        this.exceptionMessage = ex.getMessage();
        this.exceptionStackTrace = ExceptionUtils.getStackTrace(ex);
    }

    @Override
    public String getUtilityName() {
        return utilityName;
    }

    @Override
    public String getAbortMessage() {
        return abortMessage;
    }

    @Override
    public String getExceptionClassName() {
        return exceptionClass;
    }

    @Override
    public String getExceptionMessage() {
        return exceptionMessage;
    }

    @Override
    public String getExceptionStackTrace() {
        return exceptionStackTrace;
    }

}