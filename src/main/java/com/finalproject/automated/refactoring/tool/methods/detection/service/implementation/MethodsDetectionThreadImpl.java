package com.finalproject.automated.refactoring.tool.methods.detection.service.implementation;

import com.finalproject.automated.refactoring.tool.files.detection.model.FileModel;
import com.finalproject.automated.refactoring.tool.methods.detection.service.MethodsDetectionThread;
import com.finalproject.automated.refactoring.tool.model.MethodModel;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author fazazulfikapp
 * @version 1.0.0
 * @since 18 October 2018
 */

@Service
public class MethodsDetectionThreadImpl implements MethodsDetectionThread {

    private static final String NEW_LINE_DELIMITER = "\n";

    @Async
    @Override
    public Future detect(FileModel fileModel, String methodsRegex, Map<String, List<MethodModel>> result) {
        return null;
    }

    private List<Integer> getIndexOfMethods(String content, String methodsRegex) {
        Pattern pattern = Pattern.compile(methodsRegex, Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(content);

        while (matcher.find()) {
            System.out.print("Start index: " + matcher.start());
            System.out.print(" End index: " + matcher.end() + " ");
        }

        return null;
    }
}
