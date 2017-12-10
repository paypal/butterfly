package com.paypal.butterfly.extensions.api;

/**
 * Transformation utility condition to determine if a transformation utility
 * should be executed or not, based on an one file criteria. Every
 * {@code SingleUtilityCondition} subclass result type must always
 * be boolean. The criteria to this type of condition
 * is based on evaluating a single file (when checking if a particular
 * file contains a given word for example). For conditions
 * based on comparing two files see {@link DoubleCondition}.
 * For conditions based on multiple files see {@link MultipleConditions}
 *
 *
 * @see DoubleCondition
 * @see MultipleConditions
 *
 * @author facarvalho
 */
public abstract class SingleCondition<T extends SingleCondition> extends UtilityCondition<T> {
}
