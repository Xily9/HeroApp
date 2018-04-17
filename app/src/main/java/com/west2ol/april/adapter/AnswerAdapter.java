package com.west2ol.april.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.west2ol.april.R;
import com.west2ol.april.entity.receive.QuestionsInfo;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AnswerAdapter extends RecyclerView.Adapter<AnswerAdapter.ViewHolder>{
    private List<QuestionsInfo.QuestionBean.AnswersBean> answersBeanList;
    private Context mContext;
    private onItemClickListener listener;
    private int userAnswer,answer;
    public AnswerAdapter(Context mContext,List<QuestionsInfo.QuestionBean.AnswersBean> answersBeanList,int userAnswer,int answer) {
        this.answersBeanList=answersBeanList;
        this.mContext=mContext;
        this.userAnswer=userAnswer;
        this.answer=answer;
    }

    public void setUserAnswer(int userAnswer) {
        this.userAnswer = userAnswer;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.layout_item_answers,parent,false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        QuestionsInfo.QuestionBean.AnswersBean answersBean=answersBeanList.get(position);
        holder.alpha.setText(String.valueOf((char)(position+65)));

        if(answer>0){
            if(answer-1==position){
                holder.alpha.setTextColor(Color.parseColor("#ffffff"));
                holder.alpha.setBackgroundResource(R.drawable.bg_round_click);
            }else if(userAnswer-1==position){
                holder.alpha.setTextColor(Color.parseColor("#ffffff"));
                holder.alpha.setBackgroundResource(R.drawable.bg_round_error);
            }else{
                holder.alpha.setTextColor(Color.parseColor("#000000"));
                holder.alpha.setBackgroundResource(R.drawable.bg_round);
            }
        }else if(userAnswer-1==position){
                holder.alpha.setTextColor(Color.parseColor("#ffffff"));
                holder.alpha.setBackgroundResource(R.drawable.bg_round_click);
        }else{
            holder.alpha.setTextColor(Color.parseColor("#000000"));
            holder.alpha.setBackgroundResource(R.drawable.bg_round);
        }
        holder.description.setText(answersBean.getDescription());
        holder.itemView.setOnClickListener(view -> listener.onClick(holder.alpha,position));
        holder.alpha.setOnClickListener(view -> listener.onClick(holder.alpha, position));
    }

    public void setOnItemClickListener(onItemClickListener listener){
        this.listener=listener;
    }

    @Override
    public int getItemCount() {
        return answersBeanList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.alpha)
        Button alpha;
        @BindView(R.id.description)
        TextView description;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
    public interface onItemClickListener{
        void onClick(Button button,int position);
    }
}
