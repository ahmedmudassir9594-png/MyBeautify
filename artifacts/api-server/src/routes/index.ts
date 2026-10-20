import { Router, type IRouter } from "express";
import catalogRouter from "./catalog";
import healthRouter from "./health";

const router: IRouter = Router();

router.use(healthRouter);
router.use(catalogRouter);

export default router;
