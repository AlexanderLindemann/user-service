ALTER TABLE celebrity ADD theme_type VARCHAR(10) DEFAULT 'LEGACY' NOT NULL;
--UPDATE users.public.celebrity SET theme_type = 'MIDDLE' WHERE celebrity.nick_name = 'Lady Natalii';