package myschoolapp.com.gsnedutech.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "notes")
public class Notes implements Serializable {


    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "student_id")
    private String studentId;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(name = "color")
    private String color;

    @ColumnInfo(name = "createdat")
    private String createdAt;

    @ColumnInfo(name = "updatedat")
    private String updatedAt;

    public Notes(int id, String studentId, String title, String description, String color, String createdAt, String updatedAt) {
        this.id = id;
        this.studentId = studentId;
        this.title = title;
        this.description = description;
        this.color = color;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }


    @Ignore
    public Notes(String studentId, String title, String description, String color, String createdAt, String updatedAt) {
        this.studentId = studentId;
        this.title = title;
        this.description = description;
        this.color = color;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }



    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getId() {
        return id;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }


}
