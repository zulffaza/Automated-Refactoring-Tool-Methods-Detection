package com.finalproject.automated.refactoring.tool.methods.detection.service.implementation.util;

import com.finalproject.automated.refactoring.tool.files.detection.model.FileModel;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @author fazazulfikapp
 * @version 1.0.0
 * @since 5 December 2018
 */

public class TestUtil {

    public static Future getFutureExpectation() {
        return new Future() {

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
    }

    public static FileModel getFileModel() {
        return FileModel.builder()
                .path("path")
                .filename("Filename.java")
                .content(getFileContent())
                .build();
    }

    private static String getFileContent() {
        return "package path;\n" +
                "\n" +
                "import java.io.Serializable;\n" +
                "\n" +
                "public class Filename implements Serializable {\n" +
                "\n" +
                "    private String name;\n" +
                "    private String extension;\n" +
                "\n" +
                "    @SuppressWarnings()\n" +
                "    public Filename() throws Exception, IOException {\n" +
                "\n" +
                "    }\n" +
                "\n" +
                "    @SuppressWarnings()\n" +
                "    public Filename(@NonNull String name, @NonNull String extension) throws Exception, IOException {\n" +
                "        this.name = name;\n" +
                "        this.extension = extension;\n" +
                "    }\n" +
                "\n" +
                "    public String getName() {\n" +
                "        return name;\n" +
                "    }\n" +
                "\n" +
                "    public void setName(@NonNull String name) {\n" +
                "        this.name = name;\n" +
                "    }\n" +
                "\n" +
                "    public String getExtension() {\n" +
                "        return extension;\n" +
                "    }\n" +
                "\n" +
                "    public void setExtension(@NonNull String extension) {\n" +
                "        this.extension = extension;\n" +
                "    }\n" +
                "}";
    }
}
