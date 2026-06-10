import { Router } from 'express';
import { createProxyMiddleware } from 'http-proxy-middleware';

const router = Router();

const CART_SERVICE_URL = process.env.CART_SERVICE_URL || 'http://localhost:3001';
const PAYMENT_SERVICE_URL = process.env.PAYMENT_SERVICE_URL || 'http://localhost:3002';
const ORDER_SERVICE_URL = process.env.ORDER_SERVICE_URL || 'http://localhost:3003';
const REPORTING_SERVICE_URL = process.env.REPORTING_SERVICE_URL || 'http://localhost:3004';

router.use('/api/cart', createProxyMiddleware({
  target: CART_SERVICE_URL,
  changeOrigin: true,
  pathRewrite: { '^/api/cart': '/api/cart' }
}));

router.use('/api/payments', createProxyMiddleware({
  target: PAYMENT_SERVICE_URL,
  changeOrigin: true,
  pathRewrite: { '^/api/payments': '/api/payments' }
}));

router.use('/api/orders', createProxyMiddleware({
  target: ORDER_SERVICE_URL,
  changeOrigin: true,
  pathRewrite: { '^/api/orders': '/api/orders' }
}));

router.use('/api/reports', createProxyMiddleware({
  target: REPORTING_SERVICE_URL,
  changeOrigin: true,
  pathRewrite: { '^/api/reports': '/api/reports' }
}));

export { router as proxyRoutes };
