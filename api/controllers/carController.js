const { getDb } = require("../db/db.js");

// Look up cars and populate dealers
const getRequestWithDealerPopulated = (query) => [
	{ $match: query },
	{
		$lookup: {
			from: "dealers",
			localField: "dealerId",
			foreignField: "id",
			as: "dealer",
		},
	},
	{
		$project: {
			"dealer.cars": 0,
		},
	},
];

// Get all cars and their dealers
const getAllCars = async (req, res) => {
	console.log("[API LOG]: get all cars body: ", req.body);
	try {
		const db = getDb();

		const { carMake, fromYear, toYear } = req.query;

		// Build the query filter
		const query = {};

		if (carMake) {
			query.brand = { $regex: carMake, $options: "i" };
		}

		if (fromYear && toYear) {
			query.productionYear = { $gte: parseInt(fromYear), $lte: parseInt(toYear) };
		} else if (fromYear) {
			query.productionYear = { $gte: parseInt(fromYear) };
		} else if (toYear) {
			query.productionYear = { $lte: parseInt(toYear) };
		}

		const request = getRequestWithDealerPopulated(query);

		const cars = await db.collection("cars").aggregate(request).toArray();

		console.log("[API LOG]: get all cars result: ", cars);
		res.status(200).json({ cars });
	} catch (err) {
		console.error(err);
		res.status(400).send({ message: "Error fetching cars" });
	}
};

// Get a car by id (not _id)
const getCarById = async (req, res) => {
	try {
		const db = getDb();
		const carId = parseInt(req.params.id);

		if (isNaN(carId)) {
			return res.status(400).send({ message: "Invalid or missing car id" });
		}

		const request = getRequestWithDealerPopulated({ id: carId });

		const car = await db.collection("cars").aggregate(request).toArray();

		if (car.length === 0) {
			return res.status(404).send({ message: "Car not found" });
		}

		res.json({ car });
	} catch (err) {
		console.error(err);
		res.status(400).send({ message: "Error finding car" });
	}
};

// Create a new car
const createCar = async (req, res) => {
	try {
		const db = getDb();
		const newCar = req.body;

		// Validate car fields
		const { brand, model, productionYear, dealerId } = newCar;
		if (!brand || !model || !productionYear || !dealerId) {
			return res.status(400).send({ message: "Missing car fields" });
		}

		// Get last car's id and increment it
		const lastEntry = await db.collection("cars").findOne({}, { sort: { _id: -1 } });
		newCar.id = lastEntry.id ? lastEntry.id + 1 : 1;

		// Parse dealer ids to integers
		newCar.dealerId = parseInt(dealerId);

		const result = await db.collection("cars").insertOne(newCar);

		if (result) {
			return res.status(200).send({ car: newCar });
		} else {
			res.status(400).send({ message: "Error creating car" });
		}
	} catch (err) {
		console.error(err);
		res.status(400).send({ message: "Error creating car" });
	}
};

// Update car
const updateCar = async (req, res) => {
	try {
		const db = getDb();
		const carId = parseInt(req.params.id);

		if (!carId) {
			return res.status(400).send({ message: "Missing car id" });
		}

		let updateData = req.body;

		// Delete populated dealers
		delete updateData.dealers;

		// Parse the dealer id's to integers
		updateData = {
			...updateData,
			dealerId: parseInt(updateData.dealerId),
		};

		const result = await db.collection("cars").updateOne({ id: carId }, { $set: updateData });

		if (result.matchedCount === 0) {
			return res.status(404).send({ message: "Car not found" });
		}

		res.status(200).send({ car: { carId, ...updateData } });
	} catch (err) {
		console.error(err);
		res.status(400).send({ message: "Error updating car" });
	}
};

// Delete car
const deleteCar = async (req, res) => {
	try {
		const db = getDb();
		const carId = parseInt(req.params.id);

		if (!carId) {
			return res.status(400).send({ message: "Missing car id" });
		}

		const result = await db.collection("cars").deleteOne({ id: carId });

		if (result.deletedCount === 0) {
			return res.status(404).send({ message: "Car not found" });
		}

		res.status(200).send({ message: "Car deleted" });
	} catch (err) {
		console.error(err);
		res.status(400).send({ message: "Error deleting car" });
	}
};

module.exports = {
	getAllCars,
	getCarById,
	createCar,
	updateCar,
	deleteCar,
};
