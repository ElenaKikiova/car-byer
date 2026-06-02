const { getDb } = require("../db/db.js");

// Look up dealers and populate cars
const getRequestWithCarsPopulated = (query) => [
	{ $match: query },
	{
		$lookup: {
			from: "cars",
			localField: "id",
			foreignField: "dealerId",
			as: "cars",
		},
	},
	{
		$project: {
			"cars.dealerId": 0,
		},
	},
];

const getAllDealers = async (req, res) => {
	try {
		const db = getDb();

		const { city } = req.query;

		const query = {};

		if (city) {
			query.city = { $regex: city, $options: "i" };
		}

		const request = getRequestWithCarsPopulated(query);

		const dealers = await db.collection("dealers").aggregate(request).toArray();

		res.status(200).json({ dealers });
	} catch (err) {
		console.error(err);
		res.status(400).send({ message: "Error fetching dealers" });
	}
};

// Get a dealer by id (not _id)
const getDealerById = async (req, res) => {
	try {
		const db = getDb();
		const dealerId = parseInt(req.params.id);

		if (isNaN(dealerId)) {
			return res.status(400).send({ message: "Missing dealer id" });
		}

		const request = getRequestWithCarsPopulated({ id: dealerId });

		// Fetch dealer by id and it's cars
		const dealer = await db.collection("dealers").aggregate(request).toArray();

		if (!dealer) {
			return res.status(404).send({ message: "Delaer not found" });
		}

		res.status(200).json({ dealer });
	} catch (err) {
		console.error(err);
		res.status(400).send({ message: "Error fetching dealer" });
	}
};

// Create a new dealer
const createDealer = async (req, res) => {
	try {
		const db = getDb();
		const newDealer = req.body;

		// Validate
		const { name, address, workingHours } = newDealer;
		if (!name || !address || !workingHours) {
			return res.status(400).send({ message: "Missing dealer fields" });
		}

		// Get the last dealer id and increment it
		const lastDealer = await db.collection("dealers").findOne({}, { sort: { id: -1 } });
		newDealer.id = lastDealer.id ? lastDealer.id + 1 : 1;

		const result = await db.collection("dealers").insertOne(newDealer);
		console.error(result);

		if (result) {
			res.status(200).json({ dealer: newDealer });
		} else {
			res.status(400).send({ message: "Error creating dealer" });
		}
	} catch (err) {
		console.error(err);
		res.status(400).send({ message: "Error creating dealer" });
	}
};

// Update a dealer
const updateDealer = async (req, res) => {
	try {
		const db = getDb();
		const dealerId = parseInt(req.params.id);

		if (isNaN(dealerId)) {
			return res.status(400).send({ message: "Missing dealer id" });
		}

		const updateData = req.body;
		const { name, address, workingHours } = updateData;

		// Validate
		if (!name || !address || !workingHours) {
			return res.status(400).send({ message: "Missing dealer fields" });
		}

		const result = await db
			.collection("dealers")
			.updateOne({ id: dealerId }, { $set: updateData });

		if (result.matchedCount === 0) {
			return res.status(404).send({ message: "Dealer not found" });
		}

		const updatedDealer = await db.collection("dealers").findOne({ id: dealerId });

		res.status(200).json({ dealer: updatedDealer });
	} catch (err) {
		console.error(err);
		res.status(400).send({ message: "Error updating dealer" });
	}
};

// Delete a dealer
const deleteDealer = async (req, res) => {
	try {
		const db = getDb();
		const dealerId = parseInt(req.params.id);

		if (isNaN(dealerId)) {
			return res.status(400).send({ message: "Missing dealer id" });
		}

		const result = await db.collection("dealers").deleteOne({ id: dealerId });

		if (result.deletedCount === 0) {
			return res.status(404).send({ message: "Dealer not found" });
		}

		res.status(200).json("Dealer deleted successfully");
	} catch (err) {
		console.error(err);
		res.status(400).send({ message: "Error deleting dealer" });
	}
};

module.exports = {
	getAllDealers,
	getDealerById,
	createDealer,
	updateDealer,
	deleteDealer,
};
