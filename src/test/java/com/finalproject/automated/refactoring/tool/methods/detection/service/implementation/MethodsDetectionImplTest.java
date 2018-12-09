package com.finalproject.automated.refactoring.tool.methods.detection.service.implementation;

import com.finalproject.automated.refactoring.tool.files.detection.model.FileModel;
import com.finalproject.automated.refactoring.tool.methods.detection.service.MethodsDetectionThread;
import com.finalproject.automated.refactoring.tool.methods.detection.service.implementation.util.TestUtil;
import com.finalproject.automated.refactoring.tool.model.MethodModel;
import com.finalproject.automated.refactoring.tool.utils.service.ThreadsWatcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

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
    private ThreadsWatcher threadsWatcher;

    @Value("${threads.waiting.time}")
    private Integer waitingTime;

    private static final Integer NUMBER_OF_PATH = 3;

    private FileModel fileModel;

    @Before
    public void setUp() {
        Future future = TestUtil.getFutureExpectation();

        fileModel = FileModel.builder()
                .path("path")
                .filename("Filename.java")
                .content("content")
                .build();

        when(methodsDetectionThread.detect(eq(fileModel),
                eq(Collections.synchronizedMap(new HashMap<>())))).thenReturn(future);
        doNothing().when(threadsWatcher)
                .waitAllThreadsDone(eq(Collections.singletonList(future)), eq(waitingTime));
    }


    @Test
    public void detect_singlePath_success() {
        List<MethodModel> result = methodsDetection.detect(fileModel);
        assertNull(result);
    }

    @Test
    public void detect_multiPath_success() {
        Map<String, List<MethodModel>> result = methodsDetection.detect(
                Collections.nCopies(NUMBER_OF_PATH, fileModel));
        assertNotNull(result);
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
}