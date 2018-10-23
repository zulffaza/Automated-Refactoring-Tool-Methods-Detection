package com.finalproject.automated.refactoring.tool.methods.detection.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * @author fazazulfikapp
 * @version 1.0.0
 * @since 18 October 2018
 */

@Configuration
@EnableAsync
@Profile("!non-async")
public class AsyncConfig {
}
