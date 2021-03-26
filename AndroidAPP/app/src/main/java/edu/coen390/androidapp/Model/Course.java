package edu.coen390.androidapp.Model;


import java.io.Serializable;

public class Course implements Serializable {

//class of Course objects




        private long id;
        private long invigilator_id;
        private String title;
        private String code;



        public Course(long id, long invigilator_id, String title, String code) {
            this.id = id;
            this.invigilator_id = invigilator_id;
            this.title = title;
            this.code = code;

        }

        public Course(long invigilator_id, String title, String code){
            this.invigilator_id = invigilator_id;
            this.title = title;
            this.code = code;
        }

        public Course(String title, String code) {
            this.title = title;
            this.code = code;
        }



        //check this
        @Override
        public String toString() {


            return title + " " + code;


        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public long getInvigilator_id() {
            return invigilator_id;
        }

        public void setInvigilator_id(long invigilator_id) {
            this.invigilator_id = invigilator_id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }
    }





