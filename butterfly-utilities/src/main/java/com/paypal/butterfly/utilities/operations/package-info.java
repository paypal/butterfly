/**
 * Multiple transformation operations, organized by type, each one having its own subpackage.
 * Transformation operation is a special type of transformation utility, which DOES apply modifications
 * to the project folder.
 * <br>
 * Differences between transformation utilities and transformation operations:
 * <ul>
 *     <li>Utilities never modify the application. Operations always do.</li>
 *     <li>Utilities usually return a value, but not necessarily. Operations never do.</li>
 *     <li>Utilities usually save its result meta-data object in the transformation context, but not necessarily. Operations always do.</li>
 *     <li>Operations allow multiple operations.</li>
 * </ul>
 *
 * @since 1.0.0
 */
package com.paypal.butterfly.utilities.operations;