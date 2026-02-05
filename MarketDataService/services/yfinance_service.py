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


def get_stock_risk(symbol: str):
    info = yf.Ticker(symbol).info

    beta = info.get("beta", 1)
    debt_to_equity = info.get("debtToEquity", 0)
    profit_margin = info.get("profitMargins", 0)
    market_cap = info.get("marketCap", 0)

    score = 0

    # Volatility
    if beta:
        score += min(beta * 20, 30)

    # Financial leverage
    if debt_to_equity:
        score += min(debt_to_equity / 2, 25)

    # Profitability
    if profit_margin:
        score += max(20 - profit_margin * 100, 0)

    # Market cap safety
    if market_cap < 10_000_000_000:
        score += 15

    score = min(int(score), 100)

    if score <= 30:
        level = "LOW"
        meaning = "Stable stock with relatively low volatility and strong fundamentals."
    elif score <= 60:
        level = "MEDIUM"
        meaning = "Moderate risk due to market volatility or financial factors."
    else:
        level = "HIGH"
        meaning = "High volatility or financial risk; suitable for aggressive investors."

    return score, level, meaning


def get_stock_history(symbol: str, period="1y", interval="1d"):
    ticker = yf.Ticker(symbol)
    hist = ticker.history(period=period, interval=interval)

    candles = []
    for index, row in hist.iterrows():
        candles.append({
            "time": int(index.timestamp()),
            "open": round(row["Open"], 2),
            "high": round(row["High"], 2),
            "low": round(row["Low"], 2),
            "close": round(row["Close"], 2),
            "volume": int(row["Volume"])
        })

    return candles
