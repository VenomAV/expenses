CREATE TABLE IF NOT EXISTS employees (
    id uuid NOT NULL,
    name varchar NOT NULL,
    surname varchar NOT NULL
);

CREATE TABLE IF NOT EXISTS claims (
    id uuid NOT NULL,
    type char NOT NULL,
    employeeId uuid NOT NULL,
    expenses JSON NOT NULL
);

CREATE TABLE IF NOT EXISTS expensesheets (
    id uuid NOT NULL,
    type char NOT NULL,
    employeeId uuid NOT NULL,
    expenses JSON NOT NULL
);