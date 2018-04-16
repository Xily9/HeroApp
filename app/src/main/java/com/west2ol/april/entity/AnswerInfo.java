package com.west2ol.april.entity;

import java.util.List;

public class AnswerInfo {

    /**
     * correct : [1,2,3,4,5]
     * incorrect : [{"qid":101,"aid":4}]
     * status : 0
     */

    private int status;
    private List<Integer> correct;
    private List<IncorrectBean> incorrect;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<Integer> getCorrect() {
        return correct;
    }

    public void setCorrect(List<Integer> correct) {
        this.correct = correct;
    }

    public List<IncorrectBean> getIncorrect() {
        return incorrect;
    }

    public void setIncorrect(List<IncorrectBean> incorrect) {
        this.incorrect = incorrect;
    }

    public static class IncorrectBean {
        /**
         * qid : 101
         * aid : 4
         */

        private int qid;
        private int aid;

        public int getQid() {
            return qid;
        }

        public void setQid(int qid) {
            this.qid = qid;
        }

        public int getAid() {
            return aid;
        }

        public void setAid(int aid) {
            this.aid = aid;
        }
    }
}
