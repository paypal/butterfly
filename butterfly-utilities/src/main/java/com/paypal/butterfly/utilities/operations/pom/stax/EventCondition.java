package com.paypal.butterfly.utilities.operations.pom.stax;

import javax.xml.stream.events.XMLEvent;

interface EventCondition {

    boolean evaluateEvent(XMLEvent xmlEvent);

}
