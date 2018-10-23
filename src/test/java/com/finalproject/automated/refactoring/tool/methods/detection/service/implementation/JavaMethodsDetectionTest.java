package com.finalproject.automated.refactoring.tool.methods.detection.service.implementation;

import com.finalproject.automated.refactoring.tool.files.detection.model.FileModel;
import com.finalproject.automated.refactoring.tool.methods.detection.service.MethodsDetectionThread;
import com.finalproject.automated.refactoring.tool.model.MethodModel;
import com.finalproject.automated.refactoring.tool.utils.service.ThreadsWatcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

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
public class JavaMethodsDetectionTest {

    @Autowired
    private JavaMethodsDetection methodsDetection;

    @MockBean
    private MethodsDetectionThread methodsDetectionThread;

    @MockBean
    private ThreadsWatcher threadsWatcher;

    private static final Integer NUMBER_OF_PATH = 3;
    private static final Integer WAITING_TIME = 500;

    private static final String METHODS_REGEX = "^(?:\\s)*(?:((?:public)|(?:private)|(?:protected)|(?:static)|(?:final)|(?:native)|(?:synchronized)|(?:abstract)|(?:threadsafe)|(?:transient))+\\s)*(?:(\\w*)\\s*)+((?:\\()+(?:\\w|\\[|\\]|,|\\s)*(?:\\)))+(?:\\s)*(\\{)?(?:\\s)*$";

    private FileModel fileModel;

    @Before
    public void setUp() {
        Future future = new Future() {

            @Override
            public boolean cancel(boolean mayInterruptIfRunning) {
                return false;
            }

            @Override
            public boolean isCancelled() {
                return false;
            }

            @Override
            public boolean isDone() {
                return true;
            }

            @Override
            public Object get() {
                return null;
            }

            @Override
            public Object get(long timeout, TimeUnit unit) {
                return null;
            }
        };

        fileModel = FileModel.builder()
                .path("path")
                .filename("filename")
                .content("content")
                .build();

        when(methodsDetectionThread.detect(eq(fileModel), eq(METHODS_REGEX),
                eq(Collections.synchronizedMap(new HashMap<>())))).thenReturn(future);
        doNothing().when(threadsWatcher)
                .waitAllThreadsDone(eq(Collections.singletonList(future)), eq(WAITING_TIME));
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