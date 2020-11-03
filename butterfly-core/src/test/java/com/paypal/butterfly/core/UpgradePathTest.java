package com.paypal.butterfly.core;

import org.testng.annotations.Test;
import com.paypal.butterfly.extensions.springboot.SpringBootUpgrade_1_5_6_to_1_5_7;

import static org.testng.Assert.*;

/**
 * UpgradePath Test
 *
 * Created by Badal Sarkar on 10/29/2020
 */
public class UpgradePathTest {

    @Test
    public void testUpgradePathIsValid(){
        UpgradePath upgradePath = new UpgradePath(SpringBootUpgrade_1_5_6_to_1_5_7.class, "1.5.7");
        assertTrue(upgradePath instanceof UpgradePath);
    }

    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp= "The requested upgrade version \\(1.5.9\\) is inexistent")
    public void testUpgradePathNonExsists(){
        new UpgradePath(SpringBootUpgrade_1_5_6_to_1_5_7.class, "1.5.9");
    }

    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp= "The requested upgrade version \\(1.5.6\\) is the same as the version the application is currently at")
    public void testUpgradePathEqualsCurrentPath(){
        new UpgradePath(SpringBootUpgrade_1_5_6_to_1_5_7.class, "1.5.6");
    }

    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp= "The requested upgrade version \\(1.5.5\\) is older than the version the application is currently at \\(1.5.6\\)")
    public void testUpgradePathOlderThanCurrentVersion(){
        new UpgradePath(SpringBootUpgrade_1_5_6_to_1_5_7.class, "1.5.5");
    }
}
