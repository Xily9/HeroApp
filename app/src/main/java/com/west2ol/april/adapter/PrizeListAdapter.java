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
import com.west2ol.april.entity.PrizeListInfo;
import com.west2ol.april.entity.QuestionsInfo;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PrizeListAdapter extends RecyclerView.Adapter<PrizeListAdapter.ViewHolder>{
    private List<PrizeListInfo.PrizeBean> prizeBeanList;
    private Context mContext;
    public PrizeListAdapter(Context mContext, List<PrizeListInfo.PrizeBean> prizeBeanList) {
        this.mContext=mContext;
        this.prizeBeanList=prizeBeanList;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.layout_item_prize,parent,false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PrizeListInfo.PrizeBean prizeBean=prizeBeanList.get(position);
        holder.description.setText(prizeBean.getDescription());
        holder.probability.setText(prizeBean.getProbability()+"%");
        holder.amount.setText(String.valueOf(prizeBean.getAmount()));
    }

    @Override
    public int getItemCount() {
        return prizeBeanList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.description)
        TextView description;
        @BindView(R.id.probability)
        TextView probability;
        @BindView(R.id.amount)
        TextView amount;
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
