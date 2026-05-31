const { cars, dealers } = require("./initialData.js");

const fillCarsData = async (db) => {
	const carsCollection = db.collection("cars");
	try {
		await carsCollection.insertMany(cars);
		console.log("Initial car data uploaded");
	} catch (err) {
		console.error("Error inserting car data:", err);
	}
};

const fillDealersData = async (db) => {
	const dealersCollection = db.collection("dealers");
	try {
		await dealersCollection.insertMany(dealers);
		console.log("Initial dealer data uploaded");
	} catch (err) {
		console.error("Error inserting dealer data:", err);
	}
};

module.exports = {
	fillCarsData,
	fillDealersData,
};
