const { MongoClient } = require("mongodb");
const { fillCarsData, fillDealersData } = require("./fillDb.js");

const uri = "mongodb://localhost:27017";
const client = new MongoClient(uri);
const dbName = "car-byer-db";

let db;

async function connectToDb() {
	try {
		await client.connect();
		db = client.db(dbName);
		console.log("Connected to MongoDB");
	} catch (err) {
		console.error("Error connecting to MongoDB:", err);
	}
}

const initDb = async () => {
	try {
		const carsCollection = db.collection("cars");
		const dealersCollection = db.collection("dealers");

		const carCount = await carsCollection.countDocuments();
		if (carCount === 0) {
			await fillCarsData(db);
		}

		const dealerCount = await dealersCollection.countDocuments();
		if (dealerCount === 0) {
			await fillDealersData(db);
		}

		console.log("Database initialized successfully");
	} catch (err) {
		console.error("Error initializing DB:", err);
	}
};

const getDb = () => db;

const deleteDb = async () => {
	try {
		if (!db) {
			console.log("Database connection not established.");
			return;
		}

		await db.dropDatabase();
		console.log("Database has been deleted successfully.");
	} catch (error) {
		console.error("Error deleting database:", error);
	}
};

module.exports = { connectToDb, initDb, getDb, deleteDb };
