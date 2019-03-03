package com.finalproject.automated.refactoring.tool.methods.detection.service.implementation;

import com.finalproject.automated.refactoring.tool.files.detection.model.FileModel;
import com.finalproject.automated.refactoring.tool.methods.detection.service.MethodsDetectionThread;
import com.finalproject.automated.refactoring.tool.methods.detection.service.util.MethodsDetectionUtil;
import com.finalproject.automated.refactoring.tool.model.MethodModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * @author fazazulfikapp
 * @version 1.0.0
 * @since 22 October 2018
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class MethodsDetectionImplTest {

    @Autowired
    private MethodsDetectionImpl methodsDetection;

    @MockBean
    private MethodsDetectionThread methodsDetectionThread;

    @MockBean
    private MethodsDetectionUtil methodsDetectionUtil;

    private static final Integer NUMBER_OF_PATH = 3;
    private static final Integer INVOKED_ONCE = 1;

    private FileModel fileModel;

    @Before
    public void setUp() {
        fileModel = FileModel.builder()
                .path("path")
                .filename("Filename.java")
                .content("content")
                .build();

        doNothing().when(methodsDetectionThread)
                .detect(eq(fileModel), eq(createEmptyMethodModels()));
        when(methodsDetectionUtil.getMethodKey(eq(fileModel)))
                .thenReturn("");
    }

    @Test
    public void detect_singlePath_success() {
        List<MethodModel> result = methodsDetection.detect(fileModel);
        assertNull(result);

        verifyMethodsDetectionThread(INVOKED_ONCE);
        verifyMethodsDetectionUtil();
    }

    @Test
    public void detect_multiPath_success() {
        Map<String, List<MethodModel>> result = methodsDetection.detect(
                Collections.nCopies(NUMBER_OF_PATH, fileModel));
        assertNotNull(result);

        verifyMethodsDetectionThread(NUMBER_OF_PATH);
    }

    @Test(expected = NullPointerException.class)
    public void detect_singlePath_failed_pathIsNull() {
        fileModel = null;
        methodsDetection.detect(fileModel);
    }

    @Test(expected = NullPointerException.class)
    public void detect_multiPath_failed_listOfPathIsNull() {
        List<FileModel> fileModels = null;
        methodsDetection.detect(fileModels);
    }

    private Map<String, List<MethodModel>> createEmptyMethodModels() {
        Map<String, List<MethodModel>> result = Collections.synchronizedMap(new HashMap<>());
        result.put("", Collections.synchronizedList(new ArrayList<>()));

        return new ConcurrentHashMap<>();
    }

    private void verifyMethodsDetectionThread(Integer invocationsTimes) {
        verify(methodsDetectionThread, times(invocationsTimes))
                .detect(eq(fileModel), eq(createEmptyMethodModels()));
        verifyNoMoreInteractions(methodsDetectionThread);
    }

    private void verifyMethodsDetectionUtil() {
        verify(methodsDetectionUtil, times(INVOKED_ONCE))
                .getMethodKey(eq(fileModel));
        verifyNoMoreInteractions(methodsDetectionUtil);
    }
}