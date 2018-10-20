package com.finalproject.automated.refactoring.tool.methods.detection.service.implementation;

import com.finalproject.automated.refactoring.tool.files.detection.model.FileModel;
import com.finalproject.automated.refactoring.tool.methods.detection.service.MethodsDetection;
import com.finalproject.automated.refactoring.tool.model.MethodModel;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author fazazulfikapp
 * @version 1.0.0
 * @since 18 October 2018
 */

@Service
public class JavaMethodsDetection implements MethodsDetection {

    private static final Integer WAITING_TIME = 500;

    private static final String METHODS_REGEX = "^(?:\\s)*(?:((?:public)|(?:private)|(?:protected)|(?:static)|(?:final)|(?:native)|(?:synchronized)|(?:abstract)|(?:threadsafe)|(?:transient))+\\s)*(?:(\\w*)\\s*)+((?:\\()+(?:\\w|\\[|\\]|,|\\s)*(?:\\)))+(?:\\s)*(\\{)?(?:\\s)*$";

    @Override
    public List<MethodModel> detect(FileModel fileModel) {
        return detect(Collections.singletonList(fileModel))
                .get(fileModel.getFilename());
    }

    @Override
    public Map<String, List<MethodModel>> detect(List<FileModel> fileModels) {
        return null;
    }
}
