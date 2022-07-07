ALTER TABLE celebrity ADD last_name VARCHAR(255);
ALTER TABLE celebrity ADD image_card_mp VARCHAR(255);
ALTER TABLE celebrity ADD image_card_app VARCHAR(255);
ALTER TABLE celebrity ADD image_promo_app VARCHAR(255);
ALTER TABLE celebrity ADD image_login VARCHAR(255);
ALTER TABLE celebrity ADD image_nft_owner VARCHAR(255);
ALTER TABLE celebrity ADD image_screen VARCHAR(255);
ALTER TABLE celebrity ADD active boolean DEFAULT FALSE NOT NULL;