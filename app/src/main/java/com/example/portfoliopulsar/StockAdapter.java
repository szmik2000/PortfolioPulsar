package com.example.portfoliopulsar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

public class StockAdapter extends RecyclerView.Adapter<StockAdapter.ViewHolder> {
    private static List<Stock> stocks;
    private final OnItemClickListener onItemClickListener;
    private final OnItemLongClickListener onItemLongClickListener;


    public StockAdapter(List<Stock> stocks, OnItemClickListener onItemClickListener, OnItemLongClickListener onItemLongClickListener) {
        this.stocks = stocks;
        this.onItemClickListener = onItemClickListener;
        this.onItemLongClickListener = onItemLongClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(Stock stock);
        void onAddPositionClick(Stock stock); // Add this line
    }

    public interface OnItemLongClickListener {
        boolean onItemLongClick(Stock stock);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.stock_item, parent, false);
        return new ViewHolder(view, onItemClickListener, onItemLongClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Stock stock = stocks.get(position);
        holder.tickerSymbol.setText("$" + stock.getTickerSymbol().toUpperCase());
        holder.avgBuyPrice.setText(String.format("Avg Buy Price: $%.2f", stock.getAvgBuyPrice()));
        holder.amountInvested.setText(String.format("Amount Invested: $%.2f", stock.getAmountInvested()));
        TextView stockPriceTextView = holder.itemView.findViewById(R.id.stock_price);
        ProgressBar priceProgressBar = holder.itemView.findViewById(R.id.price_progress_bar);
        TextView percentageGainLoss = holder.itemView.findViewById(R.id.percentage_gain_loss);
        if (stock.isLoading()) {
            stockPriceTextView.setVisibility(View.GONE);
            percentageGainLoss.setVisibility(View.GONE);
            priceProgressBar.setVisibility(View.VISIBLE);
        } else {
            stockPriceTextView.setText(String.format(Locale.US, "Current Price: $%.2f", stock.getPrice()));
            double gainLoss = stock.getPercentageGainLoss();
            percentageGainLoss.setText(String.format("Gain/Loss: %+,.2f%%", gainLoss));
            stockPriceTextView.setVisibility(View.VISIBLE);
            percentageGainLoss.setVisibility(View.VISIBLE);
            priceProgressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return stocks.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView tickerSymbol;
        public TextView avgBuyPrice;
        public TextView amountInvested;
        public ImageView addPositionIcon;
        private final OnItemClickListener onItemClickListener;
        private final OnItemLongClickListener onItemLongClickListener;


        public ViewHolder(@NonNull View itemView, OnItemClickListener onItemClickListener, OnItemLongClickListener onItemLongClickListener) {
            super(itemView);
            tickerSymbol = itemView.findViewById(R.id.ticker_symbol);
            avgBuyPrice = itemView.findViewById(R.id.avg_buy_price);
            amountInvested = itemView.findViewById(R.id.amount_invested);
            addPositionIcon = itemView.findViewById(R.id.add_position_icon);
            this.onItemClickListener = onItemClickListener;
            this.onItemLongClickListener = onItemLongClickListener;
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        return onItemLongClickListener.onItemLongClick(stocks.get(position));
                    }
                    return false;
                }
            });
            addPositionIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        onItemClickListener.onAddPositionClick(stocks.get(position));
                    }
                }
            });
        }


        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                onItemClickListener.onItemClick(stocks.get(position));
            }
        }
    }
}
