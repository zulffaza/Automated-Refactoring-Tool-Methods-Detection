package com.finalproject.automated.refactoring.tool.methods.detection.service.implementation;

import com.finalproject.automated.refactoring.tool.files.detection.model.FileModel;
import com.finalproject.automated.refactoring.tool.methods.detection.service.MethodsDetection;
import com.finalproject.automated.refactoring.tool.methods.detection.service.MethodsDetectionThread;
import com.finalproject.automated.refactoring.tool.methods.detection.service.util.MethodsDetectionUtil;
import com.finalproject.automated.refactoring.tool.model.MethodModel;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private MethodsDetectionUtil methodsDetectionUtil;

    @Override
    public List<MethodModel> detect(@NonNull FileModel fileModel) {
        return detect(Collections.singletonList(fileModel))
                .get(methodsDetectionUtil.getMethodKey(fileModel));
    }

    @Override
    public Map<String, List<MethodModel>> detect(@NonNull List<FileModel> fileModels) {
        Map<String, List<MethodModel>> result = Collections.synchronizedMap(new HashMap<>());
        doMethodsDetection(fileModels, result);

        return filterResult(result);
    }

    private void doMethodsDetection(List<FileModel> fileModels, Map<String, List<MethodModel>> result) {
        fileModels.parallelStream()
                .map(fileModel -> createEmptyMethodModels(fileModel, result))
                .forEach(fileModel -> methodsDetectionThread.detect(fileModel, result));
    }

    private FileModel createEmptyMethodModels(FileModel fileModel, Map<String, List<MethodModel>> result) {
        String key = methodsDetectionUtil.getMethodKey(fileModel);
        result.put(key, Collections.synchronizedList(new ArrayList<>()));

        return fileModel;
    }

    private Map<String, List<MethodModel>> filterResult(Map<String, List<MethodModel>> result) {
        return result.entrySet()
                .stream()
                .filter(this::isHavingValue)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private Boolean isHavingValue(Map.Entry<String, List<MethodModel>> resultEntry) {
        return !resultEntry.getValue()
                .isEmpty();
    }
}
