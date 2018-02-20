package com.paypal.butterfly.utilities.operations.pom.stax;

import javax.xml.stream.events.XMLEvent;

public class EndElementEventCondition implements EventCondition {

    private String tagName;

    public EndElementEventCondition(String tagName) {
        this.tagName = tagName;
    }

    @Override
    public boolean evaluateEvent(XMLEvent xmlEvent) {
        return xmlEvent.isEndElement() && xmlEvent.asEndElement().getName().getLocalPart().equals(tagName);
    }

}
