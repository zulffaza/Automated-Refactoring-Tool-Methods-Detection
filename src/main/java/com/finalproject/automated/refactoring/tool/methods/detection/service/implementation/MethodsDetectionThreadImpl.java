package com.finalproject.automated.refactoring.tool.methods.detection.service.implementation;

import com.finalproject.automated.refactoring.tool.files.detection.model.FileModel;
import com.finalproject.automated.refactoring.tool.methods.detection.model.IndexModel;
import com.finalproject.automated.refactoring.tool.methods.detection.model.service.AnalysisMethodRequest;
import com.finalproject.automated.refactoring.tool.methods.detection.service.MethodAnalysis;
import com.finalproject.automated.refactoring.tool.methods.detection.service.MethodsDetectionThread;
import com.finalproject.automated.refactoring.tool.model.MethodModel;
import com.finalproject.automated.refactoring.tool.utils.service.ThreadsWatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    @Autowired
    private MethodAnalysis methodAnalysis;

    @Autowired
    private ThreadsWatcher threadsWatcher;

    private static final Integer WAITING_TIME = 500;

    @Async
    @Override
    public Future detect(FileModel fileModel, String methodsRegex, Map<String, List<MethodModel>> result) {
        List<IndexModel> indexOfMethods = getIndexOfMethods(fileModel.getContent(), methodsRegex);
        List<Future> futures = new ArrayList<>();

        AnalysisMethodRequest analysisMethodRequest = AnalysisMethodRequest.builder()
                .fileModel(fileModel)
                .indexOfMethods(indexOfMethods)
                .futures(futures)
                .result(result)
                .build();

        analysisMethods(analysisMethodRequest);
        threadsWatcher.waitAllThreadsDone(futures, WAITING_TIME);

        return null;
    }

    private List<IndexModel> getIndexOfMethods(String content, String methodsRegex) {
        List<IndexModel> indexModels = new ArrayList<>();

        Pattern pattern = Pattern.compile(methodsRegex, Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(content);

        while (matcher.find()) {
            IndexModel indexModel = IndexModel.builder()
                    .start(matcher.start())
                    .end(matcher.end())
                    .build();

            indexModels.add(indexModel);
        }

        return indexModels;
    }

    private void analysisMethods(AnalysisMethodRequest analysisMethodRequest) {
        analysisMethodRequest.getIndexOfMethods().
                forEach(indexModel -> doAnalysisMethods(analysisMethodRequest, indexModel));
    }

    private void doAnalysisMethods(AnalysisMethodRequest analysisMethodRequest, IndexModel indexModel) {
        Future future = methodAnalysis.analysis(analysisMethodRequest.getFileModel(), indexModel,
                analysisMethodRequest.getResult());
        analysisMethodRequest.getFutures().add(future);
    }
}
