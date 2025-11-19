CREATE TABLE IF NOT EXISTS tb_users (
  id uuid PRIMARY KEY,
  email VARCHAR(255) UNIQUE NOT NULL,
  password VARCHAR(255) NOT NULL,
  department department_enum NOT NULL
);

CREATE TABLE IF NOT EXISTS tb_modules (
  id uuid PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  description TEXT,
  active BOOLEAN DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS tb_module_allowed_departments (
  module_id INTEGER REFERENCES tb_modules(id),
  department department_enum NOT NULL
);

CREATE TABLE IF NOT EXISTS tb_module_incompatibles (
  module_id INTEGER REFERENCES tb_modules(id),
  incompatible_module_id INTEGER REFERENCES tb_modules(id)
);

CREATE TABLE IF NOT EXISTS tb_access_requests (
  id uuid PRIMARY KEY,
  protocol VARCHAR(20) UNIQUE NOT NULL,
  user_id INTEGER REFERENCES tb_users(id),
  justification TEXT NOT NULL,
  urgent BOOLEAN DEFAULT FALSE,
  status status_enum NOT NULL,
  request_date TIMESTAMP NOT NULL,
  expiration_date TIMESTAMP,
  denial_reason TEXT
);

CREATE TABLE IF NOT EXISTS tb_access_request_modules (
  access_request_id INTEGER REFERENCES access_requests(id),
  module_id INTEGER REFERENCES tb_modules(id)
);

CREATE TABLE IF NOT EXISTS tb_user_active_modules (
  user_id INTEGER REFERENCES tb_users(id),
  module_id INTEGER REFERENCES tb_modules(id),
  expiration_date TIMESTAMP,
  PRIMARY KEY (user_id, module_id)
);

CREATE TABLE IF NOT EXISTS tb_request_history (
  id uuid PRIMARY KEY,
  access_request_id INTEGER REFERENCES access_requests(id),
  change_date TIMESTAMP NOT NULL,
  description TEXT NOT NULL
);