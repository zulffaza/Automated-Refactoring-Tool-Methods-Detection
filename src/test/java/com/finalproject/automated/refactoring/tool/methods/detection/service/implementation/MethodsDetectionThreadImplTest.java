package com.finalproject.automated.refactoring.tool.methods.detection.service.implementation;

import com.finalproject.automated.refactoring.tool.files.detection.model.FileModel;
import com.finalproject.automated.refactoring.tool.methods.detection.model.IndexModel;
import com.finalproject.automated.refactoring.tool.methods.detection.service.MethodAnalysis;
import com.finalproject.automated.refactoring.tool.model.MethodModel;
import com.finalproject.automated.refactoring.tool.utils.service.ThreadsWatcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

/**
 * @author fazazulfikapp
 * @version 1.0.0
 * @since 22 October 2018
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class MethodsDetectionThreadImplTest {

    @Autowired
    private MethodsDetectionThreadImpl methodsDetectionThread;

    @MockBean
    private MethodAnalysis methodAnalysis;

    @MockBean
    private ThreadsWatcher threadsWatcher;

    private static final Integer WAITING_TIME = 500;

    private static final String METHODS_REGEX = "(?:\\s)*(?:(\\w*)\\s*)?((?:\\()+(?:[@\\w\\[\\]<>\\(\\)=\",\\s])*(?:\\)))+(?:[\\w,\\s])*(\\{)+(?:\\s)*$";

    private FileModel fileModel;

    @Before
    public void setUp() {
        Future future = new Future() {

            @Override
            public boolean cancel(boolean mayInterruptIfRunning) {
                return false;
            }

            @Override
            public boolean isCancelled() {
                return false;
            }

            @Override
            public boolean isDone() {
                return true;
            }

            @Override
            public Object get() {
                return null;
            }

            @Override
            public Object get(long timeout, TimeUnit unit) {
                return null;
            }
        };

        fileModel = FileModel.builder()
                .path("com/example/carikado/emailhelp/model")
                .filename("EmailHelp.java")
                .content(createFileContent())
                .build();

        createIndexModels().forEach(indexModel ->
                when(methodAnalysis.analysis(eq(fileModel), eq(indexModel),
                        eq(Collections.synchronizedMap(new HashMap<>())))).thenReturn(future));

        doNothing().when(threadsWatcher)
                .waitAllThreadsDone(eq(Collections.singletonList(future)), eq(WAITING_TIME));
    }

    @Test
    public void detect_success() {
        Map<String, List<MethodModel>> result = Collections.synchronizedMap(new HashMap<>());
        methodsDetectionThread.detect(fileModel, METHODS_REGEX, result);
    }

    @Test(expected = NullPointerException.class)
    public void detect_failed_fileModelIsNull() {
        Map<String, List<MethodModel>> result = Collections.synchronizedMap(new HashMap<>());
        methodsDetectionThread.detect(null, METHODS_REGEX, result);
    }

    @Test(expected = NullPointerException.class)
    public void detect_failed_methodsRegexIsNull() {
        Map<String, List<MethodModel>> result = Collections.synchronizedMap(new HashMap<>());
        methodsDetectionThread.detect(fileModel, null, result);
    }

    private String createFileContent() {
        return "package com.example.carikado.emailhelp.model;\n" +
                "\n" +
                "import java.io.Serializable;\n" +
                "\n" +
                "/**\n" +
                " * Merupakan model untuk mengirim bantuan user menggunakan email\n" +
                " *\n" +
                " * @author Faza Zulfika P P\n" +
                " * @version 1.0\n" +
                " * @since 13 Oktober 2017\n" +
                " */\n" +
                "public class EmailHelp implements Serializable {\n" +
                "\n" +
                "    private String mEmailSubject;\n" +
                "    private String mEmailContent;\n" +
                "\n" +
                "    public EmailHelp() {\n" +
                "\n" +
                "    }\n" +
                "\n" +
                "    public EmailHelp(String emailSubject, String emailContent) throws Exception, IOException {\n" +
                "        mEmailSubject = emailSubject;\n" +
                "        mEmailContent = emailContent;\n" +
                "    }\n" +
                "\n" +
                "    public String getEmailSubject() {\n" +
                "        return mEmailSubject;\n" +
                "    }\n" +
                "\n" +
                "    public void setEmailSubject(String emailSubject) {\n" +
                "        mEmailSubject = emailSubject;\n" +
                "    }\n" +
                "\n" +
                "    public String getEmailContent() {\n" +
                "        return mEmailContent;\n" +
                "    }\n" +
                "\n" +
                "    public void setEmailContent(String emailContent) {\n" +
                "        mEmailContent = emailContent;\n" +
                "    }\n" +
                "}";
    }

    private List<IndexModel> createIndexModels() {
        List<IndexModel> indexModels = new ArrayList<>();

        indexModels.add(IndexModel.builder()
                .start(352)
                .end(367)
                .build());

        indexModels.add(IndexModel.builder()
                .start(385)
                .end(469)
                .build());

        indexModels.add(IndexModel.builder()
                .start(570)
                .end(590)
                .build());

        indexModels.add(IndexModel.builder()
                .start(643)
                .end(682)
                .build());

        indexModels.add(IndexModel.builder()
                .start(745)
                .end(765)
                .build());

        indexModels.add(IndexModel.builder()
                .start(818)
                .end(857)
                .build());

        return indexModels;
    }
}