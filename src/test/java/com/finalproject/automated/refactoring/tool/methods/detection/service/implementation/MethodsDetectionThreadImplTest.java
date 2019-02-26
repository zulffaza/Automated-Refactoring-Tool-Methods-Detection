package com.finalproject.automated.refactoring.tool.methods.detection.service.implementation;

import com.finalproject.automated.refactoring.tool.files.detection.model.FileModel;
import com.finalproject.automated.refactoring.tool.methods.detection.model.IndexModel;
import com.finalproject.automated.refactoring.tool.methods.detection.service.MethodAnalysis;
import com.finalproject.automated.refactoring.tool.methods.detection.service.implementation.util.TestUtil;
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

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * @author fazazulfikapp
 * @version 1.0.0
 * @since 22 October 2018
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class MethodsDetectionThreadImplTest {

    @Autowired
    private MethodsDetectionThreadImpl methodsDetectionThread;

    @MockBean
    private MethodAnalysis methodAnalysis;

    private static final Integer INVOKED_ONCE = 1;

    private FileModel fileModel;

    private List<IndexModel> indexModels;

    @Before
    public void setUp() {
        fileModel = TestUtil.getFileModel();
        indexModels = createIndexModels();

        indexModels.forEach(this::stubMethodAnalysis);
    }

    @Test
    public void detect_success() {
        Map<String, List<MethodModel>> result = Collections.synchronizedMap(new HashMap<>());
        methodsDetectionThread.detect(fileModel, result);

        verifiesMethodAnalysis();
    }

    @Test(expected = NullPointerException.class)
    public void detect_failed_fileModelIsNull() {
        Map<String, List<MethodModel>> result = Collections.synchronizedMap(new HashMap<>());
        methodsDetectionThread.detect(null, result);
    }

    private void stubMethodAnalysis(IndexModel indexModel) {
        doNothing().when(methodAnalysis)
                .analysis(eq(fileModel), eq(indexModel), eq(Collections.synchronizedMap(new HashMap<>())));
    }

    private List<IndexModel> createIndexModels() {
        List<IndexModel> indexModels = new ArrayList<>();

        indexModels.add(IndexModel.builder()
                .start(155)
                .end(228)
                .build());

        indexModels.add(IndexModel.builder()
                .start(241)
                .end(360)
                .build());

        indexModels.add(IndexModel.builder()
                .start(447)
                .end(459)
                .build());

        indexModels.add(IndexModel.builder()
                .start(503)
                .end(535)
                .build());

        indexModels.add(IndexModel.builder()
                .start(586)
                .end(603)
                .build());

        indexModels.add(IndexModel.builder()
                .start(652)
                .end(694)
                .build());

        return indexModels;
    }

    private void verifiesMethodAnalysis() {
        indexModels.forEach(this::verifiyMethodAnalysis);
        verifyNoMoreInteractions(methodAnalysis);
    }

    private void verifiyMethodAnalysis(IndexModel indexModel) {
        verify(methodAnalysis, times(INVOKED_ONCE))
                .analysis(eq(fileModel), eq(indexModel), eq(Collections.synchronizedMap(new HashMap<>())));
    }
}