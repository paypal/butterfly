/**
 * Multiple transformation utility conditions, organized by type, each one having its own subpackage.
 * Transformation utility conditions are used to determine if a specific transformation utility
 * should be executed or not. Transformation utility condition is a special type of transformation utility that
 * always result in a boolean. The criteria to its condition can be based on a single file (when checking if a particular
 * file contains a given word for example) or multiple files (when comparing two files for example).
 *
 * @see com.paypal.butterfly.extensions.api.SingleCondition
 * @see com.paypal.butterfly.extensions.api.DoubleCondition
 * @since 1.0.0
 */
package com.paypal.butterfly.utilities.conditions;