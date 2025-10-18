db = db.getSiblingDB('videos_uploader');
db.createCollection('videos_metadatas');
print('✅ videos_uploader database initialized');
