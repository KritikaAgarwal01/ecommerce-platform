using PaymentService.Models;

namespace PaymentService.Services;

public interface IPaymentProcessor
{
    Task<PaymentResponse> ProcessAsync(PaymentRequest request);
    Task<PaymentResponse> RefundAsync(Guid paymentId);
}

public class PaymentProcessor : IPaymentProcessor
{
    private readonly PaymentDbContext _dbContext;
    private readonly ILogger<PaymentProcessor> _logger;

    public PaymentProcessor(PaymentDbContext dbContext, ILogger<PaymentProcessor> logger)
    {
        _dbContext = dbContext;
        _logger = logger;
    }

    public async Task<PaymentResponse> ProcessAsync(PaymentRequest request)
    {
        var payment = new Payment
        {
            Id = Guid.NewGuid(),
            OrderId = request.OrderId,
            Amount = request.Amount,
            Currency = request.Currency,
            PaymentMethod = request.PaymentMethod,
            Status = PaymentStatus.Processing,
            CreatedAt = DateTime.UtcNow
        };

        _dbContext.Payments.Add(payment);

        // Simulate gateway integration point
        // In production, this would call Stripe, Adyen, or similar gateway
        var gatewayResult = await SimulateGatewayCallAsync(payment);

        payment.Status = gatewayResult.Success ? PaymentStatus.Succeeded : PaymentStatus.Failed;
        payment.GatewayTransactionId = gatewayResult.TransactionId;
        payment.FailureReason = gatewayResult.FailureReason;
        payment.CompletedAt = DateTime.UtcNow;

        await _dbContext.SaveChangesAsync();

        _logger.LogInformation("Payment {PaymentId} for order {OrderId} completed with status {Status}",
            payment.Id, payment.OrderId, payment.Status);

        return new PaymentResponse
        {
            PaymentId = payment.Id,
            Status = payment.Status,
            GatewayTransactionId = payment.GatewayTransactionId,
            Message = payment.Status == PaymentStatus.Succeeded
                ? "Payment processed successfully"
                : $"Payment failed: {payment.FailureReason}"
        };
    }

    public async Task<PaymentResponse> RefundAsync(Guid paymentId)
    {
        var payment = await _dbContext.Payments.FindAsync(paymentId);
        if (payment is null)
        {
            return new PaymentResponse
            {
                PaymentId = paymentId,
                Status = PaymentStatus.Failed,
                Message = "Payment not found"
            };
        }

        payment.Status = PaymentStatus.Refunded;
        payment.CompletedAt = DateTime.UtcNow;
        await _dbContext.SaveChangesAsync();

        _logger.LogInformation("Payment {PaymentId} refunded", paymentId);

        return new PaymentResponse
        {
            PaymentId = payment.Id,
            Status = PaymentStatus.Refunded,
            GatewayTransactionId = payment.GatewayTransactionId,
            Message = "Refund processed successfully"
        };
    }

    private Task<GatewayResult> SimulateGatewayCallAsync(Payment payment)
    {
        // Placeholder for external payment gateway integration
        // Replace with actual gateway SDK calls (Stripe, Adyen, etc.)
        return Task.FromResult(new GatewayResult
        {
            Success = true,
            TransactionId = $"txn_{Guid.NewGuid():N}"
        });
    }

    private record GatewayResult
    {
        public bool Success { get; init; }
        public string? TransactionId { get; init; }
        public string? FailureReason { get; init; }
    }
}
