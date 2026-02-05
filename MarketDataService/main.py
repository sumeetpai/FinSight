from fastapi import FastAPI
import time

from services.yfinance_service import (
    get_current_price,
    get_stock_info,
    get_multiple_prices
)

from models.responses import (
    StockPriceResponse,
    StockInfoResponse,
    SymbolsRequest,
    BatchPriceResponse
)

from fastapi.middleware.cors import CORSMiddleware

app = FastAPI(title="Market Data Service")

# Enable CORS for local frontend running at http://localhost:5173
origins = [
    "http://localhost:5173",
    "http://localhost:63342",
]

app.add_middleware(
    CORSMiddleware,
    allow_origins=origins,
    allow_credentials=True,
    allow_methods=["GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"],
    allow_headers=["*"],
)


@app.get(
    "/stock/price/{symbol}",
    response_model=StockPriceResponse
)
def stock_price(symbol: str):
    price, currency = get_current_price(symbol)

    return StockPriceResponse(
        symbol=symbol.upper(),
        price=price,
        currency=currency,
        timestamp=int(time.time())
    )


@app.get(
    "/stock/info/{symbol}",
    response_model=StockInfoResponse
)
def stock_info(symbol: str):
    info = get_stock_info(symbol)

    return StockInfoResponse(
        symbol=info.get("symbol"),
        name=info.get("name"),
        sector=info.get("sector"),
        industry=info.get("industry")
    )


@app.post(
    "/stocks/prices",
    response_model=BatchPriceResponse
)
def stocks_prices(request: SymbolsRequest):
    prices = get_multiple_prices(request.symbols)

    return BatchPriceResponse(
        prices=prices
    )
