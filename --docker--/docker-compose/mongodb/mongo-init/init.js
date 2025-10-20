// first db for uploads
db = db.getSiblingDB('videos');
db.createCollection('videos_metadatas');

print('✅ videos_uploader database initialized');
