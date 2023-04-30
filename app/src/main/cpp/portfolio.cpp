#include "portfolio.h"

void Portfolio::addStock(const Stock &stock) {
    stocks.push_back(stock);
}

void Portfolio::removeStock(const std::string &symbol) {
    stocks.erase(std::remove_if(stocks.begin(), stocks.end(),
                                [&symbol](const Stock &s) { return s.getSymbol() == symbol; }),
                 stocks.end());
}