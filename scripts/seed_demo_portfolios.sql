-- Demo seed: portfolios + stocks for client walkthroughs
-- Usage (MySQL): source scripts/seed_demo_portfolios.sql;
-- Update this if your demo user is different:
SET @user_id = 4;

-- 1) Ensure demo stocks exist (idempotent)
INSERT INTO stocks (stock_sym, name, current_price, day_before_price, market_cap)
SELECT 'AAPL','Apple',190,187,2900000000000
WHERE NOT EXISTS (SELECT 1 FROM stocks WHERE stock_sym = 'AAPL');
INSERT INTO stocks (stock_sym, name, current_price, day_before_price, market_cap)
SELECT 'MSFT','Microsoft',410,405,3100000000000
WHERE NOT EXISTS (SELECT 1 FROM stocks WHERE stock_sym = 'MSFT');
INSERT INTO stocks (stock_sym, name, current_price, day_before_price, market_cap)
SELECT 'GOOGL','Alphabet',165,166,2100000000000
WHERE NOT EXISTS (SELECT 1 FROM stocks WHERE stock_sym = 'GOOGL');
INSERT INTO stocks (stock_sym, name, current_price, day_before_price, market_cap)
SELECT 'NVDA','NVIDIA',720,705,1800000000000
WHERE NOT EXISTS (SELECT 1 FROM stocks WHERE stock_sym = 'NVDA');

INSERT INTO stocks (stock_sym, name, current_price, day_before_price, market_cap)
SELECT 'XOM','Exxon Mobil',106,109,420000000000
WHERE NOT EXISTS (SELECT 1 FROM stocks WHERE stock_sym = 'XOM');
INSERT INTO stocks (stock_sym, name, current_price, day_before_price, market_cap)
SELECT 'CVX','Chevron',150,152,300000000000
WHERE NOT EXISTS (SELECT 1 FROM stocks WHERE stock_sym = 'CVX');
INSERT INTO stocks (stock_sym, name, current_price, day_before_price, market_cap)
SELECT 'COP','ConocoPhillips',108,110,140000000000
WHERE NOT EXISTS (SELECT 1 FROM stocks WHERE stock_sym = 'COP');

INSERT INTO stocks (stock_sym, name, current_price, day_before_price, market_cap)
SELECT 'LMT','Lockheed Martin',470,465,110000000000
WHERE NOT EXISTS (SELECT 1 FROM stocks WHERE stock_sym = 'LMT');
INSERT INTO stocks (stock_sym, name, current_price, day_before_price, market_cap)
SELECT 'NOC','Northrop Grumman',470,475,75000000000
WHERE NOT EXISTS (SELECT 1 FROM stocks WHERE stock_sym = 'NOC');
INSERT INTO stocks (stock_sym, name, current_price, day_before_price, market_cap)
SELECT 'RTX','RTX Corporation',88,90,120000000000
WHERE NOT EXISTS (SELECT 1 FROM stocks WHERE stock_sym = 'RTX');

INSERT INTO stocks (stock_sym, name, current_price, day_before_price, market_cap)
SELECT 'V','Visa',280,276,580000000000
WHERE NOT EXISTS (SELECT 1 FROM stocks WHERE stock_sym = 'V');
INSERT INTO stocks (stock_sym, name, current_price, day_before_price, market_cap)
SELECT 'MA','Mastercard',460,455,470000000000
WHERE NOT EXISTS (SELECT 1 FROM stocks WHERE stock_sym = 'MA');
INSERT INTO stocks (stock_sym, name, current_price, day_before_price, market_cap)
SELECT 'PYPL','PayPal',62,64,70000000000
WHERE NOT EXISTS (SELECT 1 FROM stocks WHERE stock_sym = 'PYPL');

INSERT INTO stocks (stock_sym, name, current_price, day_before_price, market_cap)
SELECT 'LLY','Eli Lilly',830,820,780000000000
WHERE NOT EXISTS (SELECT 1 FROM stocks WHERE stock_sym = 'LLY');
INSERT INTO stocks (stock_sym, name, current_price, day_before_price, market_cap)
SELECT 'UNH','UnitedHealth',520,518,480000000000
WHERE NOT EXISTS (SELECT 1 FROM stocks WHERE stock_sym = 'UNH');
INSERT INTO stocks (stock_sym, name, current_price, day_before_price, market_cap)
SELECT 'JNJ','Johnson & Johnson',158,159,410000000000
WHERE NOT EXISTS (SELECT 1 FROM stocks WHERE stock_sym = 'JNJ');

-- 2) Create demo portfolios (idempotent)
INSERT INTO portfolio (name, description, active, user_id, total_value, cost_basis, `yield`)
SELECT 'Big Tech Compounder',
       'Durable moats, strong cash flow, and AI leverage. Long-term compounders.',
       1, @user_id, 0, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM portfolio WHERE name = 'Big Tech Compounder' AND user_id = @user_id);

INSERT INTO portfolio (name, description, active, user_id, total_value, cost_basis, `yield`)
SELECT 'Energy Income',
       'Cash-generative majors with disciplined capex and dividend focus.',
       1, @user_id, 0, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM portfolio WHERE name = 'Energy Income' AND user_id = @user_id);

INSERT INTO portfolio (name, description, active, user_id, total_value, cost_basis, `yield`)
SELECT 'Defense & Security',
       'Defense primes with multi-year backlog and secular demand.',
       1, @user_id, 0, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM portfolio WHERE name = 'Defense & Security' AND user_id = @user_id);

INSERT INTO portfolio (name, description, active, user_id, total_value, cost_basis, `yield`)
SELECT 'Payments & Fintech',
       'Network effects and global payments growth; mix of winners and laggards.',
       1, @user_id, 0, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM portfolio WHERE name = 'Payments & Fintech' AND user_id = @user_id);

INSERT INTO portfolio (name, description, active, user_id, total_value, cost_basis, `yield`)
SELECT 'Healthcare Innovation',
       'Pharma + services exposure to durable healthcare demand.',
       1, @user_id, 0, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM portfolio WHERE name = 'Healthcare Innovation' AND user_id = @user_id);

-- 3) Resolve portfolio and stock ids
SELECT portfolio_id INTO @p_big_tech
FROM portfolio WHERE name = 'Big Tech Compounder' AND user_id = @user_id ORDER BY portfolio_id DESC LIMIT 1;
SELECT portfolio_id INTO @p_energy
FROM portfolio WHERE name = 'Energy Income' AND user_id = @user_id ORDER BY portfolio_id DESC LIMIT 1;
SELECT portfolio_id INTO @p_defense
FROM portfolio WHERE name = 'Defense & Security' AND user_id = @user_id ORDER BY portfolio_id DESC LIMIT 1;
SELECT portfolio_id INTO @p_payments
FROM portfolio WHERE name = 'Payments & Fintech' AND user_id = @user_id ORDER BY portfolio_id DESC LIMIT 1;
SELECT portfolio_id INTO @p_health
FROM portfolio WHERE name = 'Healthcare Innovation' AND user_id = @user_id ORDER BY portfolio_id DESC LIMIT 1;

SELECT stock_id INTO @s_aapl FROM stocks WHERE stock_sym = 'AAPL';
SELECT stock_id INTO @s_msft FROM stocks WHERE stock_sym = 'MSFT';
SELECT stock_id INTO @s_googl FROM stocks WHERE stock_sym = 'GOOGL';
SELECT stock_id INTO @s_nvda FROM stocks WHERE stock_sym = 'NVDA';
SELECT stock_id INTO @s_xom FROM stocks WHERE stock_sym = 'XOM';
SELECT stock_id INTO @s_cvx FROM stocks WHERE stock_sym = 'CVX';
SELECT stock_id INTO @s_cop FROM stocks WHERE stock_sym = 'COP';
SELECT stock_id INTO @s_lmt FROM stocks WHERE stock_sym = 'LMT';
SELECT stock_id INTO @s_noc FROM stocks WHERE stock_sym = 'NOC';
SELECT stock_id INTO @s_rtx FROM stocks WHERE stock_sym = 'RTX';
SELECT stock_id INTO @s_v FROM stocks WHERE stock_sym = 'V';
SELECT stock_id INTO @s_ma FROM stocks WHERE stock_sym = 'MA';
SELECT stock_id INTO @s_pypl FROM stocks WHERE stock_sym = 'PYPL';
SELECT stock_id INTO @s_lly FROM stocks WHERE stock_sym = 'LLY';
SELECT stock_id INTO @s_unh FROM stocks WHERE stock_sym = 'UNH';
SELECT stock_id INTO @s_jnj FROM stocks WHERE stock_sym = 'JNJ';

-- 4) Seed holdings (portfolio_stock) idempotently
INSERT INTO portfolio_stock (portfolio_id, stock_id, quantity)
SELECT @p_big_tech, @s_aapl, 18
WHERE NOT EXISTS (SELECT 1 FROM portfolio_stock WHERE portfolio_id = @p_big_tech AND stock_id = @s_aapl);
INSERT INTO portfolio_stock (portfolio_id, stock_id, quantity)
SELECT @p_big_tech, @s_msft, 10
WHERE NOT EXISTS (SELECT 1 FROM portfolio_stock WHERE portfolio_id = @p_big_tech AND stock_id = @s_msft);
INSERT INTO portfolio_stock (portfolio_id, stock_id, quantity)
SELECT @p_big_tech, @s_googl, 22
WHERE NOT EXISTS (SELECT 1 FROM portfolio_stock WHERE portfolio_id = @p_big_tech AND stock_id = @s_googl);
INSERT INTO portfolio_stock (portfolio_id, stock_id, quantity)
SELECT @p_big_tech, @s_nvda, 6
WHERE NOT EXISTS (SELECT 1 FROM portfolio_stock WHERE portfolio_id = @p_big_tech AND stock_id = @s_nvda);

INSERT INTO portfolio_stock (portfolio_id, stock_id, quantity)
SELECT @p_energy, @s_xom, 30
WHERE NOT EXISTS (SELECT 1 FROM portfolio_stock WHERE portfolio_id = @p_energy AND stock_id = @s_xom);
INSERT INTO portfolio_stock (portfolio_id, stock_id, quantity)
SELECT @p_energy, @s_cvx, 18
WHERE NOT EXISTS (SELECT 1 FROM portfolio_stock WHERE portfolio_id = @p_energy AND stock_id = @s_cvx);
INSERT INTO portfolio_stock (portfolio_id, stock_id, quantity)
SELECT @p_energy, @s_cop, 20
WHERE NOT EXISTS (SELECT 1 FROM portfolio_stock WHERE portfolio_id = @p_energy AND stock_id = @s_cop);

INSERT INTO portfolio_stock (portfolio_id, stock_id, quantity)
SELECT @p_defense, @s_lmt, 8
WHERE NOT EXISTS (SELECT 1 FROM portfolio_stock WHERE portfolio_id = @p_defense AND stock_id = @s_lmt);
INSERT INTO portfolio_stock (portfolio_id, stock_id, quantity)
SELECT @p_defense, @s_noc, 6
WHERE NOT EXISTS (SELECT 1 FROM portfolio_stock WHERE portfolio_id = @p_defense AND stock_id = @s_noc);
INSERT INTO portfolio_stock (portfolio_id, stock_id, quantity)
SELECT @p_defense, @s_rtx, 25
WHERE NOT EXISTS (SELECT 1 FROM portfolio_stock WHERE portfolio_id = @p_defense AND stock_id = @s_rtx);

INSERT INTO portfolio_stock (portfolio_id, stock_id, quantity)
SELECT @p_payments, @s_v, 16
WHERE NOT EXISTS (SELECT 1 FROM portfolio_stock WHERE portfolio_id = @p_payments AND stock_id = @s_v);
INSERT INTO portfolio_stock (portfolio_id, stock_id, quantity)
SELECT @p_payments, @s_ma, 10
WHERE NOT EXISTS (SELECT 1 FROM portfolio_stock WHERE portfolio_id = @p_payments AND stock_id = @s_ma);
INSERT INTO portfolio_stock (portfolio_id, stock_id, quantity)
SELECT @p_payments, @s_pypl, 40
WHERE NOT EXISTS (SELECT 1 FROM portfolio_stock WHERE portfolio_id = @p_payments AND stock_id = @s_pypl);

INSERT INTO portfolio_stock (portfolio_id, stock_id, quantity)
SELECT @p_health, @s_lly, 6
WHERE NOT EXISTS (SELECT 1 FROM portfolio_stock WHERE portfolio_id = @p_health AND stock_id = @s_lly);
INSERT INTO portfolio_stock (portfolio_id, stock_id, quantity)
SELECT @p_health, @s_unh, 9
WHERE NOT EXISTS (SELECT 1 FROM portfolio_stock WHERE portfolio_id = @p_health AND stock_id = @s_unh);
INSERT INTO portfolio_stock (portfolio_id, stock_id, quantity)
SELECT @p_health, @s_jnj, 14
WHERE NOT EXISTS (SELECT 1 FROM portfolio_stock WHERE portfolio_id = @p_health AND stock_id = @s_jnj);

-- 5) Update portfolio totals based on holdings
UPDATE portfolio p
JOIN (
  SELECT ps.portfolio_id, SUM(ps.quantity * s.current_price) AS total_value
  FROM portfolio_stock ps
  JOIN stocks s ON s.stock_id = ps.stock_id
  GROUP BY ps.portfolio_id
) v ON v.portfolio_id = p.portfolio_id
SET p.total_value = v.total_value
WHERE p.portfolio_id IN (@p_big_tech, @p_energy, @p_defense, @p_payments, @p_health);

-- 6) Set cost_basis to force mixed gains/losses for demo
UPDATE portfolio
SET cost_basis = CASE name
  WHEN 'Big Tech Compounder' THEN total_value * 0.85  -- gain
  WHEN 'Energy Income' THEN total_value * 1.10        -- loss
  WHEN 'Defense & Security' THEN total_value * 0.95   -- modest gain
  WHEN 'Payments & Fintech' THEN total_value * 1.20   -- loss
  WHEN 'Healthcare Innovation' THEN total_value * 0.90 -- gain
  ELSE cost_basis
END
WHERE name IN ('Big Tech Compounder','Energy Income','Defense & Security','Payments & Fintech','Healthcare Innovation')
  AND user_id = @user_id;

-- Optional: set yield to zero for clean demo view
UPDATE portfolio
SET `yield` = 0
WHERE name IN ('Big Tech Compounder','Energy Income','Defense & Security','Payments & Fintech','Healthcare Innovation')
  AND user_id = @user_id;
