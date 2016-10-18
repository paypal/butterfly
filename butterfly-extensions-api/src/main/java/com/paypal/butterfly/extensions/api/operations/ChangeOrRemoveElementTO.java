package com.paypal.butterfly.extensions.api.operations;

import com.paypal.butterfly.extensions.api.TransformationOperation;

/**
 * Abstract class with {@link ChangeOrRemoveElement} implementation ready for TOs that want to be so.
 * After extending this class, all the TO class needs to do is check {@code ifNotPresent}
 * when deciding its result type in case the element to be changed or removed is not present
 *
 * @author facarvalho
 */
public abstract class ChangeOrRemoveElementTO<TO extends ChangeOrRemoveElementTO> extends TransformationOperation<TO> implements ChangeOrRemoveElement<TO> {

    @edu.umd.cs.findbugs.annotations.SuppressFBWarnings (value="URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD", justification="This property will be used by sub-classes")
    protected IfNotPresent ifNotPresent = IfNotPresent.Fail;

    @Override
    public TO failIfNotPresent() {
        ifNotPresent = IfNotPresent.Fail;
        return (TO) this;
    }

    @Override
    public TO warnIfNotPresent() {
        ifNotPresent = IfNotPresent.Warn;
        return (TO) this;
    }

    @Override
    public TO noOpIfNotPresent() {
        ifNotPresent = IfNotPresent.NoOp;
        return (TO) this;
    }

}
