import express from 'express';
import helmet from 'helmet';
import cors from 'cors';
import { proxyRoutes } from './routes/proxy';
import { requestLogger } from './middleware/logging';

const app = express();
const PORT = process.env.PORT || 3000;

app.use(helmet());
app.use(cors());
app.use(express.json());
app.use(requestLogger);

app.get('/health', (_req, res) => {
  res.status(200).json({ status: 'healthy', service: 'gateway-service' });
});

app.use('/', proxyRoutes);

app.listen(PORT, () => {
  console.log(`Gateway service running on port ${PORT}`);
});

export default app;
