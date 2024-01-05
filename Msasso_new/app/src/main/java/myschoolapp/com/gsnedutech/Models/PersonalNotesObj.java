package myschoolapp.com.gsnedutech.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PersonalNotesObj {



        @SerializedName("personalNote")
        @Expose
        private String personalNote;
        @SerializedName("personalNoteTitle")
        @Expose
        private String personalNoteTitle;
        @SerializedName("studentId")
        @Expose
        private String studentId;
        @SerializedName("createdDate")
        @Expose
        private String createdDate;
        @SerializedName("updatedDate")
        @Expose
        private String updatedDate="";
        @SerializedName("color")
        @Expose
        private String color;
        @SerializedName("personalNoteId")
        @Expose
        private String personalNoteId;

        public String getPersonalNote() {
            return personalNote;
        }

        public void setPersonalNote(String personalNote) {
            this.personalNote = personalNote;
        }

        public String getPersonalNoteTitle() {
            return personalNoteTitle;
        }

        public void setPersonalNoteTitle(String personalNoteTitle) {
            this.personalNoteTitle = personalNoteTitle;
        }

        public String getStudentId() {
            return studentId;
        }

        public void setStudentId(String studentId) {
            this.studentId = studentId;
        }

        public String getCreatedDate() {
            return createdDate;
        }

        public void setCreatedDate(String createdDate) {
            this.createdDate = createdDate;
        }

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }

        public String getPersonalNoteId() {
            return personalNoteId;
        }

        public void setPersonalNoteId(String personalNoteId) {
            this.personalNoteId = personalNoteId;
        }

        public String getUpdatedDate() {
            return updatedDate;
        }

        public void setUpdatedDate(String updatedDate) {
            this.updatedDate = updatedDate;
        }
}
