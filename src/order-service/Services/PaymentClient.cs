namespace OrderService.Services;

public interface IPaymentClient
{
    Task<PaymentResult> ProcessPaymentAsync(Guid orderId, decimal amount, string paymentMethod);
    Task RefundPaymentAsync(Guid paymentId);
}

public class PaymentClient : IPaymentClient
{
    private readonly HttpClient _httpClient;
    private readonly ILogger<PaymentClient> _logger;

    public PaymentClient(HttpClient httpClient, ILogger<PaymentClient> logger)
    {
        _httpClient = httpClient;
        _logger = logger;
    }

    public async Task<PaymentResult> ProcessPaymentAsync(Guid orderId, decimal amount, string paymentMethod)
    {
        var request = new
        {
            orderId,
            amount,
            currency = "USD",
            paymentMethod
        };

        var response = await _httpClient.PostAsJsonAsync("/api/payments", request);

        if (response.IsSuccessStatusCode)
        {
            var result = await response.Content.ReadFromJsonAsync<PaymentApiResponse>();
            return new PaymentResult
            {
                Success = true,
                PaymentId = result?.PaymentId ?? Guid.Empty
            };
        }

        _logger.LogWarning("Payment failed for order {OrderId}: {StatusCode}", orderId, response.StatusCode);
        return new PaymentResult { Success = false };
    }

    public async Task RefundPaymentAsync(Guid paymentId)
    {
        await _httpClient.PostAsync($"/api/payments/{paymentId}/refund", null);
    }
}

public record PaymentResult
{
    public bool Success { get; init; }
    public Guid PaymentId { get; init; }
}

internal record PaymentApiResponse
{
    public Guid PaymentId { get; init; }
    public string Status { get; init; } = string.Empty;
}
