CREATE TABLE IF NOT EXISTS energy_usage (
                                            id SERIAL PRIMARY KEY,
                                            community_name VARCHAR(255),
                                            production DOUBLE PRECISION NOT NULL,
                                            consumption DOUBLE PRECISION NOT NULL,
                                            kwh DOUBLE PRECISION,
                                            timestamp TIMESTAMP,
                                            grid_used DOUBLE PRECISION
);

INSERT INTO energy_usage (community_name, production, consumption, timestamp, grid_used) VALUES
                          ('Sonnenstadt', 2.0, 2.8, '2025-06-22T20:00:00', 0.8),
                          ('Winddorf', 1.5, 1.6, '2025-06-22T19:00:00', 0.1),
                          ('Solarhausen', 3.0, 3.7, '2025-06-22T18:00:00', 0.7),
                          ('Biostadt', 2.1, 2.5, '2025-06-22T17:00:00', 0.4),
                          ('Grünstetten', 2.9, 3.2, '2025-06-22T16:00:00', 0.3);
-- Tabelle: energy_record
CREATE TABLE IF NOT EXISTS energy_record (
                                             id SERIAL PRIMARY KEY,
                                             amount INTEGER NOT NULL,
                                             source VARCHAR(255),
                                             consumption DOUBLE PRECISION,
                                             production DOUBLE PRECISION
);

INSERT INTO energy_record (amount, source) VALUES
                                               (100, 'COMMUNITY'),
                                               (120, 'GRID'),
                                               (110, 'COMMUNITY'),
                                               (140, 'GRID'),
                                               (130, 'COMMUNITY');

-- Tabelle: percentage_record
CREATE TABLE IF NOT EXISTS percentage_record (
                                                 id SERIAL PRIMARY KEY,
                                                 percentage DOUBLE PRECISION NOT NULL
);

INSERT INTO percentage_record (percentage) VALUES
                                               (75.0),
                                               (82.5),
                                               (100.0),
                                               (68.4),
                                               (95.3);
