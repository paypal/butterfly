package com.paypal.butterfly.utilities.operations.pom.stax;

import javax.xml.stream.events.XMLEvent;

public class StartElementEventCondition implements EventCondition {

    private String tagName;

    public StartElementEventCondition(String tagName) {
        this.tagName = tagName;
    }

    @Override
    public boolean evaluateEvent(XMLEvent xmlEvent) {
        return xmlEvent.isStartElement() && xmlEvent.asStartElement().getName().getLocalPart().equals(tagName);
    }

}
