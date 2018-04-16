package com.west2ol.april.entity;

import java.util.List;

public class PostAnswerInfo {

    /**
     * uid : 1
     * token : f8cafa6c-cfb9-468f-ad9a-f0148d3f6314
     * answers : [{"question_id":101,"answer_id":2},{"question_id":1,"answer_id":8},{"question_id":2,"answer_id":12},{"question_id":3,"answer_id":16},{"question_id":4,"answer_id":20},{"question_id":5,"answer_id":24}]
     */

    private int uid;
    private String token;
    private List<AnswersBean> answers;

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public List<AnswersBean> getAnswers() {
        return answers;
    }

    public void setAnswers(List<AnswersBean> answers) {
        this.answers = answers;
    }

    public static class AnswersBean {
        /**
         * question_id : 101
         * answer_id : 2
         */

        private int question_id;
        private int answer_id;

        public int getQuestion_id() {
            return question_id;
        }

        public void setQuestion_id(int question_id) {
            this.question_id = question_id;
        }

        public int getAnswer_id() {
            return answer_id;
        }

        public void setAnswer_id(int answer_id) {
            this.answer_id = answer_id;
        }
    }
}
