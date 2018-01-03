package com.paypal.butterfly.extensions.api;

/**
 * Condition to determine if a transformation utility
 * should be executed or not. Although this type has no
 * explicitly defined structural additions to typical
 * {@link TransformationUtility} classes, every
 * UtilityCondition subclass result type must always
 * be boolean. The criteria to the condition can be
 * based on a single file (when checking if a particular
 * file contains a given word for example) or multiple files
 * (when comparing two files for example).
 *
 * IMPORTANT:
 * Every UtilityCondition subclass MUST be a Java bean, which means they must have
 * a public no arguments default constructor, and also public setters and getters for all
 * their properties. In addition to that, every setter must return the
 * UtilityCondition instance.
 *
 * @author facarvalho
 */
public abstract class UtilityCondition<U extends UtilityCondition> extends TransformationUtility<U> {
}
