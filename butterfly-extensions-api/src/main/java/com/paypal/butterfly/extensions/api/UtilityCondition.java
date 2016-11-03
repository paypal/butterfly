package com.paypal.butterfly.extensions.api;

import com.paypal.butterfly.extensions.api.TransformationUtility;

/**
 * Condition to determine if a transformation utility
 * should be executed or not
 *
 * IMPORTANT:
 * Every UtilityCondition subclass MUST be a Java bean, which means they must have
 * a public no arguments default constructor, and also public setters and getters for all
 * their properties. In addition to that, every setter must return the
 * UtilityCondition instance.
 *
 * @author facarvalho
 */
public abstract class UtilityCondition<TOC> extends TransformationUtility<TOC> {
}
