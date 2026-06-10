namespace OrderService.Services;

public interface ICartClient
{
    Task<IEnumerable<CartItemDto>?> GetCartItemsAsync(string userId);
    Task ClearCartAsync(string userId);
}

public class CartClient : ICartClient
{
    private readonly HttpClient _httpClient;
    private readonly ILogger<CartClient> _logger;

    public CartClient(HttpClient httpClient, ILogger<CartClient> logger)
    {
        _httpClient = httpClient;
        _logger = logger;
    }

    public async Task<IEnumerable<CartItemDto>?> GetCartItemsAsync(string userId)
    {
        var response = await _httpClient.GetAsync($"/api/cart/{userId}");

        if (!response.IsSuccessStatusCode)
        {
            _logger.LogWarning("Failed to retrieve cart for user {UserId}", userId);
            return null;
        }

        var cart = await response.Content.ReadFromJsonAsync<CartResponse>();
        return cart?.Items;
    }

    public async Task ClearCartAsync(string userId)
    {
        await _httpClient.DeleteAsync($"/api/cart/{userId}");
    }
}

public record CartItemDto
{
    public string ProductId { get; init; } = string.Empty;
    public string ProductName { get; init; } = string.Empty;
    public int Quantity { get; init; }
    public decimal UnitPrice { get; init; }
}

internal record CartResponse
{
    public string UserId { get; init; } = string.Empty;
    public IEnumerable<CartItemDto> Items { get; init; } = Enumerable.Empty<CartItemDto>();
}
