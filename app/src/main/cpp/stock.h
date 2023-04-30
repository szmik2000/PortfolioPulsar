#pragma once

#include <string>

class Stock {
public:
    Stock(const std::string &symbol, int quantity, double purchasePrice);

    const std::string &getSymbol() const;
    int getQuantity() const;
    double getPurchasePrice() const;

private:
    std::string symbol;
    int quantity;
    double purchasePrice;
};