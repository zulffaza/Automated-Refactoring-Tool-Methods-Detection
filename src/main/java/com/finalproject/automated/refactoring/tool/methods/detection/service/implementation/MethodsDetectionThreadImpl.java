package com.finalproject.automated.refactoring.tool.methods.detection.service.implementation;

import com.finalproject.automated.refactoring.tool.files.detection.model.FileModel;
import com.finalproject.automated.refactoring.tool.methods.detection.model.IndexModel;
import com.finalproject.automated.refactoring.tool.methods.detection.service.MethodAnalysis;
import com.finalproject.automated.refactoring.tool.methods.detection.service.MethodsDetectionThread;
import com.finalproject.automated.refactoring.tool.model.MethodModel;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author fazazulfikapp
 * @version 1.0.0
 * @since 18 October 2018
 */

@Service
public class MethodsDetectionThreadImpl implements MethodsDetectionThread {

    @Autowired
    private MethodAnalysis methodAnalysis;

    @Value("${methods.detection.regex}")
    private String methodsRegex;

    @Override
    public void detect(@NonNull FileModel fileModel, @NonNull Map<String, List<MethodModel>> result) {
        List<IndexModel> indexOfMethods = getIndexOfMethods(fileModel.getContent());
        doAnalysisMethods(indexOfMethods, fileModel, result);
    }

    private List<IndexModel> getIndexOfMethods(String content) {
        Pattern pattern = Pattern.compile(methodsRegex, Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(content);

        return findIndex(matcher);
    }

    private List<IndexModel> findIndex(Matcher matcher) {
        List<IndexModel> indexModels = new ArrayList<>();

        while (matcher.find()) {
            saveIndex(matcher, indexModels);
        }

        return indexModels;
    }

    private void saveIndex(Matcher matcher, List<IndexModel> indexModels) {
        IndexModel indexModel = IndexModel.builder()
                .start(matcher.start())
                .end(matcher.end())
                .build();

        indexModels.add(indexModel);
    }

    private void doAnalysisMethods(List<IndexModel> indexOfMethods, FileModel fileModel,
                                   Map<String, List<MethodModel>> result) {
        indexOfMethods.parallelStream()
                .forEach(indexOfMethod -> methodAnalysis.analysis(fileModel, indexOfMethod, result));
    }
}
