package com.paypal.butterfly.test;

import com.paypal.butterfly.core.ButterflyCoreConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(ButterflyCoreConfig.class)
public class ButterflyTestConfig {
}
