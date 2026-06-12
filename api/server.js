const express = require("express");
const cors = require("cors");

// Dotenv for env vars
require("dotenv").config();

const { connectToDb, initDb, deleteDb } = require("./db/db.js");

const authRoutes = require("./routes/authRoutes");
const carRoutes = require("./routes/carRoutes");
const dealerRoutes = require("./routes/dealerRoutes");

const app = express();
const PORT = process.env.PORT || 8088;

// CORS config
const corsOptions = {
	origin: "*",
	methods: ["GET", "POST", "PUT", "DELETE"],
	allowedHeaders: ["Content-Type", "Authorization"],
};

// Enable CORS
app.use(cors(corsOptions));

app.use(express.json());

connectToDb()
	.then(async () => {
		// Uncomment to delete the db
		// deleteDb();

		initDb();

		app.use("/auth", authRoutes);
		app.use("/cars", carRoutes);
		app.use("/dealers", dealerRoutes);

		app.listen(PORT, () => {
			console.log(`Server is running on ${PORT}`);
		});
	})
	.catch((err) => {
		console.error("Error connecting to the database:", err);
	});
