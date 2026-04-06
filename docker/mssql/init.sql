-- Create all service databases
IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = 'pmk_patient')
    CREATE DATABASE pmk_patient;
GO

IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = 'pmk_payment')
    CREATE DATABASE pmk_payment;
GO

IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = 'pmk_settlement')
    CREATE DATABASE pmk_settlement;
GO

IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = 'pmk_notification')
    CREATE DATABASE pmk_notification;
GO
