package com.finalproject.automated.refactoring.tool.methods.detection.service.implementation;

import com.finalproject.automated.refactoring.tool.files.detection.model.FileModel;
import com.finalproject.automated.refactoring.tool.methods.detection.service.MethodsDetectionThread;
import com.finalproject.automated.refactoring.tool.methods.detection.service.implementation.util.TestUtil;
import com.finalproject.automated.refactoring.tool.methods.detection.service.util.MethodsDetectionUtil;
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
import static org.mockito.ArgumentMatchers.anyList;
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
    private ThreadsWatcher threadsWatcher;

    @MockBean
    private MethodsDetectionUtil methodsDetectionUtil;

    @Value("${threads.waiting.time}")
    private Integer waitingTime;

    private static final Integer NUMBER_OF_PATH = 3;
    private static final Integer INVOKED_ONCE = 1;

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
        when(methodsDetectionUtil.getMethodKey(eq(fileModel)))
                .thenReturn("");
    }


    @Test
    public void detect_singlePath_success() {
        List<MethodModel> result = methodsDetection.detect(fileModel);
        assertNull(result);

        verifyMethodsDetectionThread(INVOKED_ONCE);
        verifyThreadsWatcher();
        verifyMethodsDetectionUtil();
    }

    @Test
    public void detect_multiPath_success() {
        Map<String, List<MethodModel>> result = methodsDetection.detect(
                Collections.nCopies(NUMBER_OF_PATH, fileModel));
        assertNotNull(result);

        verifyMethodsDetectionThread(NUMBER_OF_PATH);
        verifyThreadsWatcher();
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

    private void verifyMethodsDetectionThread(Integer invocationsTimes) {
        verify(methodsDetectionThread, times(invocationsTimes))
                .detect(eq(fileModel), eq(Collections.synchronizedMap(new HashMap<>())));
        verifyNoMoreInteractions(methodsDetectionThread);
    }

    private void verifyThreadsWatcher() {
        verify(threadsWatcher, times(INVOKED_ONCE))
                .waitAllThreadsDone(anyList(), eq(waitingTime));
        verifyNoMoreInteractions(threadsWatcher);
    }

    private void verifyMethodsDetectionUtil() {
        verify(methodsDetectionUtil, times(INVOKED_ONCE))
                .getMethodKey(eq(fileModel));
        verifyNoMoreInteractions(methodsDetectionUtil);
    }
}