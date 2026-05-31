const express = require("express");
const cors = require("cors");
const { connectToDb, initDb, deleteDb } = require("./db/db.js");

const carRoutes = require("./routes/carRoutes");
const dealerRoutes = require("./routes/dealerRoutes");

const app = express();
const PORT = 8088;

// CORS config
const corsOptions = {
	origin: "http://localhost:3000",
	methods: ["GET", "POST", "PUT", "DELETE"],
	allowedHeaders: ["Content-Type", "Authorization"],
};

// Enable CORS
app.use(cors(corsOptions));

app.use(express.json());

connectToDb()
	.then(async () => {
		// Uncomment to delete the db
		deleteDb();

		initDb();

		app.use("/cars", carRoutes);
		app.use("/dealers", dealerRoutes);

		app.listen(PORT, () => {
			console.log(`Server is running on http://localhost:${PORT}`);
		});
	})
	.catch((err) => {
		console.error("Error connecting to the database:", err);
	});
