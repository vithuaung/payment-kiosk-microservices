-- Add person_ref and pay_method to mtxn_settlement.
-- Needed so the retry endpoint can reconstruct the SAP request without re-supplying these fields.

ALTER TABLE mtxn_settlement ADD person_ref VARCHAR(30) NULL;
ALTER TABLE mtxn_settlement ADD pay_method VARCHAR(20) NULL;
