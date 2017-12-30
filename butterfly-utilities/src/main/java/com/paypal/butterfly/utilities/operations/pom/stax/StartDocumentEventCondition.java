package com.paypal.butterfly.utilities.operations.pom.stax;

import javax.xml.stream.events.XMLEvent;

public class StartDocumentEventCondition implements EventCondition {

    @Override
    public boolean evaluateEvent(XMLEvent xmlEvent) {
        return xmlEvent.isStartDocument();
    }

}
