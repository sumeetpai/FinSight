from pydantic import BaseModel
from typing import List, Dict, Optional


class StockPriceResponse(BaseModel):
    symbol: str
    price: Optional[float]
    currency: Optional[str]
    timestamp: int


class StockInfoResponse(BaseModel):
    symbol: str
    name: Optional[str]
    sector: Optional[str]
    industry: Optional[str]


class SymbolsRequest(BaseModel):
    symbols: List[str]


class BatchPriceResponse(BaseModel):
    prices: Dict[str, Optional[float]]
