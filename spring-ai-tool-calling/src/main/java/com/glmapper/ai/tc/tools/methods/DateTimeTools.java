package com.glmapper.ai.tc.tools.methods;

import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.context.i18n.LocaleContextHolder;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;

/**
 * @Classname DateTimeTools
 * @Description 时间工具
 * @Date 2025/5/29 15:05
 * @Created by glmapper
 */
public class DateTimeTools {

    @Tool(description = "Get the current date and time in the user's timezone")
    public String getCurrentDateTime() {
        return LocalDateTime.now().atZone(LocaleContextHolder.getTimeZone().toZoneId()).toString();
    }


    /***
     *
     *
     *
     *
     *
     *
     * @param toolContext
     * @return
     */
    @Tool(description = "Get the current date and time in the user's timezone")
    public String getFormatDateTime(ToolContext toolContext) {
        return new SimpleDateFormat(toolContext.getContext().get("format").toString())
                .format(LocalDateTime.now().atZone(LocaleContextHolder.getTimeZone().toZoneId()).toInstant().toEpochMilli());
    }
}
