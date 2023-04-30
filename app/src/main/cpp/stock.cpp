#include "stock.h"

Stock::Stock(const std::string &symbol, int quantity, double purchasePrice)
        : symbol(symbol), quantity(quantity), purchasePrice(purchasePrice) {}

const std::string &Stock::getSymbol() const {
    return symbol;
}

int Stock::getQuantity() const {
    return quantity;
}

double Stock::getPurchasePrice() const {
    return purchasePrice;
}