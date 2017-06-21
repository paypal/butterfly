package com.paypal.butterfly.extensions.api.conditions;

/**
 * Condition to determine if a transformation utility
 * should be executed or not. Every
 * SingleUtilityCondition subclass result type must always
 * be boolean. The criteria to this type of condition
 * is based on evaluating a single file (when checking if a particular
 * file contains a given word for example). For conditions
 * based on comparing two files see {@link DoubleUtilityCondition}.
 * For conditions based on multiple files see {@link MultipleUtilityCondition}
 *
 * IMPORTANT:
 * Every SingleUtilityCondition subclass MUST be a Java bean, which means they must have
 * a public no arguments default constructor, and also public setters and getters for all
 * their properties. In addition to that, every setter must return the
 * SingleUtilityCondition instance.
 *
 * @see DoubleUtilityCondition
 * @see MultipleUtilityCondition
 *
 * @author facarvalho
 */
public abstract class SingleUtilityCondition<SUC> extends UtilityCondition<SUC> {
}
