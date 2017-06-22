package com.paypal.butterfly.extensions.api.conditions;

/**
 * Condition to determine if a transformation utility
 * should be executed or not. Every
 * MultipleUtilityCondition subclass result type must always
 * be boolean. The criteria to this type of condition
 * is based on multiple files (for example, when checking if at least one,
 * or all, of multiple properties files have a given property). For conditions
 * based on evaluating a single file see {@link SingleUtilityCondition}.
 * For conditions based on comparing two files see {@link DoubleUtilityCondition}
 *
 * IMPORTANT:
 * Every MultipleUtilityCondition subclass MUST be a Java bean, which means they must have
 * a public no arguments default constructor, and also public setters and getters for all
 * their properties. In addition to that, every setter must return the
 * MultipleUtilityCondition instance.
 *
 * @see SingleUtilityCondition
 * @see DoubleUtilityCondition
 *
 * @author facarvalho
 */
public abstract class MultipleUtilityCondition<MUC> extends UtilityCondition<MUC> {
}
