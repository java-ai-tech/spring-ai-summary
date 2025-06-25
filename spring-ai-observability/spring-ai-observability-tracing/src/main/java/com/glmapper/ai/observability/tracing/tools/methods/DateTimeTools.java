package com.glmapper.ai.observability.tracing.tools.methods;

import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.context.i18n.LocaleContextHolder;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;


public class DateTimeTools {

    @Tool(description = "Get the current date and time in the user's timezone")
    public String getCurrentDateTime() {
        return LocalDateTime.now().atZone(LocaleContextHolder.getTimeZone().toZoneId()).toString();
    }


    @Tool(description = "Get the current date and time in the user's timezone")
    public String getFormatDateTime(ToolContext toolContext) {
        return new SimpleDateFormat(toolContext.getContext().get("format").toString())
                .format(LocalDateTime.now().atZone(LocaleContextHolder.getTimeZone().toZoneId()).toInstant().toEpochMilli());
    }
}
