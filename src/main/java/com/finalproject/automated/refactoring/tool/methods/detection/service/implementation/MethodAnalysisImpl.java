package com.finalproject.automated.refactoring.tool.methods.detection.service.implementation;

import com.finalproject.automated.refactoring.tool.files.detection.model.FileModel;
import com.finalproject.automated.refactoring.tool.methods.detection.model.IndexModel;
import com.finalproject.automated.refactoring.tool.methods.detection.service.MethodAnalysis;
import com.finalproject.automated.refactoring.tool.model.MethodModel;
import com.finalproject.automated.refactoring.tool.model.PropertyModel;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * @author fazazulfikapp
 * @version 1.0.0
 * @since 22 October 2018
 */

@Service
public class MethodAnalysisImpl implements MethodAnalysis {

    @Override
    public Future analysis(FileModel fileModel, IndexModel indexModel, Map<String, List<MethodModel>> result) {
        MethodModel methodModel = MethodModel.builder().build();

        anaysisMethodAttributes(fileModel.getContent(), indexModel, methodModel);
        result.put(fileModel.getFilename(), null);

        return null;
    }

    private void anaysisMethodAttributes(String content, IndexModel indexModel, MethodModel methodModel) {
        String methodDeclarations = content.substring(indexModel.getStart(), indexModel.getEnd());
        List<String> splitByFirstParentheses = Arrays.asList(methodDeclarations.split("\\("));
        List<String> splitByLastParentheses = Arrays.asList(splitByFirstParentheses.get(1).split("\\)"));

        getKeywords(splitByFirstParentheses.get(0), methodModel);
        getParameters(splitByLastParentheses.get(0), methodModel);
        getExceptions(splitByLastParentheses.get(1), methodModel);
    }

    private void getKeywords(String keywords, MethodModel methodModel) {
        keywords = keywords.trim();

        List<String> words = Arrays.asList(keywords.split(","));
        Integer size = words.size();
        Integer maxKeywords = size - 2;

        words.stream()
                .limit(maxKeywords)
                .map(String::trim)
                .forEach(methodModel.getKeywords()::add);

        methodModel.setReturnType(words.get(maxKeywords).trim());
        methodModel.setName(words.get(maxKeywords + 1).trim());
    }

    private void getParameters(String parameters, MethodModel methodModel) {
        parameters = parameters.trim();

        List<String> words = Arrays.asList(parameters.split(","));

        words.stream()
                .map(String::trim)
                .map(parameter -> Arrays.asList(parameter.split("(?:\\s)+")))
                .map(splittedParameters ->
                        createPropertyModel(splittedParameters.get(0), splittedParameters.get(1)))
                .forEach(methodModel.getParameters()::add);
    }

    private PropertyModel createPropertyModel(String type, String name) {
        return PropertyModel.builder()
                .type(type)
                .name(name)
                .build();
    }

    private void getExceptions(String exceptions, MethodModel methodModel) {
        exceptions = exceptions.replace("{", "").trim();

        List<String> words = Arrays.asList(exceptions.split(","));
        words.set(0, words.get(0).split("(?:\\s)+")[1]);

        words.stream()
                .map(String::trim)
                .forEach(methodModel.getExceptions()::add);
    }
}
