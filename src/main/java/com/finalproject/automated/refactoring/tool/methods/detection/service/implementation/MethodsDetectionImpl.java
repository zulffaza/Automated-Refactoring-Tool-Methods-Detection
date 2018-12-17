package com.finalproject.automated.refactoring.tool.methods.detection.service.implementation;

import com.finalproject.automated.refactoring.tool.files.detection.model.FileModel;
import com.finalproject.automated.refactoring.tool.methods.detection.service.MethodsDetection;
import com.finalproject.automated.refactoring.tool.methods.detection.service.MethodsDetectionThread;
import com.finalproject.automated.refactoring.tool.methods.detection.service.util.MethodsDetectionUtil;
import com.finalproject.automated.refactoring.tool.model.MethodModel;
import com.finalproject.automated.refactoring.tool.utils.service.ThreadsWatcher;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * @author fazazulfikapp
 * @version 1.0.0
 * @since 18 October 2018
 */

@Service
public class MethodsDetectionImpl implements MethodsDetection {

    @Autowired
    private MethodsDetectionThread methodsDetectionThread;

    @Autowired
    private ThreadsWatcher threadsWatcher;

    @Autowired
    private MethodsDetectionUtil methodsDetectionUtil;

    @Value("${threads.waiting.time}")
    private Integer waitingTime;

    @Override
    public List<MethodModel> detect(@NonNull FileModel fileModel) {
        return detect(Collections.singletonList(fileModel))
                .get(methodsDetectionUtil.getMethodKey(fileModel));
    }

    @Override
    public Map<String, List<MethodModel>> detect(@NonNull List<FileModel> fileModels) {
        Map<String, List<MethodModel>> result = Collections.synchronizedMap(new HashMap<>());
        List<Future> threads = doMethodsDetection(fileModels, result);

        threadsWatcher.waitAllThreadsDone(threads, waitingTime);

        return result;
    }

    private List<Future> doMethodsDetection(List<FileModel> fileModels, Map<String, List<MethodModel>> result) {
        return fileModels.stream()
                .map(fileModel -> methodsDetectionThread.detect(fileModel, result))
                .collect(Collectors.toList());
    }
}
