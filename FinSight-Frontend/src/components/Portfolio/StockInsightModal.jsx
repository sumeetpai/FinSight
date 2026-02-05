import { useEffect, useMemo, useState } from 'react';
import { X } from 'lucide-react';

const API_BASE_URL = 'http://localhost:8080/api/v1';

function CandleChart({ candles }) {
  const data = useMemo(() => {
    if (!candles || candles.length === 0) return [];
    return candles.slice(-60);
  }, [candles]);

  if (!data.length) {
    return (
      <div className="text-center text-gray-500 py-8">
        No historical data available.
      </div>
    );
  }

  const width = 640;
  const height = 260;
  const padding = 24;
  const highs = data.map(c => c.high);
  const lows = data.map(c => c.low);
  const maxHigh = Math.max(...highs);
  const minLow = Math.min(...lows);
  const range = maxHigh - minLow || 1;
  const xStep = data.length > 1 ? (width - padding * 2) / (data.length - 1) : 1;
  const candleWidth = Math.max(3, xStep * 0.6);

  const yFor = (value) =>
    padding + ((maxHigh - value) / range) * (height - padding * 2);

  return (
    <div className="w-full overflow-x-auto">
      <svg
        viewBox={`0 0 ${width} ${height}`}
        className="w-full h-64 bg-white rounded-xl border border-gray-200"
        role="img"
        aria-label="Candlestick chart"
      >
        <line x1={padding} y1={padding} x2={padding} y2={height - padding} stroke="#e5e7eb" />
        <line x1={padding} y1={height - padding} x2={width - padding} y2={height - padding} stroke="#e5e7eb" />
        {data.map((candle, i) => {
          const x = padding + i * xStep;
          const openY = yFor(candle.open);
          const closeY = yFor(candle.close);
          const highY = yFor(candle.high);
          const lowY = yFor(candle.low);
          const isUp = candle.close >= candle.open;
          const color = isUp ? '#16a34a' : '#dc2626';
          const rectY = Math.min(openY, closeY);
          const rectH = Math.max(1, Math.abs(openY - closeY));
          return (
            <g key={`${candle.time}-${i}`}>
              <line x1={x} y1={highY} x2={x} y2={lowY} stroke={color} strokeWidth="1" />
              <rect
                x={x - candleWidth / 2}
                y={rectY}
                width={candleWidth}
                height={rectH}
                fill={color}
                rx="1"
              />
            </g>
          );
        })}
      </svg>
    </div>
  );
}

export function StockInsightModal({ symbol, name, onClose }) {
  const [risk, setRisk] = useState(null);
  const [history, setHistory] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    let alive = true;
    const load = async () => {
      setLoading(true);
      setError('');
      try {
        const [riskResp, historyResp] = await Promise.all([
          fetch(`${API_BASE_URL}/market/risk/${encodeURIComponent(symbol)}`),
          fetch(`${API_BASE_URL}/market/history/${encodeURIComponent(symbol)}?period=1y&interval=1d`)
        ]);

        if (!riskResp.ok) {
          throw new Error(`Risk lookup failed (${riskResp.status})`);
        }
        if (!historyResp.ok) {
          throw new Error(`History lookup failed (${historyResp.status})`);
        }

        const [riskData, historyData] = await Promise.all([
          riskResp.json(),
          historyResp.json()
        ]);

        if (!alive) return;
        setRisk(riskData);
        setHistory(historyData);
      } catch (err) {
        if (!alive) return;
        setError(err.message || 'Failed to load stock insights.');
      } finally {
        if (alive) setLoading(false);
      }
    };
    load();
    return () => {
      alive = false;
    };
  }, [symbol]);

  return (
    <div className="fixed inset-0 bg-black/60 backdrop-blur-sm flex items-center justify-center p-4 z-50">
      <div className="bg-white/95 backdrop-blur-sm rounded-2xl w-full max-w-4xl p-6 shadow-2xl border border-white/20">
        <div className="flex items-center justify-between mb-6">
          <div>
            <h3 className="text-2xl font-bold text-gray-900">{symbol}</h3>
            {name && <p className="text-gray-600">{name}</p>}
          </div>
          <button
            onClick={onClose}
            className="p-2 bg-gray-100/80 rounded-xl hover:bg-gray-200/80 transition-all duration-200"
          >
            <X className="w-6 h-6 text-gray-600" />
          </button>
        </div>

        {loading ? (
          <div className="flex items-center justify-center h-64">
            <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
          </div>
        ) : error ? (
          <div className="text-center text-red-600 font-semibold py-12">{error}</div>
        ) : (
          <div className="space-y-6">
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
              <div className="bg-slate-50 rounded-xl border border-slate-200 p-4">
                <div className="text-xs uppercase tracking-wide text-slate-500 font-semibold">Risk Score</div>
                <div className="text-3xl font-bold text-slate-900 mt-2">{risk?.risk_score ?? '--'}</div>
                <div className="text-sm text-slate-600 mt-1">{risk?.risk_level ?? ''}</div>
              </div>
              <div className="md:col-span-2 bg-slate-50 rounded-xl border border-slate-200 p-4">
                <div className="text-xs uppercase tracking-wide text-slate-500 font-semibold">Risk Notes</div>
                <div className="text-sm text-slate-700 mt-2">
                  {risk?.meaning ?? 'No additional risk details available.'}
                </div>
              </div>
            </div>

            <div>
              <div className="text-sm font-semibold text-gray-700 mb-2">Live Candlestick (1Y / 1D)</div>
              <CandleChart candles={history?.candles || []} />
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
