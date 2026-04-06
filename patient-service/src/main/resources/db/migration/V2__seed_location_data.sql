-- Seed reference locations for the default branch hierarchy
INSERT INTO cd_location (location_id, location_code, location_name, parent_code, location_type, is_active)
VALUES
    (NEWID(), 'ORG-CENTRAL',  'Central Medical Centre',  NULL,          'ORG',     1),
    (NEWID(), 'BRANCH-NORTH', 'North Branch',            'ORG-CENTRAL', 'BRANCH',  1),
    (NEWID(), 'CTR-01',       'Counter 1',               'BRANCH-NORTH','COUNTER', 1),
    (NEWID(), 'CTR-02',       'Counter 2',               'BRANCH-NORTH','COUNTER', 1),
    (NEWID(), 'CTR-03',       'General Registration',    'BRANCH-NORTH','COUNTER', 1);
