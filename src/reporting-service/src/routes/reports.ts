import { Router, Request, Response } from 'express';
import { ReportingService } from '../services/reportingService';

const router = Router();
const reportingService = new ReportingService();

router.get('/orders/summary', async (_req: Request, res: Response) => {
  const summary = await reportingService.getOrderSummary();
  return res.json(summary);
});

router.get('/orders/daily', async (req: Request, res: Response) => {
  const date = req.query.date as string || new Date().toISOString().split('T')[0];
  const report = await reportingService.getDailyOrderReport(date);
  return res.json(report);
});

router.get('/payments/summary', async (_req: Request, res: Response) => {
  const summary = await reportingService.getPaymentSummary();
  return res.json(summary);
});

router.get('/carts/active', async (_req: Request, res: Response) => {
  const activeCarts = await reportingService.getActiveCartsReport();
  return res.json(activeCarts);
});

export { router as reportRoutes };
