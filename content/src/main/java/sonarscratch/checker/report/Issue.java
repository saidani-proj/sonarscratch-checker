/**
 * sonarscratch.checker project
 * Copyright (c) tcdorg. All rights reserved.
 * Licensed under the MIT License. See LICENSE.txt in the project root for license information.
 */

package sonarscratch.checker.report;

import org.json.JSONObject;

class Issue {
    private String message;
    private String type;
    private String severity;
    private IssueFile file;
    private FilePreview filePreview;
    private JSONObject infos;

    Issue(String message, String type, String severity, IssueFile file, FilePreview filePreview, JSONObject infos) {
        this.message = message;
        this.type = type;
        this.severity = severity;
        this.file = file;
        this.filePreview = filePreview;
        this.infos = infos;
    }

    String getMessage() {
        return message;
    }

    String getType() {
        return type;
    }

    String getSeverity() {
        return severity;
    }

    IssueFile getFile() {
        return file;
    }

    FilePreview getFilePreview() {
        return filePreview;
    }

    JSONObject getInfos() {
        return infos;
    }
}
