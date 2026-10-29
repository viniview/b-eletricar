-- Popular o banco "beletricar" (tabelas: fornecedor, usuario, veiculo).
-- Carros 100% eletricos, dados pesquisados em 24/10/2026 em sites automotivos brasileiros e fichas das marcas.
-- preco = preco de tabela sugerido (R$); autonomia_km = Inmetro (PBEV); capacidade_bateria_kwh = capacidade declarada;
-- potencia_cv = potencia maxima; tempo_carga_horas = estimativa de recarga completa em AC (NULL se a potencia AC nao foi confirmada).
-- foto_url = nome do arquivo em src/main/resources/static/images/carros/ (a coluna e criada pelo Spring ao ligar a aplicacao).

-- 1) Fornecedores (classe MarcaVeiculo).
INSERT INTO fornecedor (nome, email_contato, site) VALUES
('BYD Auto do Brasil', 'sac@byd.com', 'https://www.byd.com.br'),
('Renault do Brasil', 'atendimento.cliente@renault.com', 'https://www.renault.com.br'),
('Volvo Car Brasil', 'sac.volvocars@volvocars.com', 'https://www.volvocars.com/br'),
('Stellantis Brasil', 'sac@stellantis.com', 'https://www.stellantis.com.br'),
('GAC Brasil', 'contato@gacmotor.com.br', 'https://www.gacmotor.com.br'),
('BMW do Brasil', 'relacionamento.cliente@bmw.com.br', 'https://www.bmw.com.br'),
('Audi do Brasil', 'centraldeatendimento@audi.com.br', 'https://www.audi.com.br'),
('Geely Brasil', 'contato@geelyauto.com.br', 'https://www.geely.com'),
('MG Motor Brasil', 'contato@mgmotor.com.br', 'https://www.mgmotor.com.br');

-- 2) Usuarios (senhas em BCrypt)
-- Admin: admin@beletricar.com / senha admin123
-- Demais usuarios: senha user1234
INSERT INTO usuario (email, senha, nome, role, sub_id) VALUES
('admin@beletricar.com', '$2a$10$Er4Qzd2/P5buYfyUhb2cD.9ebSlfovJm.PiybPLoF3ZF4/StdDNbO', 'Administrador', 'ADMIN', NULL),
('ana.souza@exemplo.com', '$2a$10$yT2ex.uGWx5nv5KnDaO//uY.kwjT228tN/0x.mT2XUWMzDdfDAyfS', 'Ana Souza', 'USER', NULL),
('bruno.lima@exemplo.com', '$2a$10$yT2ex.uGWx5nv5KnDaO//uY.kwjT228tN/0x.mT2XUWMzDdfDAyfS', 'Bruno Lima', 'USER', NULL),
('carla.dias@exemplo.com', '$2a$10$yT2ex.uGWx5nv5KnDaO//uY.kwjT228tN/0x.mT2XUWMzDdfDAyfS', 'Carla Dias', 'USER', NULL);

-- 3) Veiculos (fornecedor_id buscado pelo nome)
INSERT INTO veiculo (marca, modelo, carroceria, preco, autonomia_km, capacidade_bateria_kwh, potencia_cv, tempo_carga_horas, fornecedor_id, foto_url) VALUES
('BYD', 'Dolphin Mini GS',   'Hatch', 119990, 280, 38.9,  75, 5.9,  (SELECT id FROM fornecedor WHERE nome = 'BYD Auto do Brasil'), 'byd-dolphin-mini.jpg'),
('BYD', 'Dolphin GS',        'Hatch', 149990, 291, 44.9,  95, 6.8,  (SELECT id FROM fornecedor WHERE nome = 'BYD Auto do Brasil'), 'byd-dolphin.jpg'),
('BYD', 'Dolphin SE',        'Hatch', 159990, 272, 45.1, 177, NULL, (SELECT id FROM fornecedor WHERE nome = 'BYD Auto do Brasil'), 'byd-dolphin.jpg'),
('BYD', 'Dolphin Plus',      'Hatch', 184800, 330, 60.5, 204, NULL, (SELECT id FROM fornecedor WHERE nome = 'BYD Auto do Brasil'), 'byd-dolphin.jpg'),
('BYD', 'Yuan Plus AWD',     'SUV',   269990, 378, 74.9, 449, 6.8,  (SELECT id FROM fornecedor WHERE nome = 'BYD Auto do Brasil'), 'byd-yuan-plus.jpg'),
('BYD', 'Seal',              'Sedan', 299990, 372, 82.5, 531, 7.5,  (SELECT id FROM fornecedor WHERE nome = 'BYD Auto do Brasil'), 'byd-seal.jpg'),
('Renault', 'Kwid E-Tech',   'Hatch',  99990, 180, 26.8,  65, 3.8,  (SELECT id FROM fornecedor WHERE nome = 'Renault do Brasil'), 'renault-kwid-e-tech.jpg'),
('Volvo', 'EX30 Single Motor', 'SUV', 249950, 250, 51.0, 272, 6.0, (SELECT id FROM fornecedor WHERE nome = 'Volvo Car Brasil'), 'volvo-ex30.jpg'),
('Volvo', 'EX30 Twin Motor Performance', 'SUV', 319950, 316, 69.0, 428, 8.0, (SELECT id FROM fornecedor WHERE nome = 'Volvo Car Brasil'), 'volvo-ex30.jpg'),
('Fiat', '500e',             'Hatch', 214990, 227, 42.0, 118, 5.7,  (SELECT id FROM fornecedor WHERE nome = 'Stellantis Brasil'), 'fiat-500e.jpg'),
('GAC', 'Aion ES', 'Sedan', 170990, 314, 55.2, 136, NULL, (SELECT id FROM fornecedor WHERE nome = 'GAC Brasil'), 'gac-aion-es.jpg'),
('BMW', 'i4 eDrive40 M Sport', 'Sedan', 581950, 399, 81.3, 340, 7.4, (SELECT id FROM fornecedor WHERE nome = 'BMW do Brasil'), 'bmw-i4.jpg'),
('BMW', 'i4 M50 xDrive', 'Sedan', 674950, 405, 81.3, 544, 7.4, (SELECT id FROM fornecedor WHERE nome = 'BMW do Brasil'), 'bmw-i4.jpg'),
('Audi', 'A6 Sportback e-tron Performance Black', 'Sedan', 649990, 445, 100.0, 367, NULL, (SELECT id FROM fornecedor WHERE nome = 'Audi do Brasil'), 'audi-a6-e-tron.jpg'),
('Volvo', 'ES90 Ultra Twin Motor Performance', 'Sedan', 659950, 524, 106.0, 680, 9.6, (SELECT id FROM fornecedor WHERE nome = 'Volvo Car Brasil'), 'volvo-es90.jpg'),
('Geely', 'EX2 Pro', 'Hatch', 124600, 289, 39.4, 116, 6.0, (SELECT id FROM fornecedor WHERE nome = 'Geely Brasil'), 'geely-ex2.jpg'),
('Geely', 'EX2 Max', 'Hatch', 137800, 289, 39.4, 116, 6.0, (SELECT id FROM fornecedor WHERE nome = 'Geely Brasil'), 'geely-ex2.jpg'),
('MG', 'MG4 Urban Comfort 43 kWh', 'Hatch', 129990, 299, 43.0, 150, NULL, (SELECT id FROM fornecedor WHERE nome = 'MG Motor Brasil'), 'mg-mg4-urban.jpg'),
('MG', 'MG4 Urban Luxury 43 kWh', 'Hatch', 139990, 299, 43.0, 150, NULL, (SELECT id FROM fornecedor WHERE nome = 'MG Motor Brasil'), 'mg-mg4-urban.jpg'),
('MG', 'MG4 Urban Luxury 54 kWh', 'Hatch', 149990, 358, 54.0, 160, NULL, (SELECT id FROM fornecedor WHERE nome = 'MG Motor Brasil'), 'mg-mg4-urban.jpg'),
('Geely', 'EX5 Pro', 'SUV', 195800, 413, 60.2, 218, NULL, (SELECT id FROM fornecedor WHERE nome = 'Geely Brasil'), 'geely-ex5.jpg');

-- Conferencia
SELECT 'fornecedor' AS tabela, count(*) FROM fornecedor
UNION ALL SELECT 'usuario', count(*) FROM usuario
UNION ALL SELECT 'veiculo', count(*) FROM veiculo;