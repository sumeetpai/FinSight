import yfinance as yf

def get_current_price(symbol: str):
    ticker = yf.Ticker(symbol)
    price = ticker.info.get("currentPrice")
    currency = ticker.info.get("currency")
    return price, currency


def get_stock_info(symbol: str):
    info = yf.Ticker(symbol).info
    return {
        "symbol": symbol,
        "name": info.get("longName"),
        "sector": info.get("sector"),
        "industry": info.get("industry")
    }


def get_multiple_prices(symbols: list[str]):
    result = {}
    for symbol in symbols:
        try:
            price, _ = get_current_price(symbol)
            result[symbol] = price
        except Exception:
            result[symbol] = None
    return result
