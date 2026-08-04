package com.banksphere.core.utility;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
// import org.thymeleaf.TemplateEngine;
// import org.thymeleaf.context.Context;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class PDFGeneratorUtil {

    // private final TemplateEngine templateEngine; // TODO: Uncomment when Thymeleaf is configured

    public byte[] generateAccountStatement(Map<String, Object> data) {
        // TODO: render template to HTML, convert to PDF using iText7
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public byte[] generateLoanAmortization(Map<String, Object> data) {
        // TODO: render template to HTML, convert to PDF using iText7
        throw new UnsupportedOperationException("Not implemented yet");
    }

    private String renderTemplate(String templateName, Map<String, Object> data) {
        // TODO: render Thymeleaf template to String
        throw new UnsupportedOperationException("Not implemented yet");
    }

    private byte[] convertHtmlToPdf(String html) {
        // TODO: use iText7 HtmlConverter
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
