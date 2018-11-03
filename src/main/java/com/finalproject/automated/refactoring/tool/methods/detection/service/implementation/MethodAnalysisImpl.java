package com.finalproject.automated.refactoring.tool.methods.detection.service.implementation;

import com.finalproject.automated.refactoring.tool.files.detection.model.FileModel;
import com.finalproject.automated.refactoring.tool.methods.detection.model.IndexModel;
import com.finalproject.automated.refactoring.tool.methods.detection.service.MethodAnalysis;
import com.finalproject.automated.refactoring.tool.model.MethodModel;
import com.finalproject.automated.refactoring.tool.model.PropertyModel;
import org.springframework.scheduling.annotation.Async;
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

    private static final String OPEN_BRACES_DELIMITER = "{";
    private static final String CLOSE_BRACES_DELIMITER = "}";
    private static final String SEMICOLON_DELIMITER = ";";
    private static final String OPEN_PARENTHESES_DELIMITER = "\\(";
    private static final String CLOSE_PARENTHESES_DELIMITER = "\\)";
    private static final String QOMMA_DELIMITER = ",";
    private static final String POINT_DELIMITER = "\\.";
    private static final String WHITESPACE_DELIMITER = "(?:\\s)+";
    private static final String EMPTY_STRING = "";

    private static final Integer FIRST_INDEX = 0;
    private static final Integer SECOND_INDEX = 1;
    private static final Integer ONE_RESERVED_WORDS = 1;
    private static final Integer TWO_RESERVED_WORDS = 2;

    @Async
    @Override
    public Future analysis(FileModel fileModel, IndexModel indexModel, Map<String, List<MethodModel>> result) {
        MethodModel methodModel = MethodModel.builder().build();

        extendStartIndex(fileModel.getContent(), indexModel);
        anaysisMethodAttributes(fileModel, indexModel, methodModel);
        analysisMethodBody(fileModel.getContent(), indexModel, methodModel);
        addToResult(fileModel.getFilename(), methodModel, result);

        return null;
    }

    private void extendStartIndex(String content, IndexModel indexModel) {
        Integer index = indexModel.getStart();

        // TODO check if its a annotation parameters

        while (!isEOF(content.charAt(index)))
            index--;

        indexModel.setStart(++index);
    }

    private Boolean isEOF(Character character) {
        String string = String.valueOf(character);
        return string.equals(OPEN_BRACES_DELIMITER) || string.equals(CLOSE_BRACES_DELIMITER) ||
                string.equals(SEMICOLON_DELIMITER);
    }

    private void anaysisMethodAttributes(FileModel fileModel, IndexModel indexModel, MethodModel methodModel) {
        String methodDeclarations = fileModel.getContent()
                .substring(indexModel.getStart(), indexModel.getEnd()).trim();

        List<String> splitByFirstParentheses = Arrays.asList(methodDeclarations.split(OPEN_PARENTHESES_DELIMITER));
        List<String> splitByLastParentheses = Arrays.asList(splitByFirstParentheses.get(SECOND_INDEX)
                .split(CLOSE_PARENTHESES_DELIMITER));

        getKeywords(fileModel.getFilename(), splitByFirstParentheses.get(FIRST_INDEX), methodModel);
        getParameters(splitByLastParentheses.get(FIRST_INDEX), methodModel);
        getExceptions(splitByLastParentheses.get(SECOND_INDEX), methodModel);
    }

    private void getKeywords(String filename, String keywords, MethodModel methodModel) {
        filename = filename.split(POINT_DELIMITER)[FIRST_INDEX];
        keywords = keywords.trim();

        List<String> words = Arrays.asList(keywords.split(WHITESPACE_DELIMITER));
        Boolean isConstructor = words.contains(filename);

        Integer numOfReservedWords = getNumOfReservedWords(isConstructor);
        Integer size = words.size();
        Integer maxKeywords = size - numOfReservedWords;

        words.stream()
                .limit(maxKeywords)
                .map(String::trim)
                .forEach(methodModel.getKeywords()::add);

        numOfReservedWords--;

        if (!isConstructor)
            methodModel.setReturnType(words.get(maxKeywords).trim());

        methodModel.setName(words.get(maxKeywords + numOfReservedWords).trim());
    }

    private Integer getNumOfReservedWords(Boolean isConstructor) {
        if (isConstructor)
            return ONE_RESERVED_WORDS;
        else
            return TWO_RESERVED_WORDS;
    }

    private void getParameters(String parameters, MethodModel methodModel) {
        parameters = parameters.trim();

        if (!parameters.isEmpty()) {
            List<String> words = Arrays.asList(parameters.split(QOMMA_DELIMITER));

            // TODO check if its a keywords for property

            words.stream()
                    .map(String::trim)
                    .map(parameter -> Arrays.asList(parameter.split(WHITESPACE_DELIMITER)))
                    .map(splittedParameters ->
                            createPropertyModel(splittedParameters.get(FIRST_INDEX), splittedParameters.get(SECOND_INDEX)))
                    .forEach(methodModel.getParameters()::add);
        }
    }

    private PropertyModel createPropertyModel(String type, String name) {
        return PropertyModel.builder()
                .type(type)
                .name(name)
                .build();
    }

    private void getExceptions(String exceptions, MethodModel methodModel) {
        exceptions = exceptions.replace(OPEN_BRACES_DELIMITER, EMPTY_STRING).trim();

        if (!exceptions.isEmpty()) {
            List<String> words = Arrays.asList(exceptions.split(QOMMA_DELIMITER));
            words.set(FIRST_INDEX, words.get(FIRST_INDEX).split(WHITESPACE_DELIMITER)[SECOND_INDEX]);

            words.stream()
                    .map(String::trim)
                    .forEach(methodModel.getExceptions()::add);
        }
    }

    private void analysisMethodBody(String content, IndexModel indexModel, MethodModel methodModel) {
        Integer startBodyIndex = indexModel.getStart();

        String body = searchMethodBody(content, startBodyIndex, indexModel.getEnd());
        methodModel.setBody(body);
    }

    private String searchMethodBody(String content, Integer startBodyIndex, Integer endIndex) {
        Stack<Integer> stack = new Stack<>();
        Integer index;

        for (index = startBodyIndex; index < content.length(); index++) {
            String character = String.valueOf(content.charAt(index));

            switch (character) {
                case OPEN_BRACES_DELIMITER:
                    stack.push(index);
                    break;
                case CLOSE_BRACES_DELIMITER:
                    startBodyIndex = stack.pop();
                    break;
            }

            if (index > endIndex && stack.empty())
                break;
        }

        return content.substring(startBodyIndex + 1, index - 1);
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
