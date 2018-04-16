package com.west2ol.april.entity;

import java.util.List;

public class QuestionsInfo {

    /**
     * question : [{"qid":2,"description":"2","answers":[{"aid":9,"description":"9"},{"aid":10,"description":"10"},{"aid":11,"description":"11"},{"aid":12,"description":"12"}]},{"qid":3,"description":"3","answers":[{"aid":13,"description":"13"},{"aid":14,"description":"14"},{"aid":15,"description":"15"},{"aid":16,"description":"16"}]},{"qid":4,"description":"4","answers":[{"aid":17,"description":"17"},{"aid":18,"description":"18"},{"aid":19,"description":"19"},{"aid":20,"description":"20"}]},{"qid":1,"description":"1","answers":[{"aid":5,"description":"5"},{"aid":6,"description":"6"},{"aid":7,"description":"7"},{"aid":8,"description":"8"}]},{"qid":5,"description":"5","answers":[{"aid":21,"description":"21"},{"aid":22,"description":"22"},{"aid":23,"description":"23"},{"aid":24,"description":"24"}]},{"qid":101,"description":"单选题践行全心全意为人民服务的根本宗旨，把党的（ ）贯彻到治国理政全部活动之中，把人民对美好生活的向往作为奋斗目标，依靠人民创造历史伟业。","answers":[{"aid":1,"description":"A、政治路线"},{"aid":2,"description":"B、思想路线"},{"aid":4,"description":"D、群众路线"},{"aid":3,"description":"C、组织路线"}]}]
     * status : 0
     */

    private int status;
    private List<QuestionBean> question;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<QuestionBean> getQuestion() {
        return question;
    }

    public void setQuestion(List<QuestionBean> question) {
        this.question = question;
    }

    public static class QuestionBean {
        /**
         * qid : 2
         * description : 2
         * answers : [{"aid":9,"description":"9"},{"aid":10,"description":"10"},{"aid":11,"description":"11"},{"aid":12,"description":"12"}]
         */

        private int qid;
        private String description;
        private List<AnswersBean> answers;

        public int getQid() {
            return qid;
        }

        public void setQid(int qid) {
            this.qid = qid;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public List<AnswersBean> getAnswers() {
            return answers;
        }

        public void setAnswers(List<AnswersBean> answers) {
            this.answers = answers;
        }

        public static class AnswersBean {
            /**
             * aid : 9
             * description : 9
             */

            private int aid;
            private String description;

            public int getAid() {
                return aid;
            }

            public void setAid(int aid) {
                this.aid = aid;
            }

            public String getDescription() {
                return description;
            }

            public void setDescription(String description) {
                this.description = description;
            }
        }
    }
}
