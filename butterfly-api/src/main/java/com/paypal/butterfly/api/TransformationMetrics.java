package com.paypal.butterfly.api;

import com.paypal.butterfly.api.TransformationRequest;
import com.paypal.butterfly.api.TransformationResult;

/**
 * POJO containing metrics and statistics about the result of a transformation template execution.
 * One, or more (in case of an upgrade), transformation metric can object is retrieved
 * by calling {@link TransformationResult#getMetrics()}
 *
 * @author facarvalho
 */
public interface TransformationMetrics {

    /**
     * Returns the name of the transformation template whose execution generated these metrics.
     * Notice that this value might differ from {@link TransformationRequest#getTemplateName()},
     * in case the transformation was an upgrade, since one transformation metric object is
     * created per upgrade step.
     *
     * @return the name of the transformation template whose execution generated these metrics
     */
    String getTemplateName();

    /**
     * Returns the name of the transformation template class whose execution generated these metrics.
     * Notice that this value might differ from {@link TransformationRequest#getTemplateClassName()},
     * in case the transformation was an upgrade, since one transformation metric object is
     * created per upgrade step.
     *
     * @return the name of the transformation template class whose execution generated these metrics
     */
    String getTemplateClassName();

    /**
     * Returns the transformation conclusion date in "yyyyy-mm-dd hh:mm:ss"
     *
     * @return the transformation conclusion date in "yyyyy-mm-dd hh:mm:ss"
     */
    String getDateTime();

    /**
     * Returns the transformation conclusion date in milliseconds
     *
     * @return the transformation conclusion date in milliseconds
     */
    long getTimestamp();

    /**
     * Returns the version the application was upgraded from.
     * It returns null if the transformation template is not
     * an upgrade template
     *
     * @return the version the application was upgraded from.
     * It returns null if the transformation template is not
     * an upgrade template
     */
    String getFromVersion();

    /**
     * Returns the version the application was upgraded to.
     * It returns null if the transformation template is not
     * an upgrade template
     *
     * @return the version the application was upgraded to.
     * It returns null if the transformation template is not
     * an upgrade template
     */
    String getToVersion();

    /**
     * Returns true only if this particular transformation template has completed successfully.
     * Notice that even a successful transformation might have post-transformation
     * manual instructions, warnings or errors.
     * <br>
     * Notice that this method differs from {@link TransformationResult#isSuccessful()},
     * which refers to the result of the whole transformation, while this, in the case of an upgrade,
     * refers to the result of a particular upgrade step, since one transformation metric object is
     * created per upgrade step.
     * <br>
     * If transformation is not successful, details about why it aborted
     * can be retrieved by {@link TransformationResult#getAbortDetails()}
     *
     * @return true if the transformation was successful, or false, if it aborted.
     */
    boolean isSuccessful();

    /**
     * Returns true if this transformation requires
     * manual instructions to be completed. It returns
     * false otherwise, or if the transformation aborted.
     * <br>
     * Notice that this method differs from {@link TransformationResult#hasManualInstructions()},
     * which refers to the result of the whole transformation, while this, in the case of an upgrade,
     * refers to the result of a particular upgrade step, since one transformation metric object is
     * created per upgrade step.
     *
     * @return true if this transformation requires
     * manual instructions to be completed
     */
    boolean hasManualInstructions();

    /**
     * Returns transformation statistics
     *
     * @return transformation statistics
     */
    TransformationStatistics getStatistics();

}
