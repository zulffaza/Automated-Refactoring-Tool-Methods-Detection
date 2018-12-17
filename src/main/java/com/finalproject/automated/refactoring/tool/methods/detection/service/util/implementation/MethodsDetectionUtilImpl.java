package com.finalproject.automated.refactoring.tool.methods.detection.service.util.implementation;

import com.finalproject.automated.refactoring.tool.files.detection.model.FileModel;
import com.finalproject.automated.refactoring.tool.methods.detection.service.util.MethodsDetectionUtil;
import lombok.NonNull;
import org.springframework.stereotype.Service;

/**
 * @author Faza Zulfika P P
 * @version 1.0.0
 * @since 17 December 2018
 */

@Service
public class MethodsDetectionUtilImpl implements MethodsDetectionUtil {

    private static final String KEY_DIVIDER = "/";

    @Override
    public String getMethodKey(@NonNull FileModel fileModel) {
        return fileModel.getPath() + KEY_DIVIDER + fileModel.getFilename();
    }
}
