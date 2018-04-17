package com.west2ol.april.entity.receive;

import java.util.List;

public class PrizeListInfo {

    /**
     * prize : [{"id":1,"amount":2,"probability":50,"description":"Fuck"},{"id":2,"amount":2,"probability":50,"description":"Shit"}]
     * status : 0
     */

    private int status;
    private List<PrizeBean> prize;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<PrizeBean> getPrize() {
        return prize;
    }

    public void setPrize(List<PrizeBean> prize) {
        this.prize = prize;
    }

    public static class PrizeBean {
        /**
         * id : 1
         * amount : 2
         * probability : 50
         * description : Fuck
         */

        private int id;
        private int amount;
        private int probability;
        private String description;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getAmount() {
            return amount;
        }

        public void setAmount(int amount) {
            this.amount = amount;
        }

        public int getProbability() {
            return probability;
        }

        public void setProbability(int probability) {
            this.probability = probability;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
}
