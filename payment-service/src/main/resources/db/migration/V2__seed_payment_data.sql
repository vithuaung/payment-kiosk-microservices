-- Seed payment methods
INSERT INTO cd_pay_method (method_code, method_name, method_group, is_active)
VALUES
    ('NETS_CARD',  'NETS Card',        'CARD', 1),
    ('NETS_QR',    'NETS QR Payment',  'CARD', 1),
    ('NETS_FLASH', 'NETS Flash Pay',   'CARD', 1),
    ('CASH',       'Cash Payment',     'CASH', 1),
    ('CARD_UOB',   'UOB Credit Card',  'CARD', 1),
    ('CARD_OCBC',  'OCBC Credit Card', 'CARD', 1),
    ('CARD_UNION', 'UnionPay Card',    'CARD', 1),
    ('ZERO_BILL',  'Zero Bill',        'ZERO', 1);

-- Seed sample terminal
INSERT INTO cd_terminal (terminal_code, location_code, terminal_status, is_active)
VALUES ('TERM-001', 'CTR-01', 'ONLINE', 1);
