package org.sanyuankexie.attendance.model;

import lombok.Data;

import java.io.ByteArrayOutputStream;

@Data
public class AttachmentData {
    private String filename;
    private ByteArrayOutputStream stream;
}
