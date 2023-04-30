#pragma once

#include <vector>
#include "stock.h"

class Portfolio {
public:
    void addStock(const Stock &stock);
    void removeStock(const std::string &symbol);
    // Add more methods as needed

private:
    std::vector<Stock> stocks;
};