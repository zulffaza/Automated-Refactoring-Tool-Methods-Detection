package com.finalproject.automated.refactoring.tool.methods.detection.service.implementation;

import com.finalproject.automated.refactoring.tool.files.detection.model.FileModel;
import com.finalproject.automated.refactoring.tool.methods.detection.model.IndexModel;
import com.finalproject.automated.refactoring.tool.model.MethodModel;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * @author fazazulfikapp
 * @version 1.0.0
 * @since 23 October 2018
 */

public class MethodAnalysisImplTest {

    private static final Integer FIRST_INDEX = 0;
    private static final Integer SECOND_INDEX = 1;
    private static final Integer ZERO = 0;
    private static final Integer ONE = 1;
    private static final Integer TWO = 2;

    private static final String METHOD_MODIFIER = "public";

    private MethodAnalysisImpl methodAnalysis;

    private FileModel fileModel;

    private IndexModel indexModel;

    private IndexModel nonConstructorIndexModel;

    @Before
    public void setUp() {
        methodAnalysis = new MethodAnalysisImpl();
        fileModel = FileModel.builder()
                .path("com/example/carikado/emailhelp/model")
                .filename("EmailHelp.java")
                .content(createFileContent())
                .build();
        indexModel = IndexModel.builder()
                .start(385)
                .end(469)
                .build();
        nonConstructorIndexModel = IndexModel.builder()
                .start(643)
                .end(682)
                .build();
    }

    @Test
    public void analysis_success() {
        String methodName = "EmailHelp";

        Map<String, List<MethodModel>> result = Collections.synchronizedMap(new HashMap<>());
        methodAnalysis.analysis(fileModel, indexModel, result);

        assertEquals(ONE.intValue(), result.size());
        assertTrue(result.containsKey(fileModel.getFilename()));
        assertEquals(ONE.intValue(), result.get(fileModel.getFilename()).size());
        assertEquals(ONE.intValue(), result.get(fileModel.getFilename()).get(FIRST_INDEX).getKeywords().size());
        assertEquals(METHOD_MODIFIER,
                result.get(fileModel.getFilename()).get(FIRST_INDEX).getKeywords().get(FIRST_INDEX));
        assertNull(result.get(fileModel.getFilename()).get(FIRST_INDEX).getReturnType());
        assertEquals(methodName, result.get(fileModel.getFilename()).get(FIRST_INDEX).getName());
        assertEquals(TWO.intValue(), result.get(fileModel.getFilename()).get(FIRST_INDEX).getParameters().size());
        assertEquals(TWO.intValue(), result.get(fileModel.getFilename()).get(FIRST_INDEX).getExceptions().size());
    }

    @Test
    public void analysis_success_resultIsNotEmpty() {
        String methodName = "EmailHelp";

        Map<String, List<MethodModel>> result = Collections.synchronizedMap(new HashMap<>());
        result.put(fileModel.getFilename(), new ArrayList<>());
        result.get(fileModel.getFilename()).add(MethodModel.builder().build());

        methodAnalysis.analysis(fileModel, indexModel, result);

        assertEquals(ONE.intValue(), result.size());
        assertTrue(result.containsKey(fileModel.getFilename()));
        assertEquals(TWO.intValue(), result.get(fileModel.getFilename()).size());
        assertEquals(ONE.intValue(), result.get(fileModel.getFilename()).get(SECOND_INDEX).getKeywords().size());
        assertEquals(METHOD_MODIFIER,
                result.get(fileModel.getFilename()).get(SECOND_INDEX).getKeywords().get(FIRST_INDEX));
        assertNull(result.get(fileModel.getFilename()).get(SECOND_INDEX).getReturnType());
        assertEquals(methodName, result.get(fileModel.getFilename()).get(SECOND_INDEX).getName());
        assertEquals(TWO.intValue(), result.get(fileModel.getFilename()).get(SECOND_INDEX).getParameters().size());
        assertEquals(TWO.intValue(), result.get(fileModel.getFilename()).get(SECOND_INDEX).getExceptions().size());
    }

    @Test
    public void analysis_success_nonConstructorMethods() {
        String methodName = "setEmailSubject";
        String returnType = "void";

        Map<String, List<MethodModel>> result = Collections.synchronizedMap(new HashMap<>());
        methodAnalysis.analysis(fileModel, nonConstructorIndexModel, result);

        assertEquals(ONE.intValue(), result.size());
        assertTrue(result.containsKey(fileModel.getFilename()));
        assertEquals(ONE.intValue(), result.get(fileModel.getFilename()).size());
        assertEquals(ONE.intValue(), result.get(fileModel.getFilename()).get(FIRST_INDEX).getKeywords().size());
        assertEquals(METHOD_MODIFIER,
                result.get(fileModel.getFilename()).get(FIRST_INDEX).getKeywords().get(FIRST_INDEX));
        assertEquals(returnType, result.get(fileModel.getFilename()).get(FIRST_INDEX).getReturnType());
        assertEquals(methodName, result.get(fileModel.getFilename()).get(FIRST_INDEX).getName());
        assertEquals(ONE.intValue(), result.get(fileModel.getFilename()).get(FIRST_INDEX).getParameters().size());
        assertEquals(ZERO.intValue(), result.get(fileModel.getFilename()).get(FIRST_INDEX).getExceptions().size());
    }

    @Test(expected = NullPointerException.class)
    public void analysis_failed_fileModelIsNull() {
        Map<String, List<MethodModel>> result = Collections.synchronizedMap(new HashMap<>());
        methodAnalysis.analysis(null, indexModel, result);
    }

    @Test(expected = NullPointerException.class)
    public void analysis_failed_indexModelIsNull() {
        Map<String, List<MethodModel>> result = Collections.synchronizedMap(new HashMap<>());
        methodAnalysis.analysis(fileModel, null, result);
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
}