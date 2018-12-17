package com.finalproject.automated.refactoring.tool.methods.detection.service.util.implementation;

import com.finalproject.automated.refactoring.tool.files.detection.model.FileModel;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Faza Zulfika P P
 * @version 1.0.0
 * @since 17 December 2018
 */

public class MethodsDetectionUtilImplTest {

    private MethodsDetectionUtilImpl methodsDetectionUtil;

    private FileModel fileModel;

    @Before
    public void setUp() {
        methodsDetectionUtil = new MethodsDetectionUtilImpl();
        fileModel = FileModel.builder()
                .path("path")
                .filename("Filename.java")
                .content("content")
                .build();
    }

    @Test
    public void getMethodKey_success() {
        String key = methodsDetectionUtil.getMethodKey(fileModel);
        String expectedKey = "path/Filename.java";

        assertEquals(expectedKey, key);
    }

    @Test(expected = NullPointerException.class)
    public void getMethodKey_failed_fileModelIsNull() {
        methodsDetectionUtil.getMethodKey(null);
    }
}