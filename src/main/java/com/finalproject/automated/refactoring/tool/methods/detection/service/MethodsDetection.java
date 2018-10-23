package com.finalproject.automated.refactoring.tool.methods.detection.service;

import com.finalproject.automated.refactoring.tool.files.detection.model.FileModel;
import com.finalproject.automated.refactoring.tool.model.MethodModel;
import lombok.NonNull;

import java.util.List;
import java.util.Map;

/**
 * @author fazazulfikapp
 * @version 1.0.0
 * @since 18 October 2018
 */

public interface MethodsDetection {

    List<MethodModel> detect(@NonNull FileModel fileModel);

    Map<String, List<MethodModel>> detect(@NonNull List<FileModel> fileModels);
}
