ALTER TABLE role DROP CONSTRAINT IF EXISTS role_role_type_check;
ALTER TABLE role ADD CONSTRAINT role_role_type_check
    CHECK ( role_type IN('ROLE_USER', 'ROLE_OWNER', 'ROLE_ADMIN') );

INSERT INTO role(role_type) VALUES ('ROLE_ADMIN');
