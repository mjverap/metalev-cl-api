
INSERT INTO venues (city_id, name, street) VALUES
                                               (167, 'La Bodeguita de Nicanor', 'Ánibal Pinto 1661'),
                                               (167, 'Black & White (ExMansión)', 'Freire 16'),
                                               (167, 'Refugio Bar', 'Maipú 122'),
                                               (167, 'Havana Club', 'Barros Arana 1356'),
                                               (167, 'Teatro Lihuén', 'San Martín 601'),
                                               (45, 'Trotamundos', 'Blanco 1253'),
                                               (45, 'El Huevo', 'Blanco 1386'),
                                               (45, 'Poseidón Restobar', 'Pl. Ánibal Pinto 339'),
                                               (51, 'Valparaíso Sporting', 'Av. Los Castaños 404'),
                                               (244, 'Living Club', 'Las Toninas 1991'),
                                               (342, 'Blondie', 'Av. Libertador Bernardo O''Higgins 2879'),
                                               (313, 'Estadio Nacional', 'Av. Grecia 2001'),
                                               (342, 'Movistar Arena', 'Av. Beauchef 1204'),
                                               (313, 'Sala RBX', 'Av. Vicuña Mackenna 1220'),
                                               (342, 'Teatro Cariola', 'San Diego 246'),
                                               (342, 'Teatro Caupolicán', 'San Diego 850'),
                                               (342, 'Teatro Coliseo', 'Nataniel Cox 59'),
                                               (92, 'Gran Arena Monticello', 'Panamericana Sur Km. 57');

INSERT INTO recitals (name, min_ticket_price, max_ticket_price, start_date, end_date, venue_id, type, status, recital_link) VALUES
                                                                                                                                ('Exodus - Tour 2022', 10000, 30000, '2022-11-28', '2022-11-28', 10, 'INTERNATIONAL', 'PAST', NULL),
                                                                                                                                ('Exodus - Tour 2022', 10000, 30000, '2022-11-26', '2022-11-26', 7, 'INTERNATIONAL', 'PAST', NULL),
                                                                                                                                ('Nervosa - Tour 2022', 5000, 20000, '2022-10-14', '2022-10-14', 7, 'INTERNATIONAL', 'PAST', NULL),
                                                                                                                                ('My Dying Bride - Latin America Tour 2026', 42000, 53000, '2026-11-23', '2026-11-23', 15, 'INTERNATIONAL', 'UPCOMING', 'https://www.passline.com/eventos/my-dying-bride'),
                                                                                                                                ('Opeth - The Last Will and Testament', 40250, 79350, '2026-11-01', '2026-11-01', 13, 'INTERNATIONAL', 'UPCOMING', 'https://www.puntoticket.com/OPETH'),
                                                                                                                                ('Chile Terror Fest II 2026', 57500, 74750, '2026-11-28', '2026-11-29', 16, 'FESTIVAL', 'UPCOMING', 'https://www.puntoticket.com/evento/chileterror-2-teatro-caupolican-nov-2026');

INSERT INTO recital_bands (recital_id, band_name)
SELECT r.id, v.band_name
FROM (VALUES
             ('Exodus - Tour 2022', 10, DATE '2022-11-28', 'Exodus'),
             ('Exodus - Tour 2022', 7, DATE '2022-11-26', 'Exodus'),
             ('Nervosa - Tour 2022', 7, DATE '2022-10-14', 'Nervosa'),
             ('My Dying Bride - Latin America Tour 2026', 15, DATE '2026-11-23', 'My Dying Bride'),
             ('Opeth - The Last Will and Testament', 13, DATE '2026-11-01', 'Opeth'),
             ('Opeth - The Last Will and Testament', 13, DATE '2026-11-01', 'Mar de Grises'),
             ('Chile Terror Fest II 2026', 16, DATE '2026-11-28', 'Carcass'),
             ('Chile Terror Fest II 2026', 16, DATE '2026-11-28', 'Necrot'),
             ('Chile Terror Fest II 2026', 16, DATE '2026-11-28', 'Torturer'),
             ('Chile Terror Fest II 2026', 16, DATE '2026-11-28', 'Diabolus'),
             ('Chile Terror Fest II 2026', 16, DATE '2026-11-28', 'Testament'),
             ('Chile Terror Fest II 2026', 16, DATE '2026-11-28', 'Municipal Waste'),
             ('Chile Terror Fest II 2026', 16, DATE '2026-11-28', 'Immolator'),
             ('Chile Terror Fest II 2026', 16, DATE '2026-11-28', 'Fallout'),
             ('Chile Terror Fest II 2026', 16, DATE '2026-11-28', 'Inquisicion')
     ) AS v(recital_name, venue_id, start_date, band_name)
         JOIN recitals r
              ON r.name = v.recital_name
                  AND r.venue_id = v.venue_id
                  AND r.start_date = v.start_date;
