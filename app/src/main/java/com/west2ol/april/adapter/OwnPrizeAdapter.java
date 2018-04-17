package com.west2ol.april.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.west2ol.april.R;
import com.west2ol.april.entity.receive.PrizeListInfo;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OwnPrizeAdapter extends RecyclerView.Adapter<OwnPrizeAdapter.ViewHolder>{
    private List<PrizeListInfo.PrizeBean> prizeBeanList;
    private Context mContext;
    public OwnPrizeAdapter(Context mContext, List<PrizeListInfo.PrizeBean> prizeBeanList) {
        this.mContext=mContext;
        this.prizeBeanList=prizeBeanList;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.layout_item_ownprize,parent,false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PrizeListInfo.PrizeBean prizeBean=prizeBeanList.get(position);
        holder.description.setText(prizeBean.getDescription());
        holder.amount.setText("x"+prizeBean.getAmount());
    }

    @Override
    public int getItemCount() {
        return prizeBeanList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.description)
        TextView description;
        @BindView(R.id.amount)
        TextView amount;
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
