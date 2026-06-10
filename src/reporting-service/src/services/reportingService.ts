import { CosmosClient, Container } from '@azure/cosmos';
import { DefaultAzureCredential } from '@azure/identity';
import * as sql from 'mssql';

export class ReportingService {
  private cosmosContainer: Container;
  private sqlPool: sql.ConnectionPool | null = null;

  constructor() {
    const endpoint = process.env.COSMOS_DB_ENDPOINT!;
    const credential = new DefaultAzureCredential();
    const client = new CosmosClient({ endpoint, aadCredentials: credential });
    const database = client.database(process.env.COSMOS_DB_DATABASE || 'ecommerce');
    this.cosmosContainer = database.container(process.env.COSMOS_DB_CONTAINER || 'reports');
  }

  private async getSqlPool(): Promise<sql.ConnectionPool> {
    if (!this.sqlPool) {
      this.sqlPool = await sql.connect(process.env.SQL_CONNECTION_STRING!);
    }
    return this.sqlPool;
  }

  async getOrderSummary(): Promise<object> {
    const pool = await this.getSqlPool();
    const result = await pool.request().query(`
      SELECT
        COUNT(*) as totalOrders,
        SUM(totalAmount) as totalRevenue,
        AVG(totalAmount) as averageOrderValue,
        COUNT(CASE WHEN status = 'Completed' THEN 1 END) as completedOrders,
        COUNT(CASE WHEN status = 'Pending' THEN 1 END) as pendingOrders
      FROM orders.dbo.Orders
    `);
    return result.recordset[0];
  }

  async getDailyOrderReport(date: string): Promise<object> {
    const pool = await this.getSqlPool();
    const result = await pool.request()
      .input('date', sql.Date, date)
      .query(`
        SELECT
          CAST(createdAt AS DATE) as orderDate,
          COUNT(*) as orderCount,
          SUM(totalAmount) as dailyRevenue
        FROM orders.dbo.Orders
        WHERE CAST(createdAt AS DATE) = @date
        GROUP BY CAST(createdAt AS DATE)
      `);
    return result.recordset[0] || { orderDate: date, orderCount: 0, dailyRevenue: 0 };
  }

  async getPaymentSummary(): Promise<object> {
    const pool = await this.getSqlPool();
    const result = await pool.request().query(`
      SELECT
        COUNT(*) as totalPayments,
        SUM(amount) as totalProcessed,
        COUNT(CASE WHEN status = 'Succeeded' THEN 1 END) as successfulPayments,
        COUNT(CASE WHEN status = 'Failed' THEN 1 END) as failedPayments
      FROM payments.dbo.Payments
    `);
    return result.recordset[0];
  }

  async getActiveCartsReport(): Promise<object> {
    const querySpec = {
      query: 'SELECT VALUE COUNT(1) FROM c WHERE ARRAY_LENGTH(c.items) > 0'
    };
    const { resources } = await this.cosmosContainer.items.query(querySpec).fetchAll();
    return {
      activeCartCount: resources[0] || 0,
      reportGeneratedAt: new Date().toISOString()
    };
  }
}
