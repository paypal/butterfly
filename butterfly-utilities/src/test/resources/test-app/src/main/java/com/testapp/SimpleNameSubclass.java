package com.testapp;

import java.util.logging.Logger;

public class SimpleNameSubclass extends Logger {

    protected SimpleNameSubclass(String name, String resourceBundleName) {
        super(name, resourceBundleName);
    }

}
