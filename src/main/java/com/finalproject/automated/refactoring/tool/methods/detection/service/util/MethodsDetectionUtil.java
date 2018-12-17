package com.finalproject.automated.refactoring.tool.methods.detection.service.util;

import com.finalproject.automated.refactoring.tool.files.detection.model.FileModel;
import lombok.NonNull;

/**
 * @author Faza Zulfika P P
 * @version 1.0.0
 * @since 17 December 2018
 */

public interface MethodsDetectionUtil {

    String getMethodKey(@NonNull FileModel fileModel);
}
