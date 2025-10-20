// // first db for uploads
// db = db.getSiblingDB('videos_uploader');
// db.createCollection('videos_metadatas');
//
// // second db for converts
// db = db.getSiblingDB('videos_converter');
// db.createCollection('videos_metadatas');
//
// print('✅ videos_uploader database initialized');

// Helper function to create collection if it doesn't exist
function createCollectionIfNotExists(dbName, collectionName) {
    const database = db.getSiblingDB(dbName);
    const collections = database.getCollectionNames();
    if (!collections.includes(collectionName)) {
        database.createCollection(collectionName);
        print(`✅ Collection '${collectionName}' created in database '${dbName}'`);
    } else {
        print(`ℹ️ Collection '${collectionName}' already exists in database '${dbName}'`);
    }
}

// First db for uploads
createCollectionIfNotExists('videos', 'videos_metadatas');

// Second db for converts
// createCollectionIfNotExists('videos_converter', 'videos_metadatas');

print('✅ Database initialization complete');