INSERT INTO users (email, password, department) VALUES
('ti@user.com', '$2a$10$examplehash1', 'TI'),
('financeiro@user.com', '$2a$10$examplehash2', 'FINANCEIRO'),
('rh@user.com', '$2a$10$examplehash3', 'RH'),
('operacoes@user.com', '$2a$10$examplehash4', 'OPERACOES');

-- Módulos
INSERT INTO modules (id, name, description, active) VALUES
(1, 'Portal do Colaborador', 'Acesso geral', TRUE),
(2, 'Relatórios Gerenciais', 'Relatórios', TRUE),
(3, 'Gestão Financeira', 'Financeiro', TRUE),
(4, 'Aprovador Financeiro', 'Aprovação financeira', TRUE),
(5, 'Solicitante Financeiro', 'Solicitação financeira', TRUE),
(6, 'Administrador RH', 'Admin RH', TRUE),
(7, 'Colaborador RH', 'Colaborador RH', TRUE),
(8, 'Gestão de Estoque', 'Estoque', TRUE),
(9, 'Compras', 'Compras', TRUE),
(10, 'Auditoria', 'Auditoria TI', TRUE);

INSERT INTO module_allowed_departments (module_id, department) VALUES
(1, 'TI'), (1, 'FINANCEIRO'), (1, 'RH'), (1, 'OPERACOES'), (1, 'OUTROS'),
(2, 'TI'), (2, 'FINANCEIRO'), (2, 'RH'), (2, 'OPERACOES'), (2, 'OUTROS'),
(3, 'FINANCEIRO'), (3, 'TI'),
(4, 'FINANCEIRO'), (4, 'TI'),
(5, 'FINANCEIRO'), (5, 'TI'),
(6, 'RH'), (6, 'TI'),
(7, 'RH'), (7, 'TI'),
(8, 'OPERACOES'), (8, 'TI'),
(9, 'OPERACOES'), (9, 'TI'),
(10, 'TI');


INSERT INTO module_incompatibles (module_id, incompatible_module_id) VALUES
(4, 5), (5, 4),
(6, 7), (7, 6);