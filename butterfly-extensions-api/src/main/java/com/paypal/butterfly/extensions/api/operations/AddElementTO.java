package com.paypal.butterfly.extensions.api.operations;

import com.paypal.butterfly.extensions.api.TransformationOperation;

/**
 * Convenience class with {@link AddElement} implementation ready for {@link TransformationOperation} subclasses.
 * Protected instance variable {@code ifPresent} can be used when deciding the result type,
 * in case the element to be added is already present.
 *
 * @author facarvalho
 */
public abstract class AddElementTO<T extends AddElementTO> extends TransformationOperation<T> implements AddElement<T> {

    @edu.umd.cs.findbugs.annotations.SuppressFBWarnings (value="URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD", justification="This property will be used by sub-classes")
    protected IfPresent ifPresent = IfPresent.Fail;

    @Override
    public T failIfPresent() {
        ifPresent = IfPresent.Fail;
        return (T) this;
    }

    @Override
    public T warnNotAddIfPresent() {
        ifPresent = IfPresent.WarnNotAdd;
        return (T) this;
    }

    @Override
    public T warnButAddIfPresent() {
        ifPresent = IfPresent.WarnButAdd;
        return (T) this;
    }

    @Override
    public T noOpIfPresent() {
        ifPresent = IfPresent.NoOp;
        return (T) this;
    }

    @Override
    public T overwriteIfPresent() {
        ifPresent = IfPresent.Overwrite;
        return (T) this;
    }

}
