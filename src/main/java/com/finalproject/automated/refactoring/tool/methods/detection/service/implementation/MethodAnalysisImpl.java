package com.finalproject.automated.refactoring.tool.methods.detection.service.implementation;

import com.finalproject.automated.refactoring.tool.files.detection.model.FileModel;
import com.finalproject.automated.refactoring.tool.methods.detection.model.IndexModel;
import com.finalproject.automated.refactoring.tool.methods.detection.service.MethodAnalysis;
import com.finalproject.automated.refactoring.tool.model.MethodModel;
import com.finalproject.automated.refactoring.tool.model.PropertyModel;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.Future;

/**
 * @author fazazulfikapp
 * @version 1.0.0
 * @since 22 October 2018
 */

@Service
public class MethodAnalysisImpl implements MethodAnalysis {

    private static final String OPEN_BRACES = "{";
    private static final String OPEN_BRACES_DELIMITER = "\\" + OPEN_BRACES;
    private static final String CLOSE_BRACES_DELIMITER = "}";
    private static final String QOMMA_DELIMITER = ",";
    private static final String WHITESPACE_DELIMITER = "(?:\\s)+";
    private static final String EMPTY_STRING = "";

    private static final Integer FIRST_INDEX = 0;
    private static final Integer SECOND_INDEX = 1;

    @Override
    public Future analysis(FileModel fileModel, IndexModel indexModel, Map<String, List<MethodModel>> result) {
        MethodModel methodModel = MethodModel.builder().build();

        anaysisMethodAttributes(fileModel.getContent(), indexModel, methodModel);
        analysisMethodBody(fileModel.getContent(), indexModel, methodModel);
        addToResult(fileModel.getFilename(), methodModel, result);

        return null;
    }

    private void anaysisMethodAttributes(String content, IndexModel indexModel, MethodModel methodModel) {
        String methodDeclarations = content.substring(indexModel.getStart(), indexModel.getEnd());
        List<String> splitByFirstParentheses = Arrays.asList(methodDeclarations.split(OPEN_BRACES_DELIMITER));
        List<String> splitByLastParentheses = Arrays.asList(splitByFirstParentheses.get(SECOND_INDEX)
                .split(CLOSE_BRACES_DELIMITER));

        getKeywords(splitByFirstParentheses.get(FIRST_INDEX), methodModel);
        getParameters(splitByLastParentheses.get(FIRST_INDEX), methodModel);
        getExceptions(splitByLastParentheses.get(SECOND_INDEX), methodModel);
    }

    private void getKeywords(String keywords, MethodModel methodModel) {
        keywords = keywords.trim();

        List<String> words = Arrays.asList(keywords.split(QOMMA_DELIMITER));
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

        List<String> words = Arrays.asList(parameters.split(QOMMA_DELIMITER));

        words.stream()
                .map(String::trim)
                .map(parameter -> Arrays.asList(parameter.split(WHITESPACE_DELIMITER)))
                .map(splittedParameters ->
                        createPropertyModel(splittedParameters.get(FIRST_INDEX), splittedParameters.get(SECOND_INDEX)))
                .forEach(methodModel.getParameters()::add);
    }

    private PropertyModel createPropertyModel(String type, String name) {
        return PropertyModel.builder()
                .type(type)
                .name(name)
                .build();
    }

    private void getExceptions(String exceptions, MethodModel methodModel) {
        exceptions = exceptions.replace(OPEN_BRACES, EMPTY_STRING).trim();

        List<String> words = Arrays.asList(exceptions.split(QOMMA_DELIMITER));
        words.set(FIRST_INDEX, words.get(FIRST_INDEX).split(WHITESPACE_DELIMITER)[SECOND_INDEX]);

        words.stream()
                .map(String::trim)
                .forEach(methodModel.getExceptions()::add);
    }

    private void analysisMethodBody(String content, IndexModel indexModel, MethodModel methodModel) {
        Integer startBodyIndex = indexModel.getStart();

        String body = searchMethodBody(content, startBodyIndex);
        methodModel.setBody(body);
    }

    private String searchMethodBody(String content, Integer startBodyIndex) {
        Stack<Integer> stack = new Stack<>();
        Integer index;

        for (index = startBodyIndex; index < content.length(); index++) {
            String character = String.valueOf(content.charAt(index));

            switch (character) {
                case OPEN_BRACES:
                    stack.push(index);
                    break;
                case CLOSE_BRACES_DELIMITER:
                    startBodyIndex = stack.pop();
                    break;
            }

            if (stack.empty())
                break;
        }

        return content.substring(startBodyIndex + 1, index - 1).trim();
    }

    private void addToResult(String filename, MethodModel methodModel, Map<String, List<MethodModel>> result) {
        if (result.containsKey(filename))
            result.get(filename).add(methodModel);
        else {
            List<MethodModel> methodModels = new ArrayList<>();
            methodModels.add(methodModel);

            result.put(filename, methodModels);
        }
    }
}
