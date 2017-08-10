/**
 * Multiple transformation utilities, organized by type, each one having its own subpackage.
 * Transformation utilities are executed against the "to be" transformed project folder, but they do NOT apply
 * any modification to the project folder. Instead, they just gather information about it, which is
 * saved in the transformation context, to then be used later by other utilities or by operations.
 *
 * @since 1.0.0
 */
package com.paypal.butterfly.utilities;