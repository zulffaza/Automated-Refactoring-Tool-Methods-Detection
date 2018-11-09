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

    private static final String OPEN_PARENTHESES = "(";
    private static final String CLOSE_PARENTHESES = ")";
    private static final String OPEN_BRACES = "{";
    private static final String CLOSE_BRACES = "}";
    private static final String SEMICOLON = ";";
    private static final String OPEN_PARENTHESES_DELIMITER = "\\" + OPEN_PARENTHESES;
    private static final String CLOSE_PARENTHESES_DELIMITER = "\\" + CLOSE_PARENTHESES;
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

        try {
            extendStartIndex(fileModel.getContent(), indexModel);
            anaysisMethodAttributes(fileModel, indexModel, methodModel);
            analysisMethodBody(fileModel.getContent(), indexModel, methodModel);
            addToResult(fileModel.getFilename(), methodModel, result);
        } catch (Exception e) {
            // Do nothing
            // Mark of non-method analysis
        }

        return null;
    }

    private void extendStartIndex(String content, IndexModel indexModel) {
        Integer index = indexModel.getStart();
        Stack<String> stack = new Stack<>();

        while (!isValid(stack, content.substring(index, index + SECOND_INDEX)))
            index--;

        indexModel.setStart(++index);
    }

    private Boolean isValid(Stack<String> stack, String string) {
        ifParams(stack, string);
        return validate(stack, string);
    }

    private void ifParams(Stack<String> stack, String string) {
        switch (string) {
            case CLOSE_PARENTHESES:
                stack.push(string);
                break;
            case OPEN_PARENTHESES:
                stack.pop();
                break;
        }
    }

    private Boolean validate(Stack<String> stack, String string) {
        if (!stack.empty())
            return Boolean.FALSE;
        else
            return isEOS(string);
    }

    private Boolean isEOS(String string) {
        return string.equals(SEMICOLON) || string.equals(CLOSE_BRACES) || string.equals(OPEN_BRACES);
    }

    private void anaysisMethodAttributes(FileModel fileModel, IndexModel indexModel, MethodModel methodModel) {
        String methodDeclarations = fileModel.getContent()
                .substring(indexModel.getStart(), indexModel.getEnd()).trim();

        List<String> splitByFirstParentheses = Arrays.asList(methodDeclarations.split(OPEN_PARENTHESES_DELIMITER));
        simplifyList(splitByFirstParentheses);

        System.out.println(String.join(" --> ", splitByFirstParentheses));

        List<String> splitByLastParentheses = Arrays.asList(splitByFirstParentheses.get(SECOND_INDEX)
                .split(CLOSE_PARENTHESES_DELIMITER));

        getKeywords(fileModel.getFilename(), splitByFirstParentheses.get(FIRST_INDEX), methodModel);
        getParameters(splitByLastParentheses.get(FIRST_INDEX), methodModel);
        getExceptions(splitByLastParentheses.get(SECOND_INDEX), methodModel);
    }

    private void simplifyList(List<String> words) {
        for (Integer index = FIRST_INDEX; index < words.size(); index++) {
            String word = words.get(index);

            for (Integer charIndex = FIRST_INDEX; charIndex < word.length(); charIndex++) {
                String character = word.substring(charIndex, charIndex + SECOND_INDEX);

                switch (character) {
                    case "@":
                        for (Integer nextIndex = index + SECOND_INDEX; nextIndex < words.size(); nextIndex++) {
                            String nextWord = words.get(nextIndex);

                            for (Integer nextCharIndex = FIRST_INDEX; nextCharIndex < nextWord.length(); nextCharIndex++) {
                                String nextCharacter = nextWord.substring(nextCharIndex, nextCharIndex + SECOND_INDEX);

                                if (nextCharacter.equals(")")) {
                                    words.set(index, String.join("(", words.get(index), nextWord));
                                    words.remove(nextIndex.intValue());
                                    break;
                                }
                            }
                        }
                }
            }
        }
    }

    private void getKeywords(String filename, String keywords, MethodModel methodModel)
            throws IndexOutOfBoundsException {
        filename = filename.split(POINT_DELIMITER)[FIRST_INDEX];
        keywords = keywords.trim();

        List<String> words = Arrays.asList(keywords.split(WHITESPACE_DELIMITER));
        boolean isConstructor = words.contains(filename);

        Integer numOfReservedWords = getNumOfReservedWords(isConstructor);
        Integer size = words.size();
        Integer maxKeywords = size - numOfReservedWords;

        saveKeywords(words, maxKeywords, methodModel);
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

    private void saveKeywords(List<String> words, Integer numOfKeywords, MethodModel methodModel) {
        words.stream()
                .limit(numOfKeywords)
                .map(String::trim)
                .forEach(methodModel.getKeywords()::add);
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
        exceptions = exceptions.replace(OPEN_BRACES, EMPTY_STRING).trim();

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
                case OPEN_BRACES:
                    stack.push(index);
                    break;
                case CLOSE_BRACES:
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
