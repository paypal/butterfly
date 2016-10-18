package com.paypal.butterfly.extensions.api.operations;

import com.paypal.butterfly.extensions.api.TransformationOperation;

/**
 * Abstract class with {@link AddElement} implementation ready for TOs that want to be so.
 * After extending this class, all the TO class needs to do is check {@code ifPresent}
 * when deciding its result type in case the element to be added is already present
 *
 * @author facarvalho
 */
public abstract class AddElementTO<TO extends AddElementTO> extends TransformationOperation<TO> implements AddElement<TO> {

    @edu.umd.cs.findbugs.annotations.SuppressFBWarnings (value="URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD", justification="This property will be used by sub-classes")
    protected IfPresent ifPresent = IfPresent.Fail;

    @Override
    public TO failIfPresent() {
        ifPresent = IfPresent.Fail;
        return (TO) this;
    }

    @Override
    public TO warnNotAddIfPresent() {
        ifPresent = IfPresent.WarnNotAdd;
        return (TO) this;
    }

    @Override
    public TO warnButAddIfPresent() {
        ifPresent = IfPresent.WarnButAdd;
        return (TO) this;
    }

    @Override
    public TO noOpIfPresent() {
        ifPresent = IfPresent.NoOp;
        return (TO) this;
    }

    @Override
    public TO overwriteIfPresent() {
        ifPresent = IfPresent.Overwrite;
        return (TO) this;
    }

}
