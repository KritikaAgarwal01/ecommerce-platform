import express from 'express';
import helmet from 'helmet';
import cors from 'cors';
import { cartRoutes } from './routes/cart';

const app = express();
const PORT = process.env.PORT || 3000;

app.use(helmet());
app.use(cors());
app.use(express.json());

app.get('/health', (_req, res) => {
  res.status(200).json({ status: 'healthy', service: 'cart-service' });
});

app.use('/api/cart', cartRoutes);

app.listen(PORT, () => {
  console.log(`Cart service running on port ${PORT}`);
});

export default app;
