CREATE EXTENSION IF NOT EXISTS "pgcrypto";

INSERT INTO tb_users (id, email, password, department) VALUES
('e4ff5d0b-6033-4d96-9ab6-f0fa5def972a', 'ti@user.com', '$2a$12$IbF8AAJdLsd.0ahaDEw/WO3W3lrpfpjBLa9S7PpnXHulrEERCqg3.', 'TI'),
('4f16fe47-31bf-4184-a080-680d4f00cd43', 'financeiro@user.com', '$2a$12$DpymMUy4E9b3hAw6IT8Ji.taTyTiLfF3GMTlF5N9AhHEh2QAj3lYC', 'FINANCEIRO'),
('dae6523a-eb3f-4e46-8335-5d8d39893c03', 'rh@user.com', '$2a$12$kr1zr1qNVYM6UU2VLemVIOtWsrSsngiZA64Inorm/qURgD1dzD9de', 'RH'),
('4f50d0a4-109f-4cd5-b511-c7fe1cff88b5', 'operacoes@user.com', '$2a$12$N7UG4Lqa/464fGU6zYKYVuZ0EV1nPby2wwsA6bUib5fGbB0Jdiu9u', 'OPERACOES');

-- Módulos
INSERT INTO tb_modules (id, name, description, active) VALUES
('dfc4190f-b005-45c1-9c0b-9490e5f40fc4', 'Portal do Colaborador', 'Acesso ao portal principal do colaborador', true),
('1fe1aea8-b0c0-4695-b2a5-e11b71e7fe79', 'Sistema Financeiro', 'Módulo completo de gestão financeira', true),
('5c0cccf4-662d-44d5-b5c9-ed09c5d78093', 'Aprovador Financeiro', 'Permissão para aprovar pagamentos e despesas', true),
('5ed23ac1-cad5-4201-883a-b797d618fc7a', 'Solicitante Financeiro', 'Permissão para criar solicitações de pagamento', true),
('ee00c133-41be-4428-9eb0-cd88f1c48c7f', 'Gestão de Projetos', 'Controle e acompanhamento de projetos', true),
('1157c5ed-073e-4004-b694-8d7fa2cc52c6', 'RH Online', 'Portal de recursos humanos', true);

INSERT INTO tb_module_allowed_departments (module_id, department) VALUES
('dfc4190f-b005-45c1-9c0b-9490e5f40fc4', 'TI'),
('dfc4190f-b005-45c1-9c0b-9490e5f40fc4', 'FINANCEIRO'),
('dfc4190f-b005-45c1-9c0b-9490e5f40fc4', 'RH'),
('dfc4190f-b005-45c1-9c0b-9490e5f40fc4', 'OPERACOES'),

('1fe1aea8-b0c0-4695-b2a5-e11b71e7fe79', 'FINANCEIRO'),
('5c0cccf4-662d-44d5-b5c9-ed09c5d78093', 'FINANCEIRO'),
('5ed23ac1-cad5-4201-883a-b797d618fc7a', 'FINANCEIRO'),

('ee00c133-41be-4428-9eb0-cd88f1c48c7f', 'TI'),
('ee00c133-41be-4428-9eb0-cd88f1c48c7f', 'OPERACOES'),

('1157c5ed-073e-4004-b694-8d7fa2cc52c6', 'RH');

INSERT INTO tb_module_incompatibles (module_id, incompatible_module_id) VALUES
('5c0cccf4-662d-44d5-b5c9-ed09c5d78093', '5ed23ac1-cad5-4201-883a-b797d618fc7a'),
('5ed23ac1-cad5-4201-883a-b797d618fc7a', '5c0cccf4-662d-44d5-b5c9-ed09c5d78093');