package com.tests.myapp.impl;

import com.tests.myapp.api.SampleResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("request")
public class SampleResourceImpl implements SampleResource {

    @Autowired
    public Hello hello;

    @Override
    public String sayHello() { 	
        return hello.sayHello();
    }

}
