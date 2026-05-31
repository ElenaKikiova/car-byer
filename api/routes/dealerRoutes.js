const express = require("express");
const router = express.Router();
const {
	getAllDealers,
	getDealerById,
	createDealer,
	updateDealer,
	deleteDealer,
} = require("../controllers/dealerController");

router.get("/", getAllDealers);
router.get("/:id", getDealerById);
router.post("/", createDealer);
router.put("/:id", updateDealer);
router.delete("/:id", deleteDealer);

module.exports = router;
