package com.finalproject.automated.refactoring.tool.methods.detection.service.implementation;

import com.finalproject.automated.refactoring.tool.files.detection.model.FileModel;
import com.finalproject.automated.refactoring.tool.methods.detection.service.MethodsDetection;
import com.finalproject.automated.refactoring.tool.methods.detection.service.MethodsDetectionThread;
import com.finalproject.automated.refactoring.tool.model.MethodModel;
import com.finalproject.automated.refactoring.tool.utils.service.ThreadsWatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * @author fazazulfikapp
 * @version 1.0.0
 * @since 18 October 2018
 */

@Service
public class JavaMethodsDetection implements MethodsDetection {

    @Autowired
    private MethodsDetectionThread methodsDetectionThread;

    @Autowired
    private ThreadsWatcher threadsWatcher;

    private static final Integer WAITING_TIME = 500;

    private static final String METHODS_REGEX = "(?:\\s)*(?:(\\w*)\\s*)?((?:\\()+(?:[\\w\\[\\],\\s])*(?:\\)))+(?:[\\w,\\s])*(\\{)+(?:\\s)*$";

    @Override
    public List<MethodModel> detect(FileModel fileModel) {
        return detect(Collections.singletonList(fileModel))
                .get(fileModel.getFilename());
    }

    @Override
    public Map<String, List<MethodModel>> detect(List<FileModel> fileModels) {
        Map<String, List<MethodModel>> result = Collections.synchronizedMap(new HashMap<>());
        List<Future> futures = new ArrayList<>();

        doMethodsDetection(fileModels, futures, result);
        threadsWatcher.waitAllThreadsDone(futures, WAITING_TIME);

        return result;
    }

    private void doMethodsDetection(List<FileModel> fileModels, List<Future> futures,
                                    Map<String, List<MethodModel>> result) {
        fileModels.forEach(fileModel ->
                doFileDetection(fileModel, futures, result));
    }

    private void doFileDetection(FileModel fileModel, List<Future> futures, Map<String, List<MethodModel>> result) {
        Future future = methodsDetectionThread.detect(fileModel, METHODS_REGEX, result);
        futures.add(future);
    }
}
