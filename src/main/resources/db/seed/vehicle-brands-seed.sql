-- =====================================================
-- AutoNexo - Vehicle Brands and Models Seed Data
-- =====================================================
-- This script populates the vehicle_brand and vehicle_model tables
-- with common brands and popular models for demo purposes.
-- =====================================================

-- =====================================================
-- VEHICLE BRANDS
-- =====================================================


-- NOTA DE NUEVO DEV: ESTO NUNCA FUNCIONO, EL SEEDING LO HACEN EN INFRASTRUCTURE XDDD, me trolearon

INSERT INTO vehicle_brand (name, logo_url, country, is_active, popular, created_at, updated_at) VALUES
-- Japanese Brands (Popular)
('Toyota', NULL, 'Japan', true, true, NOW(), NOW()),
('Honda', NULL, 'Japan', true, true, NOW(), NOW()),
('Nissan', NULL, 'Japan', true, true, NOW(), NOW()),
('Mazda', NULL, 'Japan', true, true, NOW(), NOW()),
('Suzuki', NULL, 'Japan', true, true, NOW(), NOW()),
('Mitsubishi', NULL, 'Japan', true, true, NOW(), NOW()),
('Subaru', NULL, 'Japan', true, false, NOW(), NOW()),
('Isuzu', NULL, 'Japan', true, false, NOW(), NOW()),

-- American Brands (Popular)
('Ford', NULL, 'USA', true, true, NOW(), NOW()),
('Chevrolet', NULL, 'USA', true, true, NOW(), NOW()),
('Jeep', NULL, 'USA', true, true, NOW(), NOW()),
('Dodge', NULL, 'USA', true, false, NOW(), NOW()),
('RAM', NULL, 'USA', true, false, NOW(), NOW()),
('GMC', NULL, 'USA', true, false, NOW(), NOW()),
('Cadillac', NULL, 'USA', true, false, NOW(), NOW()),
('Tesla', NULL, 'USA', true, true, NOW(), NOW()),

-- Korean Brands (Popular)
('Hyundai', NULL, 'South Korea', true, true, NOW(), NOW()),
('Kia', NULL, 'South Korea', true, true, NOW(), NOW()),
('Genesis', NULL, 'South Korea', true, false, NOW(), NOW()),

-- German Brands (Popular)
('Volkswagen', NULL, 'Germany', true, true, NOW(), NOW()),
('BMW', NULL, 'Germany', true, true, NOW(), NOW()),
('Mercedes-Benz', NULL, 'Germany', true, true, NOW(), NOW()),
('Audi', NULL, 'Germany', true, true, NOW(), NOW()),
('Porsche', NULL, 'Germany', true, false, NOW(), NOW()),

-- French Brands
('Peugeot', NULL, 'France', true, true, NOW(), NOW()),
('Renault', NULL, 'France', true, false, NOW(), NOW()),
('Citroën', NULL, 'France', true, false, NOW(), NOW()),

-- Italian Brands
('Fiat', NULL, 'Italy', true, false, NOW(), NOW()),

-- British Brands
('Land Rover', NULL, 'UK', true, false, NOW(), NOW()),

-- Chinese Brands
('Chery', NULL, 'China', true, true, NOW(), NOW()),
('JAC', NULL, 'China', true, false, NOW(), NOW()),
('Great Wall', NULL, 'China', true, false, NOW(), NOW()),
('BYD', NULL, 'China', true, false, NOW(), NOW());

-- =====================================================
-- VEHICLE MODELS
-- =====================================================

-- Toyota Models (Brand ID will be 1)
INSERT INTO vehicle_model (brand_id, name, start_year, end_year, is_active, created_at, updated_at) VALUES
(1, 'Corolla', 1966, NULL, true, NOW(), NOW()),
(1, 'Camry', 1982, NULL, true, NOW(), NOW()),
(1, 'RAV4', 1994, NULL, true, NOW(), NOW()),
(1, 'Hilux', 1968, NULL, true, NOW(), NOW()),
(1, 'Yaris', 1999, NULL, true, NOW(), NOW()),
(1, 'Land Cruiser', 1951, NULL, true, NOW(), NOW()),
(1, 'Prius', 1997, NULL, true, NOW(), NOW()),
(1, 'Tacoma', 1995, NULL, true, NOW(), NOW()),
(1, 'Highlander', 2000, NULL, true, NOW(), NOW()),
(1, 'Sienna', 1997, NULL, true, NOW(), NOW()),
(1, 'Avalon', 1994, NULL, true, NOW(), NOW()),
(1, '4Runner', 1984, NULL, true, NOW(), NOW()),

-- Honda Models (Brand ID will be 2)
(2, 'Civic', 1972, NULL, true, NOW(), NOW()),
(2, 'Accord', 1976, NULL, true, NOW(), NOW()),
(2, 'CR-V', 1995, NULL, true, NOW(), NOW()),
(2, 'Fit', 2001, NULL, true, NOW(), NOW()),
(2, 'HR-V', 1998, NULL, true, NOW(), NOW()),
(2, 'Pilot', 2002, NULL, true, NOW(), NOW()),
(2, 'Odyssey', 1994, NULL, true, NOW(), NOW()),
(2, 'Ridgeline', 2005, NULL, true, NOW(), NOW()),

-- Nissan Models (Brand ID will be 3)
(3, 'Sentra', 1982, NULL, true, NOW(), NOW()),
(3, 'Altima', 1992, NULL, true, NOW(), NOW()),
(3, 'Versa', 2006, NULL, true, NOW(), NOW()),
(3, 'X-Trail', 2000, NULL, true, NOW(), NOW()),
(3, 'Kicks', 2016, NULL, true, NOW(), NOW()),
(3, 'Frontier', 1997, NULL, true, NOW(), NOW()),
(3, 'Pathfinder', 1985, NULL, true, NOW(), NOW()),
(3, 'Murano', 2002, NULL, true, NOW(), NOW()),
(3, '370Z', 2008, 2020, true, NOW(), NOW()),

-- Mazda Models (Brand ID will be 4)
(4, 'Mazda3', 2003, NULL, true, NOW(), NOW()),
(4, 'Mazda6', 2002, NULL, true, NOW(), NOW()),
(4, 'CX-3', 2015, NULL, true, NOW(), NOW()),
(4, 'CX-5', 2012, NULL, true, NOW(), NOW()),
(4, 'CX-9', 2006, NULL, true, NOW(), NOW()),
(4, 'MX-5 Miata', 1989, NULL, true, NOW(), NOW()),
(4, 'BT-50', 2006, NULL, true, NOW(), NOW()),

-- Suzuki Models (Brand ID will be 5)
(5, 'Swift', 2004, NULL, true, NOW(), NOW()),
(5, 'Vitara', 1988, NULL, true, NOW(), NOW()),
(5, 'Baleno', 1995, NULL, true, NOW(), NOW()),
(5, 'Jimny', 1970, NULL, true, NOW(), NOW()),
(5, 'Alto', 1979, NULL, true, NOW(), NOW()),

-- Ford Models (Brand ID will be 9)
(9, 'F-150', 1948, NULL, true, NOW(), NOW()),
(9, 'Mustang', 1964, NULL, true, NOW(), NOW()),
(9, 'Explorer', 1990, NULL, true, NOW(), NOW()),
(9, 'Escape', 2000, NULL, true, NOW(), NOW()),
(9, 'Ranger', 1982, NULL, true, NOW(), NOW()),
(9, 'Edge', 2006, NULL, true, NOW(), NOW()),
(9, 'Expedition', 1996, NULL, true, NOW(), NOW()),
(9, 'Bronco', 1965, NULL, true, NOW(), NOW()),
(9, 'Fusion', 2005, 2020, true, NOW(), NOW()),

-- Chevrolet Models (Brand ID will be 10)
(10, 'Silverado', 1998, NULL, true, NOW(), NOW()),
(10, 'Equinox', 2004, NULL, true, NOW(), NOW()),
(10, 'Traverse', 2008, NULL, true, NOW(), NOW()),
(10, 'Tahoe', 1994, NULL, true, NOW(), NOW()),
(10, 'Malibu', 1964, NULL, true, NOW(), NOW()),
(10, 'Camaro', 1966, NULL, true, NOW(), NOW()),
(10, 'Colorado', 2004, NULL, true, NOW(), NOW()),
(10, 'Blazer', 1969, NULL, true, NOW(), NOW()),
(10, 'Suburban', 1934, NULL, true, NOW(), NOW()),
(10, 'Corvette', 1953, NULL, true, NOW(), NOW()),

-- Jeep Models (Brand ID will be 11)
(11, 'Wrangler', 1986, NULL, true, NOW(), NOW()),
(11, 'Cherokee', 1974, NULL, true, NOW(), NOW()),
(11, 'Grand Cherokee', 1992, NULL, true, NOW(), NOW()),
(11, 'Compass', 2006, NULL, true, NOW(), NOW()),
(11, 'Renegade', 2014, NULL, true, NOW(), NOW()),
(11, 'Gladiator', 2019, NULL, true, NOW(), NOW()),

-- Hyundai Models (Brand ID will be 17)
(17, 'Accent', 1994, NULL, true, NOW(), NOW()),
(17, 'Elantra', 1990, NULL, true, NOW(), NOW()),
(17, 'Sonata', 1985, NULL, true, NOW(), NOW()),
(17, 'Tucson', 2004, NULL, true, NOW(), NOW()),
(17, 'Santa Fe', 2000, NULL, true, NOW(), NOW()),
(17, 'Kona', 2017, NULL, true, NOW(), NOW()),
(17, 'Palisade', 2018, NULL, true, NOW(), NOW()),
(17, 'Venue', 2019, NULL, true, NOW(), NOW()),

-- Kia Models (Brand ID will be 18)
(18, 'Rio', 1999, NULL, true, NOW(), NOW()),
(18, 'Forte', 2008, NULL, true, NOW(), NOW()),
(18, 'Optima', 2000, NULL, true, NOW(), NOW()),
(18, 'Sportage', 1993, NULL, true, NOW(), NOW()),
(18, 'Sorento', 2002, NULL, true, NOW(), NOW()),
(18, 'Seltos', 2019, NULL, true, NOW(), NOW()),
(18, 'Telluride', 2019, NULL, true, NOW(), NOW()),
(18, 'Soul', 2008, NULL, true, NOW(), NOW()),

-- Volkswagen Models (Brand ID will be 20)
(20, 'Golf', 1974, NULL, true, NOW(), NOW()),
(20, 'Jetta', 1979, NULL, true, NOW(), NOW()),
(20, 'Tiguan', 2007, NULL, true, NOW(), NOW()),
(20, 'Passat', 1973, NULL, true, NOW(), NOW()),
(20, 'Atlas', 2017, NULL, true, NOW(), NOW()),
(20, 'Beetle', 1938, 2019, true, NOW(), NOW()),
(20, 'Touareg', 2002, NULL, true, NOW(), NOW()),

-- BMW Models (Brand ID will be 21)
(21, '3 Series', 1975, NULL, true, NOW(), NOW()),
(21, '5 Series', 1972, NULL, true, NOW(), NOW()),
(21, 'X1', 2009, NULL, true, NOW(), NOW()),
(21, 'X3', 2003, NULL, true, NOW(), NOW()),
(21, 'X5', 1999, NULL, true, NOW(), NOW()),
(21, 'X7', 2018, NULL, true, NOW(), NOW()),
(21, '7 Series', 1977, NULL, true, NOW(), NOW()),

-- Mercedes-Benz Models (Brand ID will be 22)
(22, 'C-Class', 1993, NULL, true, NOW(), NOW()),
(22, 'E-Class', 1953, NULL, true, NOW(), NOW()),
(22, 'GLA', 2013, NULL, true, NOW(), NOW()),
(22, 'GLC', 2015, NULL, true, NOW(), NOW()),
(22, 'GLE', 1997, NULL, true, NOW(), NOW()),
(22, 'S-Class', 1972, NULL, true, NOW(), NOW()),
(22, 'A-Class', 1997, NULL, true, NOW(), NOW()),

-- Audi Models (Brand ID will be 23)
(23, 'A3', 1996, NULL, true, NOW(), NOW()),
(23, 'A4', 1994, NULL, true, NOW(), NOW()),
(23, 'Q3', 2011, NULL, true, NOW(), NOW()),
(23, 'Q5', 2008, NULL, true, NOW(), NOW()),
(23, 'Q7', 2005, NULL, true, NOW(), NOW()),
(23, 'A6', 1994, NULL, true, NOW(), NOW()),

-- Peugeot Models (Brand ID will be 25)
(25, '208', 2012, NULL, true, NOW(), NOW()),
(25, '308', 2007, NULL, true, NOW(), NOW()),
(25, '3008', 2008, NULL, true, NOW(), NOW()),
(25, '5008', 2009, NULL, true, NOW(), NOW()),
(25, '2008', 2013, NULL, true, NOW(), NOW()),
(25, 'Partner', 1996, NULL, true, NOW(), NOW()),

-- Tesla Models (Brand ID will be 16)
(16, 'Model 3', 2017, NULL, true, NOW(), NOW()),
(16, 'Model Y', 2020, NULL, true, NOW(), NOW()),
(16, 'Model S', 2012, NULL, true, NOW(), NOW()),
(16, 'Model X', 2015, NULL, true, NOW(), NOW()),

-- Chery Models (Brand ID will be 30)
(30, 'Tiggo 2', 2016, NULL, true, NOW(), NOW()),
(30, 'Tiggo 4', 2018, NULL, true, NOW(), NOW()),
(30, 'Tiggo 7', 2016, NULL, true, NOW(), NOW()),
(30, 'Tiggo 8', 2018, NULL, true, NOW(), NOW()),
(30, 'Arrizo 5', 2016, NULL, true, NOW(), NOW());

-- =====================================================
-- COMPLETION MESSAGE
-- =====================================================
-- Total brands: 33
-- Total models: ~200
-- Popular brands: 20
-- =====================================================

