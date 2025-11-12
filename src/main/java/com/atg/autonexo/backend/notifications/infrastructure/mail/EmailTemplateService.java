package com.atg.autonexo.backend.notifications.infrastructure.mail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service for loading and processing email templates.
 * Templates are stored in resources/templates/emails/ directory.
 */
@Service
public class EmailTemplateService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(EmailTemplateService.class);
    private static final String TEMPLATE_PATH = "templates/emails/";
    
    // Cache templates in memory to avoid reading from disk on every email
    private final Map<String, String> templateCache = new ConcurrentHashMap<>();
    
    /**
     * Loads and processes an email template with the provided variables.
     * 
     * @param templateName name of the template file (without .html extension)
     * @param variables map of variable names to values for template replacement
     * @return processed HTML template
     */
    public String processTemplate(String templateName, Map<String, String> variables) {
        String template = loadTemplate(templateName);
        return replaceVariables(template, variables);
    }
    
    /**
     * Loads a template from resources, using cache if available.
     */
    private String loadTemplate(String templateName) {
        return templateCache.computeIfAbsent(templateName, name -> {
            try {
                ClassPathResource resource = new ClassPathResource(TEMPLATE_PATH + name + ".html");
                String content = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
                LOGGER.debug("Loaded email template: {}", name);
                return content;
            } catch (IOException e) {
                LOGGER.error("Failed to load email template: {}", name, e);
                throw new RuntimeException("Failed to load email template: " + name, e);
            }
        });
    }
    
    /**
     * Replaces placeholders in the template with actual values.
     * Placeholders are in the format {variableName}.
     */
    private String replaceVariables(String template, Map<String, String> variables) {
        String result = template;
        for (Map.Entry<String, String> entry : variables.entrySet()) {
            String placeholder = "{" + entry.getKey() + "}";
            String value = entry.getValue() != null ? entry.getValue() : "";
            result = result.replace(placeholder, value);
        }
        return result;
    }
    
    /**
     * Clears the template cache. Useful for development when templates change.
     */
    public void clearCache() {
        templateCache.clear();
        LOGGER.info("Email template cache cleared");
    }
}

