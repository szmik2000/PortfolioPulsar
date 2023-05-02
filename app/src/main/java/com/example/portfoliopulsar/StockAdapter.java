package com.example.portfoliopulsar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

public class StockAdapter extends RecyclerView.Adapter<StockAdapter.ViewHolder> {
    private static List<Stock> stocks;
    private final OnItemClickListener onItemClickListener;

    public StockAdapter(List<Stock> stocks, OnItemClickListener onItemClickListener) {
        this.stocks = stocks;
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(Stock stock);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.stock_item, parent, false);
        return new ViewHolder(view, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Stock stock = stocks.get(position);
        holder.tickerSymbol.setText(stock.getTickerSymbol());
        holder.avgBuyPrice.setText(String.format("Avg Buy Price: $%.2f", stock.getAvgBuyPrice()));
        holder.amountInvested.setText(String.format("Amount Invested: $%.2f", stock.getAmountInvested()));
        TextView stockPriceTextView = holder.itemView.findViewById(R.id.stock_price);
        ProgressBar priceProgressBar = holder.itemView.findViewById(R.id.price_progress_bar);
        if (stock.isLoading()) {
            stockPriceTextView.setVisibility(View.GONE);
            priceProgressBar.setVisibility(View.VISIBLE);
        } else {
            stockPriceTextView.setVisibility(View.VISIBLE);
            priceProgressBar.setVisibility(View.GONE);
            stockPriceTextView.setText(String.format(Locale.US, "Current Price: $%.2f", stock.getPrice()));
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
        private final OnItemClickListener onItemClickListener;

        public ViewHolder(@NonNull View itemView, OnItemClickListener onItemClickListener) {
            super(itemView);
            tickerSymbol = itemView.findViewById(R.id.ticker_symbol);
            avgBuyPrice = itemView.findViewById(R.id.avg_buy_price);
            amountInvested = itemView.findViewById(R.id.amount_invested);
            this.onItemClickListener = onItemClickListener;
            itemView.setOnClickListener(this);
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
