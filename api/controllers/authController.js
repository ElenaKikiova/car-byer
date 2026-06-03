const bcrypt = require("bcrypt");
const jwt = require("jsonwebtoken");
const { getDb } = require("../db/db");

const JWT_SECRET = "kolichki";

const register = async (req, res) => {
	console.log("[API LOG]: register route body: ", req.body);
	try {
		const db = getDb();

		const { email, password, firstName, lastName, phone, role } = req.body;

		if (!email || !password) {
			return res.status(400).json({
				message: "Email and password are required",
			});
		}

		const existingUser = await db.collection("users").findOne({ email });

		if (existingUser) {
			return res.status(409).json({
				message: "Email already exists",
			});
		}

		const hashedPassword = await bcrypt.hash(password, 10);

		const user = {
			email,
			password: hashedPassword,
			firstName,
			lastName,
			phone,
			role,
		};

		const result = await db.collection("users").insertOne(user);

		const token = jwt.sign(
			{
				id: result.insertedId,
				email,
			},
			JWT_SECRET,
			{
				expiresIn: "30d",
			}
		);

		res.status(201).json({
			token,
			user: {
				id: result.insertedId,
				email,
				firstName,
				lastName,
				role: user.role,
			},
		});
	} catch (err) {
		console.error(err);
		res.status(500).send({ message: "Error registering" });
	}
};

const login = async (req, res) => {
	console.log("[API LOG]: login route body: ", req.body);
	try {
		const db = getDb();

		const { email, password } = req.body;

		const user = await db.collection("users").findOne({ email });

		if (!user) {
			console.log("[API LOG]: login no user");
			return res.status(401).json({
				message: "Invalid email or password",
			});
		}

		const validPassword = await bcrypt.compare(password, user.password);

		if (!validPassword) {
			console.log("[API LOG]: login not valid pass");
			return res.status(401).json({
				message: "Invalid email or password",
			});
		}

		const token = jwt.sign(
			{
				id: user._id,
				email: user.email,
			},
			JWT_SECRET,
			{
				expiresIn: "30d",
			}
		);

		res.json({
			token,
			user: {
				id: user._id,
				email: user.email,
				firstName: user.firstName,
				lastName: user.lastName,
				phone: user.phone,
				role: user.role,
			},
		});
	} catch (err) {
		console.error(err);
		res.status(500).send({ message: "Error logging in" });
	}
};

module.exports = {
	register,
	login,
};
