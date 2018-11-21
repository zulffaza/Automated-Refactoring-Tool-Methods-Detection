package com.finalproject.automated.refactoring.tool.methods.detection;

import com.finalproject.automated.refactoring.tool.files.detection.model.FileModel;
import com.finalproject.automated.refactoring.tool.methods.detection.service.MethodsDetection;
import com.finalproject.automated.refactoring.tool.model.MethodModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * @author fazazulfikapp
 * @version 1.0.0
 * @since 4 November 2018
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTest {

    @Autowired
    private MethodsDetection methodsDetection;

    private List<FileModel> fileModels;

    @Before
    public void setUp() {
        fileModels = createFileModels();
    }

    @Test
    public void methodsDetection_singleFile_success() {
        List<MethodModel> methodModels = methodsDetection.detect(fileModels.get(0));
        methodModels.forEach(methodModel -> System.out.println(methodModel.getName() + " --> " + methodModel.getKeywords().size()));
    }

    private List<FileModel> createFileModels() {
        List<FileModel> fileModels = new ArrayList<>();

        fileModels.add(FileModel.builder()
                .path("com/example/carikado/emailhelp/model")
                .filename("EmailHelp.java")
                .content(createFileContent())
                .build());

        return fileModels;
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
                "    EmailHelp(String emailSubject, String emailContent) throws Exception, IOException {\n" +
                "        mEmailSubject = emailSubject;\n" +
                "        mEmailContent = emailContent;\n" +
                "    }\n" +
                "\n" +
                "    @GetMapping (\n" +
                "               value = \"/{api}/city\",\n" +
                "               produces = MediaType.APPLICATION_JSON_VALUE\n" +
                "    )\n" +
                "    public Response < String, String > getEmailSubject(@RequestParam(required = false, defaultValue = \"0\") Integer page,\n" +
                "                                              @RequestParam(required = false, defaultValue = \"10\") Integer pageSize,\n" +
                "                                              @RequestParam(required = false) Integer sort) {\n" +
                "        try {\n" +
                "            return mEmailSubject;\n" +
                "        } catch (NullPointerException e) {\n" +
                "            return null;\n" +
                "        }\n" +
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
