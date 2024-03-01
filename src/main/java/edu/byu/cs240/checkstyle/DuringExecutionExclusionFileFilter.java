package edu.byu.cs240.checkstyle;

import com.puppycrawl.tools.checkstyle.api.*;

import java.util.regex.Pattern;

public class DuringExecutionExclusionFileFilter extends AutomaticBean implements Filter {
    private Pattern fileNamePattern;
    public void setFileNamePattern(Pattern fileNamePattern) {
        this.fileNamePattern = fileNamePattern;
    }

    @Override
    public boolean accept(AuditEvent auditEvent) {
        return this.fileNamePattern == null || !this.fileNamePattern.matcher(auditEvent.getFileName()).find();
    }

    @Override
    protected void finishLocalSetup() throws CheckstyleException {}
}
