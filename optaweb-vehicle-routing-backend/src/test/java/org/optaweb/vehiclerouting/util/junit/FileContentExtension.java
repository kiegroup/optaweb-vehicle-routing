package org.optaweb.vehiclerouting.util.junit;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

public class FileContentExtension implements ParameterResolver {

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
            throws ParameterResolutionException {
        return parameterContext.isAnnotated(FileContent.class)
                && parameterContext.getParameter().getType().equals(String.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
            throws ParameterResolutionException {
        String resourceName = parameterContext.findAnnotation(FileContent.class).orElseThrow().value();
        URL resource = parameterContext.getTarget().orElseThrow().getClass().getResource(resourceName);
        if (resource == null) {
            throw new AssertionError("Resource <" + resourceName + "> cannot be loaded.");
        }
        try {
            return Files.readString(Path.of(resource.toURI()));
        } catch (URISyntaxException | IOException e) {
            throw new AssertionError("Failed to read resource <" + resourceName + ">.", e);
        }
    }
}
