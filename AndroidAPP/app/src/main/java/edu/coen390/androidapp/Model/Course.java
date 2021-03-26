package edu.coen390.androidapp.Model;



public class Course {

//class of Course objects




        private long id;
        private String title;
        private String code;


        public Course(long id, String title, String code) {
            this.id = id;
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


            return title + " " + code ;


        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
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





