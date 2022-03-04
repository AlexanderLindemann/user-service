--liquibase formatted sql
-- CATEGORIES
-- TODO now values can be inserted for dev with this script, in prod needs to be inserted via swagger POST method
INSERT INTO celebrity_category (category_name, category_img, code, created_by)
VALUES
    ('Music', 'https://nft-platform.s3.eu-central-1.amazonaws.com/celeb_category/4c6f6d95-de86-4c52-845f-e61b800e3bd2', 'music', ''),
    ('Movies', 'https://nft-platform.s3.eu-central-1.amazonaws.com/celeb_category/dd2e1840-149c-4392-a4f7-52f98a8e29ef', 'movies', ''),
    ('Sport', 'https://nft-platform.s3.eu-central-1.amazonaws.com/celeb_category/33cb40b8-8617-4071-8c9d-66838bb18dea', 'sport', ''),
    ('Influencers', 'https://nft-platform.s3.eu-central-1.amazonaws.com/celeb_category/f7f2a2ef-c34f-4d1e-b8d0-271b8345f585', 'influencers', ''),
    ('Gaming', 'https://nft-platform.s3.eu-central-1.amazonaws.com/celeb_category/2dd484dc-afda-4a96-8e45-b44ec8049cfc', 'gaming', ''),
    ('Art', 'https://nft-platform.s3.eu-central-1.amazonaws.com/celeb_category/b65b0563-0cab-40fc-a032-25b58977d865', 'art', ''),
    ('Streamers', 'https://nft-platform.s3.eu-central-1.amazonaws.com/celeb_category/33b5252b-30d5-43dc-83c8-46d39ea21777', 'streamers', '')
;
