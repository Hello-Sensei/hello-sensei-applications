// first db for uploads
db = db.getSiblingDB('videos_uploader');
db.createCollection('videos_metadatas');

// second db for converts
db = db.getSiblingDB('videos_converter');
db.createCollection('videos_metadatas');

print('✅ videos_uploader database initialized');
