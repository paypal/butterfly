package com.paypal.butterfly.extensions.api.utilities;

import com.paypal.butterfly.extensions.api.TransformationUtility;

/**
 * Condition to determine if a transformation operation
 * should be executed or not
 *
 * IMPORTANT:
 * Every TransformationOperationCondition subclass MUST be a Java bean, which means they must have
 * a public no arguments default constructor, and also public setters and getters for all
 * their properties. In addition to that, every setter must return the
 * TransformationOperationCondition instance.
 *
 * @author facarvalho
 */
public abstract class TransformationOperationCondition<TOC> extends TransformationUtility<TOC> {
}
