import express from 'express';
import helmet from 'helmet';
import cors from 'cors';
import { reportRoutes } from './routes/reports';

const app = express();
const PORT = process.env.PORT || 3000;

app.use(helmet());
app.use(cors());
app.use(express.json());

app.get('/health', (_req, res) => {
  res.status(200).json({ status: 'healthy', service: 'reporting-service' });
});

app.use('/api/reports', reportRoutes);

app.listen(PORT, () => {
  console.log(`Reporting service running on port ${PORT}`);
});

export default app;
