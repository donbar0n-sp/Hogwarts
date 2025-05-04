package ru.hogwarts.school.model;

import jakarta.persistence.*;

@Entity
public class Avatar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String filepath;
    private long fileSize;
    private String mediaType;

    @Lob
    private byte[] data;

    @OneToOne
    private Student student;

    public Avatar() {

    }

    public Avatar(long id, String filepath, long fileSize, String mediaType, byte[] data, Student student) {
        this.id = id;
        this.filepath = filepath;
        this.fileSize = fileSize;
        this.mediaType = mediaType;
        this.data = data;
        this.student = student;
    }

    public long getId() {
        return id;
    }

    public String getFilepath() {
        return filepath;
    }

    public long getFileSize() {
        return fileSize;
    }

    public String getMediaType() {
        return mediaType;
    }

    public byte[] getData() {
        return data;
    }

    public Student getStudent() {
        return student;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public void setStudent(Student student) {
        this.student = student;
    }
}
